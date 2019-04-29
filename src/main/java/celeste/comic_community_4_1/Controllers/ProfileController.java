package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.PostComparator;
import celeste.comic_community_4_1.miscellaneous.PostData;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.PostContent;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        if (linkedUsername.equals(username)) {
            model.addAttribute("isOthersProfile", false);
        } else {
            model.addAttribute("isOthersProfile", true);
        }

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
            for (int j = 0; j < postContents.size(); j++) {
                images.add(postContents.get(j).getPostIndentity().getWork().getThumbnail());
            }

            // Count
            long shareCount = postRepository.countByoriginalPostIDAndIsRepost(post.getOriginalPostID(), true);
            long commentCount = commentRepository.countCommentByPostIndentityPost(post);
            long starCount = starRepository.findByPostIndentityPost(post).size();
            long likeCount = likeRepository.findByPostIndentityPost(post).size();

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, profileOwner);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, profileOwner);

            postDataList.add(new PostData(post, originalPost, images, shareCount, commentCount, starCount, likeCount, myStar, myLike));
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

        User founduser = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", founduser);

        // Is the same user
        model.addAttribute("isOthersProfile", !username.equals(linkedUsername));
        User linkedUser = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", linkedUsername));
        model.addAttribute("profileOwner", linkedUser);

        //All the series by this user

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followers", d.size());
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

        User founduser = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", founduser);

        // Is the same user
        model.addAttribute("isOthersProfile", !username.equals(linkedUsername));
        User linkedUser = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", linkedUsername));
        model.addAttribute("profileOwner", linkedUser);

        //All the series by this user

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followers", d.size());
        return "profile_subscription";
    }

}

