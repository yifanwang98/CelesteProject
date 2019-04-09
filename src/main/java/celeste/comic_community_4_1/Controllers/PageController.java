package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Note;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.FollowRepository;
import celeste.comic_community_4_1.repository.NoteRepository;
import celeste.comic_community_4_1.repository.UserRepository;
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
public class PageController{


    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;



    @PostMapping("/profile_post")
    public String FirstLogin(@RequestParam(value = "username" ,required = false) String username,
                             @RequestParam(value = "password" ,required = false) String password,
                             ModelMap model, HttpServletRequest request) throws Exception{
//        System.out.println("123123");
        if(request.getSession().getAttribute("username")!=null){
            request.getSession().removeAttribute("username");
        }
//        if(username.isEmpty()){
//            model.addAttribute("errors","This username can't be empty.");
//            return "index";
//        }
//        if(password.isEmpty()){
//            model.addAttribute("errors","This password can't be empty.");
//            return "index";
//        }
        if (!userRepository.existsById(username)) {
            model.addAttribute("errors","This username doesn't exist.");
            return "index";
        }
        User founduser = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if(!password.equals(founduser.getPassword())){
            model.addAttribute("errors","Your password is incorrect.");
            return "index";
        }
        Optional<User> a = userRepository.findById(username);
        User b= a.get();
        model.addAttribute("User",b);
        //----------------------avatar---------------
//       String avatarPath = "src/main/resources/static/images/cat.jpg";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "jpg", bos );
//        byte [] data = bos.toByteArray();
//        String base64 = Base64.getEncoder().encodeToString(data);
//        b.setAvatar(base64);
//        userRepository.save(b);
        //----------------------avatar---------------

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(b);
        model.addAttribute("following",c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(b);
        model.addAttribute("followers",d.size());

//        System.out.println("Following:" + c.size() + ". Followers:" + d.size()+".");
        return "profile_post";


    }
    @GetMapping("/signup")
    public String signUp() {
        return "signUp";
    }
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

        model.addAttribute("User",newUser);

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(newUser);
        model.addAttribute("following",c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(newUser);
        model.addAttribute("followers",d.size());

//        System.out.println("Following:" + c.size() + ". Followers:" + d.size()+".");
        return "profile_post";
    }


    @ResponseBody
    @GetMapping("/checkusername")
    public String testtt(@RequestParam(value = "username" ,required = false) String username){
        Optional<User> a=  userRepository.findById(username);
//        System.out.println(a);
        if(a.isPresent()){
            return "Taken";
        }
        else {
            return "Not taken";
        }
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

