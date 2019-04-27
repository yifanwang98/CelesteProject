package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
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
            ;
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

        return "singlePost";
    }
}

