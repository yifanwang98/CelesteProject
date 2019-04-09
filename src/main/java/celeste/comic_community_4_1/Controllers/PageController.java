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
public class PageController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;



    @PostMapping("/profile_post")
    public String FirstLogin(@RequestParam(value = "username" ,required = false) String username,
                             @RequestParam(value = "password" ,required = false) String password,
                             ModelMap model, HttpServletRequest request) {
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
//        Follow f = new Follow();
//        FollowIndentity fi = new FollowIndentity();
//        User u = new User();
//        u.setUsername("abc");
//        fi.setUser1(u);
//        f.setFollowIndentity(fi);
//        Example<Follow> ex = Example.of(f);
//        List<Follow> followTable = followRepository.findAll(ex);
//        int count = 0;
        //for(int i =0; i<)
//       System.out.println(followRepository.findAllByFollowIndentity("abc"));
        Optional<User> a = userRepository.findById(username);
        User b= a.get();
        List<Follow> c = followRepository.findByFollowIndentityUserone(b);
        System.out.println(c.size());
        model.addAttribute("follows","Your password is incorrect.");
        return "profile_post";


    }
    @GetMapping("/signUpSignIn")
    public String signUp(@RequestParam(value = "username" ,required = false) String username,
                         @RequestParam(value = "password" ,required = false) String password,
                         ModelMap model, HttpServletRequest request) {
        return "signUp";
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

