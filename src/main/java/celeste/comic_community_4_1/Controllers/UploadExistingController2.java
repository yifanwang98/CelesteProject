package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.*;
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
import java.util.*;

@Controller
public class UploadExistingController2 {

    private static final double MAX_FILE_SIZE = 2.5;


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
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
        model.addAttribute("User", user);

        UploadPostDraft draft = (UploadPostDraft) request.getSession().getAttribute("postDraft");
        if (draft == null) {
            draft = new UploadPostDraft();
        } else {
            draft.isWiki = false;
        }
        model.addAttribute("postDraft", draft);
        return "uploadPost2";
    }

    @PostMapping("/wiki")
    public String goToUploadPostPage(@RequestParam("wikiID") long seriesID,
                                     ModelMap model,
                                     HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Find Current User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
        model.addAttribute("User", user);

        UploadPostDraft draft = (UploadPostDraft) request.getSession().getAttribute("postDraft");
        if (draft == null) {
            draft = new UploadPostDraft();
        }
        draft.isWiki = true;
        draft.wikiSeriesID = seriesID;

        model.addAttribute("postDraft", draft);
        request.getSession().setAttribute("postDraft", draft);
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
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
        model.addAttribute("User", user);

        if (request.getSession().getAttribute("postDraft") == null) {
            request.getSession().setAttribute("postDraft", new UploadPostDraft());
        }

        UploadPostDraft upd = (UploadPostDraft) request.getSession().getAttribute("postDraft");
        boolean error = false;
        for (MultipartFile f : file) {
            if (f.isEmpty())
                continue;

            double size = f.getSize() * Math.pow(10, -6);
            if (size > UploadExistingController2.MAX_FILE_SIZE) {
                error = true;
            }

            if (upd.getThumbnails().size() >= 9) {
                request.getSession().setAttribute("postDraft", upd);
                model.addAttribute("postDraft", upd);
                model.addAttribute("error1", true);
                return "uploadPost2";
            }

            upd.addImage(ThumbnailConverter.toBase64Only(f));
            upd.addThumbnail(ThumbnailConverter.toBase64Square(f, 200.0));
        }
        model.addAttribute("error", error);
        model.addAttribute("error1", false);
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
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
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
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
        model.addAttribute("User", user);

        if (request.getSession().getAttribute("postDraft") == null) {
            return "uploadPost2";
        }
        UploadPostDraft draft = (UploadPostDraft) request.getSession().getAttribute("postDraft");
        model.addAttribute("postDraft", draft);
        model.addAttribute("genreList", ComicGenre.GENRE);

        List<Series> mySeries;// = seriesRepository.findByUser(user);
        if (draft.isWiki) {
            mySeries = new ArrayList<>();
            mySeries.add(seriesRepository.findSeriesBySeriesID(draft.wikiSeriesID));
        } else {
            mySeries = seriesRepository.findByUser(user);
        }
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
                                 @RequestParam(value = "selectedSeries", required = false) long[] selectedSeries,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }
        // Find Current User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
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

            if (selectedSeries != null) {
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