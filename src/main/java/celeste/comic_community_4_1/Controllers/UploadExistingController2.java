package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.TagProcessor;
import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
import celeste.comic_community_4_1.miscellaneous.UploadPostDraft;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesContentIndentity;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    SeriesContentRepository seriesContentRepository;

    @Autowired
    PostTagRepository postTagRepository;


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
            upd.addImage(ThumbnailConverter.toBase64Only(f));
            upd.addThumbnail(ThumbnailConverter.toBase64Square(f, 200.0));
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
        UploadPostDraft draft = (UploadPostDraft) request.getSession().getAttribute("postDraft");
        model.addAttribute("postDraft", draft);
        model.addAttribute("genreList", ComicGenre.GENRE);

        List<Series> mySeries = seriesRepository.findByUser(user);
        model.addAttribute("mySeries", mySeries);
        return "uploadPost3";
    }

    @PostMapping("/uploadPostInfo")
    public String uploadPostInfo(@RequestParam("description") String description,
                                 @RequestParam("primaryGenre") String genre1,
                                 @RequestParam("secondaryGenre") String genre2,
                                 @RequestParam("tag1") String tag1,
                                 @RequestParam("tag2") String tag2,
                                 @RequestParam("tag3") String tag3,
                                 @RequestParam("tag4") String tag4,
                                 @RequestParam("tag5") String tag5,
                                 @RequestParam("selectedSeries") long[] selectedSeries,
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

        Set<String> set = new HashSet<>();
        if (!(tag1 == null || tag1.length() <= 0)) {
            set.add(TagProcessor.process(tag1));
        }
        if (!(tag2 == null || tag2.length() <= 0)) {
            set.add(TagProcessor.process(tag2));
        }
        if (!(tag3 == null || tag3.length() <= 0)) {
            set.add(TagProcessor.process(tag3));
        }
        if (!(tag4 == null || tag4.length() <= 0)) {
            set.add(TagProcessor.process(tag4));
        }
        if (!(tag5 == null || tag5.length() <= 0)) {
            set.add(TagProcessor.process(tag5));
        }

        if (genre1.equals(genre2) && !genre1.equals("None")) {
            genre2 = "None"; // Prevent Duplicate Genre
        }

        if (!genre1.equals("None")) {
            set.remove(TagProcessor.process(genre1));
        }
        if (!genre2.equals("None")) {
            set.remove(TagProcessor.process(genre2));
        }

        // Save To Database
        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setPostComment(description.trim());
        newPost.setOriginalPostID(newPost.getPostID());
        newPost.setPrimaryGenre(genre1);
        newPost.setSecondaryGenre(genre2);
        postRepository.save(newPost);
        newPost.setOriginalPostID(newPost.getPostID());
        postRepository.save(newPost);

        for (String tag : set) {
            PostTag newPostTag = new PostTag();
            newPostTag.setPost(newPost);
            newPostTag.setTag(tag);
            postTagRepository.save(newPostTag);
        }

        for (int i = 0; i < upd.getImageString().size(); i++) {
            Work newwork = new Work();
            newwork.setUser(user);
            newwork.setContent(upd.getImageString().get(i));
            newwork.setThumbnail(upd.getThumbnails().get(i));
            workRepository.save(newwork);

            PostContent newpostcontent = new PostContent();
            PostContentIndentity newpostcontentid = new PostContentIndentity();
            newpostcontentid.setPost(newPost);
            newpostcontentid.setWork(newwork);
            newpostcontent.setPostIndentity(newpostcontentid);
            postContentRepository.save(newpostcontent);

            if (selectedSeries.length > 0) {
                for (long seriesID : selectedSeries) {
                    Series series = seriesRepository.findSeriesBySeriesID(seriesID);

                    SeriesContentIndentity seriesContentIndentity = new SeriesContentIndentity();
                    seriesContentIndentity.setWork(newwork);
                    seriesContentIndentity.setSeries(series);

                    SeriesContent seriesContent = new SeriesContent();
                    seriesContent.setSeriesContentIndentity(seriesContentIndentity);
                    seriesContentRepository.save(seriesContent);

                    series.setLastUpdate(new Date());
                    seriesRepository.save(series);
                }
            }
        }

        request.getSession().removeAttribute("postDraft");

        return "uploadPost4";
    }

}