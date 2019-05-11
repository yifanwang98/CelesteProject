package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.PostData;
import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
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
import java.util.Date;
import java.util.List;

@Controller
public class SignUpController {


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


    @GetMapping("/backtosignin")
    public String backtosignin() {
        return "index";
    }

    @PostMapping("/signUpSignIn")
    public String signUpSignIn(@RequestParam(value = "username") String username,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "email") String email,
                               @RequestParam(value = "gender") String gender,
                               ModelMap model, HttpServletRequest request) throws Exception {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setCreatedAt(new Date());
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setGender(gender);

//        String avatarPath = "src/main/resources/static/images/default-avatar.png";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);

        String base64 = ThumbnailConverter.DEFAULT_AVATAR;
        newUser.setAvatar(base64);
        userRepository.save(newUser);
        model.addAttribute("User", newUser);

        // Get Following & Followers
        model.addAttribute("following", 0);
        model.addAttribute("followers", 0);

        //All the post by this user
        model.addAttribute("postsCount", 0);

        // Organize Info
        List<PostData> postDataList = new ArrayList<>();

        model.addAttribute("postDataList", postDataList);

        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);

        return "mainPage";
    }

}

