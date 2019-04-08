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
public class DataController {
    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/nextt")
//    @ResponseBody
    public String Followers() {
        return "next";
    }


}

