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
import java.util.ArrayList;
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
        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following
        List<Follow> following = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("followingList", following);
        model.addAttribute("following", following.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followers", d.size());

        if (!linkedUsername.equals(username)) {
            model.addAttribute("crossCheckedFollowList", crossCheckFollowing(user, following));
            model.addAttribute("isSubscribing", isSubscribing(user, linkedUser));
        }
        return "profile_following";
    }

    @GetMapping("/follower")
    public String viewFollowers(@RequestParam(value = "user") String linkedUsername,
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
        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following
        List<Follow> following = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("following", following.size());
        //Get Followers
        List<Follow> follower = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followerList", follower);
        model.addAttribute("followers", follower.size());

        if (!linkedUsername.equals(username)) {
            model.addAttribute("crossCheckedFollowList", crossCheckFollowing(user, follower));
            model.addAttribute("isSubscribing", isSubscribing(user, linkedUser));
        }
        return "profile_follower";
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
        model.addAttribute("isOthersProfile", false);


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

    private List<Boolean> crossCheckFollowing(User me, List<Follow> othersFollowing) {
        List<Boolean> result = new ArrayList<>();
        List<Follow> myFollowing = followRepository.findByFollowIndentityUserone(me);
        boolean found;
        for (Follow f : othersFollowing) {
            found = false;
            for (Follow myF : myFollowing) {
                if (myF.getFollowIndentity().getUser2().getUsername().equals(f.getFollowIndentity().getUser2().getUsername())) {
                    result.add(true);
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(false);
            }
        }
        return result;
    }

    private boolean isSubscribing(User me, User others) {
        List<Follow> myFollowing = followRepository.findByFollowIndentityUserone(me);
        for (Follow f : myFollowing) {
            if (f.getFollowIndentity().getUser2().getUsername().equals(others.getUsername())) {
                return true;
            }
        }
        return false;
    }
}

