package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.*;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class DiscoverController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostContentRepository postContentRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    SeriesContentRepository seriesContentRepository;

    @Autowired
    SearchWordsRepository searchWordsRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    GenreRepository genreRepository;


    @GetMapping("/discover")
    public String discover(ModelMap model, HttpServletRequest request) throws Exception {
        long t1 = System.currentTimeMillis();
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

        model.addAttribute("User", user);

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(user));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(user));

        //All the post by this user
        List<Post> postList;
        System.out.println("S-Time: " + ((System.currentTimeMillis() - t1) / 1000.0));
        List<SearchWords> wordsList = searchWordsRepository.findTop10ByOrderByHeatDesc();

        List<String> searchWordTagList = new ArrayList<>();
        for (SearchWords sw : wordsList) {
            searchWordTagList.add(TagProcessor.process(sw.getWord()));
        }
        System.out.println("F-Time: " + ((System.currentTimeMillis() - t1) / 1000.0));
        postList = postRepository.findPostsByCreatedAtAfterAndIsRepostAndUserIsNot(Notification.getDaysBefore(30), false, user);
        HashMap<Post, Integer> postHashMap = new HashMap<>();
        for (Post post : postList) {
            if (followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user, post.getUser()))
                continue;
            if (starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, user))
                continue;
            if (likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user))
                continue;

            int heat = 0;
            for (String tag : searchWordTagList) {
                if (postTagRepository.existsPostTagByPostAndTag(post, tag))
                    heat -= 1;
            }
            for (SearchWords sw : wordsList) {
                if (post.getPostComment().toLowerCase().contains(sw.getWord().toLowerCase()))
                    heat -= 1;
                if (post.getUser().getUsername().toLowerCase().contains(sw.getWord().toLowerCase()))
                    heat -= 1;
            }

            postHashMap.put(post, heat);
        }
        System.out.println("P1Time: " + ((System.currentTimeMillis() - t1) / 1000.0));
        List<Map.Entry<Post, Integer>> list = new ArrayList<>(postHashMap.entrySet());
        list.sort(Map.Entry.comparingByValue());

        postList.clear();

        for (Map.Entry<Post, Integer> entry : list) {
            postList.add(entry.getKey());
            if (postList.size() >= 50)
                break;
        }
        System.out.println("P2Time: " + ((System.currentTimeMillis() - t1) / 1000.0));
        // Organize Info
        int endIndex = 2;
        if (endIndex > postList.size())
            endIndex = postList.size();
        List<PostData> postDataList = new ArrayList<>();
        Post post;
        List<PostContent> postContents;
        List<SeriesContent> seriesContents;
        List<String> images;
        Work work;
        List<String> postTags;
        List<PostTag> postTagList;
        for (int i = 0; i < endIndex; i++) {
            // Post Content
            post = postList.get(i);

            postContents = postContentRepository.findByPostIndentityPostPostID(post.getOriginalPostID());
            images = new ArrayList<>();

            Set<Series> fromSeries = new HashSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                work = postContents.get(j).getPostIndentity().getWork();
                seriesContents = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                for (SeriesContent content : seriesContents) {
                    fromSeries.add(content.getSeriesContentIndentity().getSeries());
                }
                images.add(work.getThumbnail());
            }

            // Tag
            postTags = new ArrayList<>();
            postTagList = postTagRepository.findPostTagByPost(post);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            postDataList.add(new PostData(post, post, images, false, false, fromSeries, postTags));
        }

        model.addAttribute("postDataList", postDataList.subList(0, endIndex));
        request.getSession().setAttribute("discoverList", postList);
        request.getSession().setAttribute("discoverListIndex", endIndex);
        System.out.println("G-Time: " + ((System.currentTimeMillis() - t1) / 1000.0));
        // Genre List
        List<GenreData> genreDataList = new ArrayList<>();
        List<Genre> genreDataList2 = genreRepository.findAll();
        for (Genre genre : genreDataList2) {
            genreDataList.add(new GenreData(genre.getGenre(), genre.getCount(), genre.getImages()));
        }
        System.out.println("T-Time: " + ((System.currentTimeMillis() - t1) / 1000.0) + "\n");
        Collections.sort(genreDataList);
        model.addAttribute("genreDataList", genreDataList);

        System.out.println("T-Time: " + ((System.currentTimeMillis() - t1) / 1000.0) + "\n");
        return "discover";
    }

    private static final String ILLEGAL_CHAR = ",.:;'\"[]\\|/{}<>`~ ";

    @ResponseBody
    @GetMapping("/checkusername")
    public String checkUsername(@RequestParam(value = "username", required = false) String username) {
        username = username.trim();
        if (username.isEmpty()) {
            return "Username too short";
        }
        for (int i = 0; i < ILLEGAL_CHAR.length(); i++) {
            if (username.indexOf(ILLEGAL_CHAR.charAt(i)) >= 0) {
                return "Illegal character: " + ILLEGAL_CHAR.charAt(i);
            }
        }
        Optional<User> a = userRepository.findById(username);
        if (a.isPresent()) {
            return "Taken";
        } else {
            return "";
        }
    }

    @PostMapping("/signUpSignIn")
    public String signUpSignIn(@RequestParam(value = "username") String username,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "email") String email,
                               @RequestParam(value = "gender") String gender,
                               ModelMap model, HttpServletRequest request) throws Exception {

        email = email.toLowerCase();

        if (!PasswordChecker.validPassword(username)) {
            model.addAttribute("username", "");
            model.addAttribute("email", email);
            model.addAttribute("gender", gender);
            model.addAttribute("errors", "Invalid character in username");
            return "signUp";
        }
        if (!PasswordChecker.validPassword(password)) {
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("gender", gender);
            model.addAttribute("errors", "Invalid character in password");
            return "signUp";
        }
        if (!PasswordChecker.validPassword(email)) {
            model.addAttribute("username", username);
            model.addAttribute("gender", gender);
            model.addAttribute("email", "");
            model.addAttribute("errors", "Invalid character in email");
            return "signUp";
        }

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setCreatedAt(new Date());
        newUser.setPassword(password);
        newUser.setEmail(email.toLowerCase());
        newUser.setGender(gender);

        String base64 = ThumbnailConverter.DEFAULT_AVATAR;
        newUser.setAvatar(base64);
        userRepository.save(newUser);
        model.addAttribute("User", newUser);

        return discover(model, request);
    }

    @ResponseBody
    @PostMapping("/discoverLoadMore")
    public String discoverLoadMore(ModelMap model, HttpServletRequest request) throws Exception {

        List<Post> postList = (List<Post>) request.getSession().getAttribute("discoverList");
        int startIndex = (Integer) request.getSession().getAttribute("discoverListIndex");
        int endIndex = startIndex + 4;
        boolean hasMore = true;
        if (endIndex > postList.size()) {
            endIndex = postList.size();
            hasMore = false;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = startIndex; i < endIndex; i++) {
            Post post = postList.get(i);
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(post.getOriginalPostID());
            List<String> images = new ArrayList<>();

            Set<Series> fromSeries = new HashSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                Work work = postContents.get(j).getPostIndentity().getWork();
                List<SeriesContent> seriesContents = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                for (SeriesContent content : seriesContents) {
                    fromSeries.add(content.getSeriesContentIndentity().getSeries());
                }
                images.add(work.getThumbnail());
            }

            // Tag
            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(post);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            sb.append(DiscoverHTML.generateHTML(new PostData(post, post, images, false, false, fromSeries, postTags)));
        }

        request.getSession().setAttribute("discoverListIndex", endIndex);
        if (hasMore)
            return "1" + sb.toString();
        return "0" + sb.toString();
    }

}

