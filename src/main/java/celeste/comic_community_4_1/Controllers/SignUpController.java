package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import celeste.comic_community_4_1.repository.*;
import com.mysql.cj.jdbc.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

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
    public String signUpSignIn(@RequestParam(value = "username" ) String username,
                               @RequestParam(value = "password" ) String password,
                               @RequestParam(value = "email" ) String email,
                               @RequestParam(value = "gender" ) String gender,
                               ModelMap model, HttpServletRequest request) throws Exception{
        System.out.println(gender);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setCreatedAt(new Date());
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setGender(gender);

        String avatarPath = "src/main/resources/static/images/default-avatar.png";
        File x = new File(avatarPath);
        BufferedImage bImage = ImageIO.read(x);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos );
        byte [] data = bos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);

        newUser.setAvatar(base64);
        userRepository.save(newUser);

            List<Post> postlist = postRepository.findByUser(newUser);
            //All the post by this user's follows
            List<Follow> followlist = followRepository.findByFollowIndentityUserone(newUser);
            for (int i = 0; i < followlist.size(); i++) {
                postlist.addAll(postRepository.findByUser(followlist.get(i).getFollowIndentity().getUser2()));
            }
//        postContentRepository.findByPostIndentityPost()
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
            List<Follow> c = followRepository.findByFollowIndentityUserone(newUser);
            model.addAttribute("following", c.size());
            //Get Followers
            List<Follow> d = followRepository.findByFollowIndentityUsertwo(newUser);
            model.addAttribute("followers", d.size());

//        System.out.println("Following:" + c.size() + ". Followers:" + d.size()+".");
            request.getSession().setAttribute("username", username);
            request.getSession().setAttribute("password", password);


//        System.out.println("Following:" + c.size() + ". Followers:" + d.size()+".");
        return "home";
    }



//    @RequestMapping(value = "/next")
////    @ResponseBody
//    public String login() {
//        return "next";
//    }

//    @RequestMapping(value = "/xx")
//    //@ResponseBody
//    public String data(Model model) throws Exception{
//        List<Note> notelist= noteRepository.findAll();
//        model.addAttribute("notes",notelist);
//
//        User user = new User();
//        user.setUsername("abc");
//        user.setPassword("123456");
//        user.setGender("male");
//        user.setEmail("123@aaa.com");
//        user.setCreatedAt(new Date());
//        /*--------This part is a sample of converting image to String----------------*/
//        String avatarPath = "src/main/resources/static/EEE.png";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "png", bos );
//        byte [] data = bos.toByteArray();
//        String base64 = Base64.getEncoder().encodeToString(data);
//        /*---------------------------------------------------------------------------*/
//        user.setAvatar(base64);
//
//        userRepository.save(user);
//        model.addAttribute("users",user);
////        System.out.println(user.getAvatar());
//
//        //System.out.println(TimeZone.getDefault().getID());
////        String avatarPath = "src/main/resources/static/EEE.png";
////        File x = new File(avatarPath);
////        BufferedImage bImage = ImageIO.read(x);
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        ImageIO.write(bImage, "png", bos );
////        byte [] data = bos.toByteArray();
//
//        return "index";
//    }
}

