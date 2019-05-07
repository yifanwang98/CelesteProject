package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.FollowStatus;
import celeste.comic_community_4_1.miscellaneous.PostData;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Controller
public class StarController {



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

    @GetMapping("/view_profile_star")
    public String viewProfileStar(@RequestParam(value = "user") String linkedUsername,
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

        // Get Star List
        List<Star> starList = starRepository.findByPostIndentityUser(user);

        // Organize Info
        List<PostData> postDataList = new ArrayList<>();
        List<FollowStatus> followingStatus = new ArrayList<>();
        for (int i = 0; i < starList.size(); i++) {
            // Post Content
            Post post = starList.get(i).getPostIndentity().getPost();
            Post originalPost = null;
            if (post.isRepost()) {
                originalPost = postRepository.findPostByPostID(post.getOriginalPostID());
            }
            List<PostContent> postContents = postContentRepository.
                    findByPostIndentityPostPostID(starList.get(i).getPostIndentity().getPost().getOriginalPostID());
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

            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            postDataList.add(new PostData(post, originalPost, images, shareCount, commentCount, starCount, likeCount, true, myLike, fromSeries));

            if (linkedUsername.equals(post.getUser().getUsername())) {
                followingStatus.add(FollowStatus.SELF);
            } else if (followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(profileOwner, post.getUser())) {
                followingStatus.add(FollowStatus.FOLLOWING);
            } else {
                followingStatus.add(FollowStatus.NOT_FOLLOWED);
            }
        }

        model.addAttribute("followingStatus", followingStatus);
        model.addAttribute("postDataList", postDataList);

//        model.addAttribute("starList", starList);

        return "profile_star";
    }

}

