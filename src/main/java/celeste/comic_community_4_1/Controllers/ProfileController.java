package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.*;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ProfileController {


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
    SeriesFollowRepository seriesFollowRepository;

    @GetMapping("/view_profile")
    public String viewProfile(@RequestParam(value = "user") String linkedUsername,
                              ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        User profileOwner = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        List<Post> postList = postRepository.findByUser(profileOwner);
        model.addAttribute("postsCount", postList.size());
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Sort
        Collections.sort(postList, new PostComparator());

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
            Set<Series> fromSeries = new TreeSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                images.add(postContents.get(j).getPostIndentity().getWork().getThumbnail());
            }

            // Count
            long shareCount = postRepository.countByoriginalPostIDAndIsRepost(post.getOriginalPostID(), true);
            long commentCount = commentRepository.countCommentByPost(post);
            long starCount = starRepository.countStarByPostIndentityPost(post);
            long likeCount = likeRepository.countLikeByPostIndentityPost(post);

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, profileOwner);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, profileOwner);

            postDataList.add(new PostData(post, originalPost, images, shareCount, commentCount, starCount, likeCount,
                    myStar, myLike, fromSeries));
        }

        model.addAttribute("postDataList", postDataList);
        return "profile_post";
    }

    @GetMapping("/view_profile_series")
    public String viewProfileSeries(@RequestParam(value = "user") String linkedUsername,
                                    ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        User profileOwner = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        model.addAttribute("postsCount", postRepository.countPostByUser(profileOwner));
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Series List
        List<Series> seriesList = seriesRepository.findByUser(profileOwner);
        Collections.sort(seriesList, new SeriesComparator());
        List<SeriesData> seriesDataList = new ArrayList<>();
        for (Series series : seriesList) {
            List<String> tags = new ArrayList<>();
            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
            boolean owner = series.getUser().getUsername().equals(user.getUsername());

            seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
        }
        model.addAttribute("seriesDataList", seriesDataList);

        return "profile_series";
    }

    @GetMapping("/view_profile_subscription")
    public String viewProfileSubscription(@RequestParam(value = "user") String linkedUsername,
                                          ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        User profileOwner = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        model.addAttribute("postsCount", postRepository.countPostByUser(profileOwner));
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Subscription List
        List<SeriesFollow> subscriptionList = seriesFollowRepository.findSeriesFollowBySeriesFollowIndentityUser(profileOwner);
        Collections.sort(subscriptionList, new SubscriptionComparator());
        List<SeriesData> seriesDataList = new ArrayList<>();
        for (SeriesFollow subscription : subscriptionList) {
            Series series = subscription.getSeriesFollowIndentity().getSeries();
            List<String> tags = new ArrayList<>();
            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
            boolean owner = series.getUser().getUsername().equals(user.getUsername());

            seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
        }
        model.addAttribute("seriesDataList", seriesDataList);

        return "profile_subscription";
    }

}

