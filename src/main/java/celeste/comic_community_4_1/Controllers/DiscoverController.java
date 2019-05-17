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
    CommentRepository commentRepository;

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

    @GetMapping("/discover")
    public String discover(ModelMap model, HttpServletRequest request) throws Exception {
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
        List<Post> postList = postRepository.findByUser(user);
        model.addAttribute("postsCount", postList.size());

        List<SearchWords> wordsList = searchWordsRepository.findTop10ByOrderByHeatDesc();

        List<String> searchWordTagList = new ArrayList<>();
        for (SearchWords sw : wordsList) {
            searchWordTagList.add(TagProcessor.process(sw.getWord()));
        }

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

        List<Map.Entry<Post, Integer>> list = new ArrayList<>(postHashMap.entrySet());
        list.sort(Map.Entry.comparingByValue());

        postList.clear();

        for (Map.Entry<Post, Integer> entry : list) {
            postList.add(entry.getKey());
            if (postList.size() >= 50)
                break;
        }

        // Organize Info
        List<PostData> postDataList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            // Post Content
            Post post = postList.get(i);
            Post originalPost = null;
            if (post.isRepost()) {
                originalPost = postRepository.findPostByPostID(post.getOriginalPostID());
            }
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(postList.get(i).getOriginalPostID());
            List<String> images = new ArrayList<>();

            Set<Series> fromSeries = new HashSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                Work work = postContents.get(j).getPostIndentity().getWork();
                images.add(work.getThumbnail());
                List<SeriesContent> seriesContents = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                for (SeriesContent content : seriesContents) {
                    fromSeries.add(content.getSeriesContentIndentity().getSeries());
                }
            }

//            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, user);
//            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            // Tag
            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(post);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

//            postDataList.add(new PostData(post, originalPost, images, myStar, myLike, fromSeries, postTags));
            postDataList.add(new PostData(post, originalPost, images, false, false, fromSeries, postTags));
        }

        model.addAttribute("postDataList", postDataList);

        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        // Genre List
        List<GenreData> genreDataList = new ArrayList<>();
        for (String genreName : ComicGenre.GENRE) {
            if (genreName.equalsIgnoreCase("none"))
                continue;

            long total = postRepository.countPostByPrimaryGenreOrSecondaryGenre(genreName, genreName);
            total += seriesRepository.countSeriesByPrimaryGenreOrSecondaryGenre(genreName, genreName);

            String genreCover = null;
            Post tempPost = postRepository.findFirstByPrimaryGenreOrSecondaryGenre(genreName, genreName);
            if (tempPost != null) {
                PostContent pc = postContentRepository.findByPostIndentityPostPostID(tempPost.getOriginalPostID()).get(0);
                genreCover = pc.getPostIndentity().getWork().getThumbnail();
            }
            if (genreCover == null) {
                Series tempSeries = seriesRepository.findFirstBySecondaryGenreOrPrimaryGenre(genreName, genreName);
                if (tempSeries != null) {
                    genreCover = tempSeries.getCover();
                }
            }
            if (genreCover == null) {
                genreCover = ThumbnailConverter.DEFAULT_SERIES_COVER;
            }

            genreDataList.add(new GenreData(genreName, total, genreCover));
        }
        Collections.sort(genreDataList);
        model.addAttribute("genreDataList", genreDataList);

        return "discover";
    }

    private static final String ILLEGAL_CHAR = ",.?:;'\"[]\\|/{}<>`~ ";

    @ResponseBody
    @GetMapping("/checkusername")
    public String checkUsername(@RequestParam(value = "username", required = false) String username) {
        if (username != null) {
            if (username.length() < 2) {
                return "Username too short";
            }
            for (int i = 0; i < ILLEGAL_CHAR.length(); i++) {
                if (username.indexOf(ILLEGAL_CHAR.charAt(i)) >= 0) {
                    return "Illegal character: " + ILLEGAL_CHAR.charAt(i);
                }
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


}

