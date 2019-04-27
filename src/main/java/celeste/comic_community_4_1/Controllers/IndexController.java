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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class IndexController {

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

    @PostMapping("/home")
    public String FirstLogin(@RequestParam(value = "username" ,required = false) String username,
                             @RequestParam(value = "password" ,required = false) String password,
                             ModelMap model, HttpServletRequest request) throws Exception{
        if (password != null && password.length() > 0) {
            if (!userRepository.existsById(username)) {
                model.addAttribute("errors", "This username doesn't exist.");
                return "index";
            }
            User user = userRepository.findById(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
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

        // Session User
        String username1 = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username1));
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

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, user);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            postDataList.add(new PostData(post, originalPost, images, shareCount, commentCount, starCount, likeCount, myStar, myLike));
        }

        model.addAttribute("postDataList", postDataList);

        request.getSession().setAttribute("username", username);
        return "mainPage";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signUp";
    }

}

