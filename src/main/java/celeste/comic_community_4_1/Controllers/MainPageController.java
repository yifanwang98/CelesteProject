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
public class MainPageController {

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
    SeriesContentRepository seriesContentRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @Autowired
    SearchWordsRepository searchWordsRepository;

    @Autowired
    GenreRepository genreRepository;

    @GetMapping(value = {"/mainPage", "/", "home"})
    public String mainPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getMembership().equals("None")) {
                if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                    return "blocked";
                }
            } else {
                if (user.getBlockedSince().after(Notification.getDaysBefore(1))) {
                    return "blocked";
                }
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
        //All the post by this user's follows
        List<Follow> followList = followRepository.findByFollowIndentityUserone(user);
        for (int i = 0; i < followList.size(); i++) {
            postList.addAll(postRepository.findByUser(followList.get(i).getFollowIndentity().getUser2()));
        }
        // Sort
        Collections.sort(postList, new PostComparator());

        // Organize Info
        int endIndex = 2;
        if (endIndex > postList.size())
            endIndex = postList.size();
        List<PostData> postDataList = new ArrayList<>();
        for (int i = 0; i < endIndex; i++) {
            // Post Content
            Post post = postList.get(i);
            Post originalPost = post;
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

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, user);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            // Tag
            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(originalPost);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            postDataList.add(new PostData(post, originalPost, images, myStar, myLike, fromSeries, postTags));
        }

        model.addAttribute("postDataList", postDataList);

        request.getSession().setAttribute("mainPageList", postList);
        request.getSession().setAttribute("mainPageListIndex", endIndex);

        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        return "mainPage";
    }

    @ResponseBody
    @PostMapping("/mainPageLoadMore")
    public String discoverLoadMore(ModelMap model, HttpServletRequest request) throws Exception {
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<Post> postList = (List<Post>) request.getSession().getAttribute("mainPageList");
        int startIndex = (Integer) request.getSession().getAttribute("mainPageListIndex");
        int endIndex = startIndex + 4;
        boolean hasMore = true;
        if (endIndex >= postList.size()) {
            endIndex = postList.size();
            hasMore = false;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = startIndex; i < endIndex; i++) {
            Post post = postList.get(i);
            Post originalPost = post;
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

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, user);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            // Tag
            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(originalPost);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            sb.append(MainPageHTML.generateHTML(new PostData(post, post, images, myStar, myLike, fromSeries, postTags), user));
        }

        request.getSession().setAttribute("mainPageListIndex", endIndex);
        if (hasMore)
            return "1" + sb.toString();
        return "0" + sb.toString();
    }

    @PostMapping("/home")
    public String firstLogin(@RequestParam(value = "username", required = false) String username,
                             @RequestParam(value = "password", required = false) String password,
                             ModelMap model, HttpServletRequest request) throws Exception {
        if (password != null && password.length() > 0) {
            if (!userRepository.existsById(username)) {
                model.addAttribute("errors", "This username doesn't exist.");
                return "index";
            }
            User user = userRepository.findUserByUsername(username);
            password = PasswordChecker.encryptSHA512(password);
            if (!password.equals(user.getPassword())) {
                model.addAttribute("errors", "Your password is incorrect.");
                return "index";
            }
            request.getSession().setAttribute("username", username);
            request.getSession().setAttribute("password", password);
        }

        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        return mainPage(model, request);
    }

    @PostMapping("signOut")
    public String signOut(ModelMap model, HttpServletRequest request) throws Exception {
        request.getSession().removeAttribute("username");
        request.getSession().removeAttribute("postDraft");
        return "index";
    }

    @GetMapping("index")
    public String signIn(ModelMap model, HttpServletRequest request) throws Exception {
//        List<User> userList = userRepository.findAll();
//        for (User user : userList) {
//            if(user.getPassword().length() < 30) {
//                user.setPassword(PasswordChecker.encryptSHA512(user.getPassword()));
//                userRepository.save(user);
//            }
//        }
//        //List<GenreData> genreDataList = new ArrayList<>();
//        for (String genreName : ComicGenre.GENRE) {
//            if (genreName.equalsIgnoreCase("none"))
//                continue;
//
//            long total = postRepository.countPostByPrimaryGenreOrSecondaryGenre(genreName, genreName);
//            total += seriesRepository.countSeriesByPrimaryGenreOrSecondaryGenre(genreName, genreName);
//
//            String genreCover = null;
//            if (total != 0) {
//                Post tempPost = postRepository.findFirstByPrimaryGenreOrSecondaryGenre(genreName, genreName);
//                if (tempPost != null) {
//                    genreCover = postContentRepository.findFirstByPostIndentityPostPostID(tempPost.getOriginalPostID()).getPostIndentity().getWork().getThumbnail();
//                }
//                if (genreCover == null) {
//                    Series tempSeries = seriesRepository.findFirstBySecondaryGenreOrPrimaryGenre(genreName, genreName);
//                    if (tempSeries != null) {
//                        genreCover = tempSeries.getCover();
//                    }
//                }
//            }
//
//            if (genreCover == null) {
//                genreCover = ThumbnailConverter.DEFAULT_SERIES_COVER;
//            }
//
//            //genreDataList.add(new GenreData(genreName, total, genreCover));
//            if(!genreRepository.existsGenreByGenre(genreName)) {
//                Genre genre = new Genre();
//                genre.setGenre(genreName);
//                genre.setCount(total);
//                genre.setImages(genreCover);
//                genreRepository.save(genre);
//            } else {
//            }
//        }
        return "index";
    }

    @GetMapping("/signup")
    public String signUp(ModelMap model) {
        model.addAttribute("username", "");
        model.addAttribute("email", "");
        model.addAttribute("gender", "male");
        model.addAttribute("errors", "");
        return "signUp";
    }

    @GetMapping("/forgetPassword")
    public String forgetPassword() {
        return "forgetPassword";
    }

    @PostMapping("/forgetPasswordUserInfo")
    @ResponseBody
    public String forgetPasswordUserInfo(@RequestParam(value = "username") String username,
                                         @RequestParam(value = "email") String email) {
        Optional<User> temp = userRepository.findById(username);
        if (temp.isPresent()) {
            if (temp.get().getEmail().equalsIgnoreCase(email)) {
                return "Match!";
            }
        }
        return "The username and email do not match!";
    }

    @PostMapping("/ResetPassword")
    public String ResetPassword(@RequestParam(value = "username") String username,
                                @RequestParam(value = "email") String email,
                                ModelMap model, HttpServletRequest request) {
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("errors", "");
        return "resetPassword";
    }

    @PostMapping("/SaveNewPassword")
    public String SaveNewPassword(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "email") String email,
                                  @RequestParam(value = "password") String password,
                                  ModelMap model, HttpServletRequest request) {

        if (!PasswordChecker.validPassword(username)) {
            model.addAttribute("email", email);
            model.addAttribute("errors", "Invalid character in username");
            return "resetPassword";
        }

        User user = userRepository.findUserByUsername(username);
        password = PasswordChecker.encryptSHA512(password);
        user.setPassword(password);
        userRepository.save(user);
        return "index";

    }

    @GetMapping("/backtosignin")
    public String backtosignin() {
        return "index";
    }

}

