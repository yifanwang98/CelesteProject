package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@Controller
public class SettingController {


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

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @GetMapping("/setting")
    public String mainPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);


        return "setting";

    }
    @ResponseBody
    @PostMapping("/changeSetting")
    public String changeAvatar(@RequestParam("file") MultipartFile file,
                                 @RequestParam("new-password") String newpassword,
                                 @RequestParam("new-email") String newemail,
                                 @RequestParam("new-gender") String newgender,
                                 ModelMap model, HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) throws Exception{

        userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setPassword(newpassword);
        userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setEmail(newemail);
        userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setGender(newgender);
        userRepository.save(userRepository.findById((String) (request.getSession().getAttribute("username"))).get());
        if (!file.isEmpty()) {
            String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
            type = type.substring(1);
            if(!type.equals("jpg") && !type.equals("png")){
                return "Only .png or .jpg is accepted!";
            }

            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            BufferedImage bImage = ImageIO.read(convFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, type, bos );
            byte [] data = bos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(data);
//            System.out.println(type);
            String username = (String) (request.getSession().getAttribute("username"));
//            System.out.println(userRepository.findById(username).get().getAvatar()==base64);
            userRepository.findById(username).get().setAvatar(base64);
            userRepository.save(userRepository.findById(username).get());
            String x = "Updated Success!";
            model.addAttribute("User",userRepository.findById(username).get());
            return x;

        }

        return "Updated Success!";
    }

    @ResponseBody
    @GetMapping("/upgrade_downgrade")
    public String upgrade(@RequestParam(value = "userstatus" ) String userstatus, ModelMap model, HttpServletRequest request) throws Exception{



        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        String returnstring;
        if(userstatus.equals("none")){
            user.setMembership("1");
            returnstring =  "Upgrade success!";
        }
        else{
            user.setMembership("none");
            returnstring =  "Downgrade success!";
        }
        userRepository.save(user);
        model.addAttribute("User", user);
        return returnstring;
    }



    @GetMapping("/resetAccount")
    public String resetAccount(ModelMap model, HttpServletRequest request) throws Exception{

        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        for(Post i : postRepository.findByUser(user)){
            postRepository.delete(i);
        }
        for(Series i : seriesRepository.findByUser(user)){
            seriesRepository.delete(i);
        }
        model.addAttribute("User",user);
        return "home";
    }

    @GetMapping("/closeAccount")
    public String closeAccount(ModelMap model, HttpServletRequest request) throws Exception{
        if (request.getSession().getAttribute("username") == null) {
            return "failed";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        userRepository.delete(user);
        request.getSession().setAttribute("username",null);
        return "success";
    }

}

