package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Comment;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SinglePostController {


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


    @GetMapping("/singlePost")
    public String userprofile(@RequestParam(value = "id") long postId,
                              ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        // Find Post
        List<Post> postList = postRepository.findByPostID(postId);
        if (postList.isEmpty()) {
            model.addAttribute("error", "The post you are trying to view may have been removed.");
            model.addAttribute("hasError", true);
            return "singlePost";
        }
        Post postToDisplay = postList.get(0);
        model.addAttribute("postToDisplay", postToDisplay);

        if (postToDisplay.isRepost()) {
            Post originalPost = postRepository.findByPostID(postToDisplay.getOriginalPostID()).get(0);
            model.addAttribute("originalPost", originalPost);
        }

        List<String> postImages = new ArrayList<>();
        List<PostContent> temp = postContentRepository.findByPostIndentityPostPostID(postToDisplay.getOriginalPostID());
        for (int j = 0; j < temp.size(); j++) {
            postImages.add(temp.get(j).getPostIndentity().getWork().getThumbnail());
        }
        model.addAttribute("postImages", postImages);
        model.addAttribute("hasError", false);

        // Retrieve All Comments
        List<Comment> commentList = commentRepository.findByPost(postToDisplay);
        model.addAttribute("commentList", commentList);

        // Count
        model.addAttribute("starCount", starRepository.findByPostIndentityPost(postToDisplay).size());
        model.addAttribute("likeCount", likeRepository.findByPostIndentityPost(postToDisplay).size());
        model.addAttribute("shareCount", postRepository.countByoriginalPostIDAndIsRepost(postId, true));

        model.addAttribute("myStar", starRepository.existsStarByPostIndentityPostAndPostIndentityUser(postToDisplay, user));
        model.addAttribute("myLike", likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(postToDisplay, user));

        return "singlePost";
    }

    @ResponseBody
    @PostMapping("/uploadComment")
    public String uploadComment(@RequestParam(value = "description") String comment,
                                @RequestParam(value = "postID") Long postID,
                              ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

//        System.out.println(postID);
        Comment newComment  = new Comment();
        newComment.setPost(postRepository.findByPostID(postID).get(0));
        newComment.setUser(user);
        newComment.setContent(comment);
        commentRepository.save(newComment);
        return "Comment Success!";
//        if (request.getSession().getAttribute("username") == null) {
//            return "index";
//        }
//
//        // Session User
//        String username = (String) request.getSession().getAttribute("username");
//        User user = userRepository.findById(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//        model.addAttribute("User", user);
    }

    }

