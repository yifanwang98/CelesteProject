package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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

    @GetMapping("/view_profile")
    public String viewProfile(@RequestParam(value = "user") String linkedUsername,
                              ModelMap model, HttpServletRequest request) throws Exception {

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

        //All the post by this user
        List<Post> postlist = postRepository.findByUser(linkedUser);
        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
        for (int i = 0; i < postlist.size(); i++) {
            ArrayList<String> list = new ArrayList<String>();
            for (int j = 0; j < postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).size(); j++) {
                list.add(postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).get(j).getPostIndentity().getWork().getContent());
            }
            imgsForeachPost.put(postlist.get(i).getPostID(), list);
        }

        model.addAttribute("postlist", postlist);
        model.addAttribute("imgsForeachPost", imgsForeachPost);

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followers", d.size());
        return "profile_post";
    }

}

