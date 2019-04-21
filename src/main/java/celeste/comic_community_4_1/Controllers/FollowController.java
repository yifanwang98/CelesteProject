package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FollowController {


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

    @GetMapping("/following")
    public String viewFollowing(@RequestParam(value = "user") String linkedUsername,
                                ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        // Is the same user
        model.addAttribute("isOthersProfile", !username.equals(linkedUsername));
        User linkedUser = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", linkedUsername));
        model.addAttribute("profileOwner", linkedUser);

        // Get Following
        List<Follow> following = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("followingList", following);
        model.addAttribute("following", following.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followers", d.size());

        return "profile_following";
    }

    @GetMapping("/unfollow")
    public String unfollow(@RequestParam(value = "value") String toBeUnfollow,
                           ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);
        model.addAttribute("profileOwner", user);

        // Is the same user
        User linkedUser = userRepository.findById(toBeUnfollow)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", toBeUnfollow));

        List<Follow> following = followRepository.findByFollowIndentityUserone(user);
        for (Follow f : following) {
            if (f.getFollowIndentity().getUser2().getUsername().equals(toBeUnfollow)) {
                followRepository.delete(f);
                break;
            }
        }

        // Get Following
        following = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("followingList", following);
        model.addAttribute("following", following.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());
        return "profile_following";
    }
}

