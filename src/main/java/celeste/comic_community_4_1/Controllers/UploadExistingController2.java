package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
import celeste.comic_community_4_1.miscellaneous.UploadPostDraft;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.PostContent;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.model.Work;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
            String[] result = ThumbnailConverter.toBase64(f);
            upd.addImage(result[0]);
            upd.addThumbnail(result[1]);
        }
        request.getSession().setAttribute("postDraft", upd);
        model.addAttribute("postDraft", upd);
        return "uploadPost2";
    }

    @GetMapping("/deleteImage")
    public String deleteImage(@RequestParam("index") int index,
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
        upd.remove(index);
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

    @PostMapping("/uploadPostInfo")
    public String uploadPostInfo(@RequestParam("description") String description,
                                 @RequestParam("primaryGenre") String genre1,
                                 @RequestParam("secondaryGenre") String genre2,
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
            return "uploadPost2";
        }

        UploadPostDraft upd = (UploadPostDraft) request.getSession().getAttribute("postDraft");

        // Save To Database
        Post newpost = new Post();
        newpost.setUser(user);
        newpost.setPostComment(description.trim());
        newpost.setOriginalPostID(newpost.getPostID());
        postRepository.save(newpost);

        Post newpost2 = (postRepository.findById(newpost.getPostID()).get());
        newpost2.setOriginalPostID(newpost2.getPostID());
        postRepository.save(newpost2);

        for (int i = 0; i < upd.getImageString().size(); i++) {
            Work newwork = new Work();
            newwork.setUser(user);
            newwork.setContent(upd.getImageString().get(i));
            newwork.setThumbnail(upd.getThumbnails().get(i));
            workRepository.save(newwork);

            PostContent newpostcontent = new PostContent();
            PostContentIndentity newpostcontentid = new PostContentIndentity();
            newpostcontentid.setPost(newpost);
            newpostcontentid.setWork(newwork);
            newpostcontent.setPostIndentity(newpostcontentid);
            postContentRepository.save(newpostcontent);
        }

        request.getSession().setAttribute("postDraft", null);

        return "uploadPost4";
    }

}