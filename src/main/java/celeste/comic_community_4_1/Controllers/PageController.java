package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.model.Note;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.NoteRepository;
import celeste.comic_community_4_1.repository.UserRepository;
import com.mysql.cj.jdbc.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Controller
public class PageController {
    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/next")
//    @ResponseBody
    public String login() {
        return "next";
    }

    @RequestMapping(value = "/xx")
    //@ResponseBody
    public String data(Model model) throws Exception{
        List<Note> notelist= noteRepository.findAll();
        model.addAttribute("notes",notelist);

        User user = new User();
        user.setUsername("abc");
        user.setPassword("123456");
        user.setGender("male");
        user.setEmail("123@aaa.com");
        user.setCreatedAt(new Date());
        /*--------This part is a sample of converting image to String----------------*/
        String avatarPath = "src/main/resources/static/EEE.png";
        File x = new File(avatarPath);
        BufferedImage bImage = ImageIO.read(x);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos );
        byte [] data = bos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);
        /*---------------------------------------------------------------------------*/
        user.setAvatar(base64);

        userRepository.save(user);
        model.addAttribute("users",user);
//        System.out.println(user.getAvatar());

        //System.out.println(TimeZone.getDefault().getID());
//        String avatarPath = "src/main/resources/static/EEE.png";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "png", bos );
//        byte [] data = bos.toByteArray();

        return "index";
    }
}

