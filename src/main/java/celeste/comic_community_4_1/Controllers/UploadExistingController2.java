package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.UploadPostDraft;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@Controller
public class UploadExistingController2 {


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


    @GetMapping("/uploadPost2")
    public String goToUploadPostPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }
        // Find Current User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        if (request.getSession().getAttribute("postDraft") == null) {
            request.getSession().setAttribute("postDraft", new UploadPostDraft());
        }
        model.addAttribute("postDraft", request.getSession().getAttribute("postDraft"));
        return "uploadPost2";
    }

    @PostMapping("upload_new_img")
    public String getAnalysis(@RequestParam("file") MultipartFile[] file,
                              ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }
        // Find Current User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        if (request.getSession().getAttribute("postDraft") == null) {
            request.getSession().setAttribute("postDraft", new UploadPostDraft());
        }

        UploadPostDraft upd = (UploadPostDraft) request.getSession().getAttribute("postDraft");
        for (MultipartFile f : file) {
            if (f.isEmpty())
                continue;

            String type = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf(".") + 1);
            File convFile = new File(f.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(f.getBytes());
            fos.close();

            BufferedImage bImage = ImageIO.read(convFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, type, bos);
            String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());

            upd.addImage(base64);
            convFile.delete();
        }
        request.getSession().setAttribute("postDraft", upd);
        model.addAttribute("postDraft", upd);
        return "uploadPost2";
    }

    @PostMapping("/uploadPost3")
    public String goToAddInfoPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }
        // Find Current User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        if (request.getSession().getAttribute("postDraft") == null) {
            return "uploadPost2";
        }
        model.addAttribute("postDraft", request.getSession().getAttribute("postDraft"));
        model.addAttribute("genreList", ComicGenre.GENRE);
        return "uploadPost3";
    }

}