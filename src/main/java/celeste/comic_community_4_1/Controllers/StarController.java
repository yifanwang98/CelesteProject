package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Star;
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
public class StarController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostContentRepository postContentRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

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

        // Is the same user
        model.addAttribute("isOthersProfile", false);
        model.addAttribute("profileOwner", user);

        //All Counts
        //Get Num of Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Num of Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());
        // Get Num of Post
        model.addAttribute("num_post", postRepository.findByUser(user).size());

        // Get Star List
        List<Star> starList = starRepository.findByPostIndentityUser(user);
        model.addAttribute("num_star", starList.size());
        model.addAttribute("starList", starList);

        return "profile_star";
    }

}

