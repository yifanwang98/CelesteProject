package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
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

        String avatarPath = "src/main/resources/static/images/default-avatar.png";
        File x = new File(avatarPath);
        BufferedImage bImage = ImageIO.read(x);

        String base64 = ThumbnailConverter.toBase64(ThumbnailConverter.convert(bImage), "png");
        newUser.setAvatar(base64);
        userRepository.save(newUser);

        List<Post> postlist = postRepository.findByUser(newUser);
        //All the post by this user's follows
            /*List<Follow> followlist = followRepository.findByFollowIndentityUserone(newUser);
            for (int i = 0; i < followlist.size(); i++) {
                postlist.addAll(postRepository.findByUser(followlist.get(i).getFollowIndentity().getUser2()));
            }*/

        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
            /*for (int i = 0; i < postlist.size(); i++) {
                ArrayList<String> list = new ArrayList<String>();
                for (int j = 0; j < postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).size(); j++) {
                    list.add(postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).get(j).getPostIndentity().getWork().getContent());
                }
                imgsForeachPost.put(postlist.get(i).getPostID(), list);
            }
*/

        model.addAttribute("User", newUser);
        model.addAttribute("postlist", postlist);
        model.addAttribute("imgsForeachPost", imgsForeachPost);


        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(newUser);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(newUser);
        model.addAttribute("followers", d.size());

        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);

        return "home";
    }

}

