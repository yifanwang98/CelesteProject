package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.PostComparator;
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

        //All the post by this user
        List<Post> postlist = postRepository.findByUser(linkedUser);
        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
        List<PostContent> temp;
        for (int i = 0; i < postlist.size(); i++) {
            ArrayList<String> list = new ArrayList<String>();
            temp = postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID());
            for (int j = 0; j < temp.size(); j++) {
                list.add(temp.get(j).getPostIndentity().getWork().getThumbnail());
            }
            imgsForeachPost.put(postlist.get(i).getPostID(), list);
        }

        Collections.sort(postlist, new PostComparator());

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

