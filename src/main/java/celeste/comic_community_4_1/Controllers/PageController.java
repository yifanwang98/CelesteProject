package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Note;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.NoteRepository;
import celeste.comic_community_4_1.repository.UserRepository;
import com.mysql.cj.jdbc.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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



    @PostMapping("/login")
    public String FirstLogin(@RequestParam(value = "username" ,required = false) String username,
                             @RequestParam(value = "password" ,required = false) String password,
                             ModelMap model, HttpServletRequest request) {
//        System.out.println("123123");
        if(request.getSession().getAttribute("username")!=null){
            request.getSession().removeAttribute("username");
        }
        if(username.isEmpty()){
            model.addAttribute("errors","This username can't be empty.");
            return "index";
        }
        if(password.isEmpty()){
            model.addAttribute("errors","This password can't be empty.");
            return "index";
        }
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

        return "profile";


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

