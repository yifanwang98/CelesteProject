package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class SinglePostController {


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
    CommentRepository commentRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    ReportInfoRepository reportInfoRepository;

    @Autowired
    PostAnalysisRepository postAnalysisRepository;


    @GetMapping("/singlePost")
    public String userprofile(@RequestParam(value = "id") long postId,
                              ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        // Find Post
        List<Post> postList = postRepository.findByPostID(postId);
        if (postList.isEmpty()) {
            model.addAttribute("error", "The post you are trying to view may have been removed.");
            model.addAttribute("hasError", true);
            return "singlePost";
        }
        Post postToDisplay = postList.get(0);
        model.addAttribute("postToDisplay", postToDisplay);

        if (postToDisplay.isRepost()) {
            Post originalPost = postRepository.findByPostID(postToDisplay.getOriginalPostID()).get(0);
            model.addAttribute("originalPost", originalPost);
        }

        List<String> postImages = new ArrayList<>();
        List<PostContent> temp = postContentRepository.findByPostIndentityPostPostID(postToDisplay.getOriginalPostID());
        for (int j = 0; j < temp.size(); j++) {
            postImages.add(temp.get(j).getPostIndentity().getWork().getThumbnail());
        }
        model.addAttribute("postImages", postImages);
        model.addAttribute("hasError", false);

        // Retrieve All Comments
        List<Comment> commentList = commentRepository.findByPost(postToDisplay);
        model.addAttribute("commentList", commentList);

        // Count
        model.addAttribute("starCount", starRepository.findByPostIndentityPost(postToDisplay).size());
        model.addAttribute("likeCount", likeRepository.findByPostIndentityPost(postToDisplay).size());
        model.addAttribute("shareCount", postRepository.countByoriginalPostIDAndIsRepost(postId, true));

        model.addAttribute("myStar", starRepository.existsStarByPostIndentityPostAndPostIndentityUser(postToDisplay, user));
        model.addAttribute("myLike", likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(postToDisplay, user));

        // Analysis
        Date today = Notification.getToday();
        if (!postAnalysisRepository.existsPostAnalysisByPostAndUserAndViewedAt(postToDisplay, user, today)
                && !user.getUsername().equals(postToDisplay.getUser().getUsername())) {
            PostAnalysis pa = new PostAnalysis();
            if (postToDisplay.isRepost()) {
                Post originalPost = postRepository.findPostByPostID(postToDisplay.getOriginalPostID());
                pa.setPost(originalPost);
            } else {
                pa.setPost(postToDisplay);
            }
            pa.setUser(user);
            pa.setViewedAt(today);
            postAnalysisRepository.save(pa);
        }

        return "singlePost";
    }

    @ResponseBody
    @PostMapping("/uploadComment")
    public String uploadComment(@RequestParam(value = "description") String comment,
                                @RequestParam(value = "postID") Long postID,
                              ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

//        System.out.println(postID);
        Comment newComment  = new Comment();
        newComment.setPost(postRepository.findByPostID(postID).get(0));
        newComment.setUser(user);
        newComment.setContent(comment);
        commentRepository.save(newComment);
        return "Comment Success!";
    }

    @ResponseBody
    @PostMapping("/singlepostLike")
    public String singlepostLike(@RequestParam(value = "postID") long postID,
                                ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Like newLike = new Like();
        PostIndentity newPi = new PostIndentity();
        newPi.setUser(user);
        newPi.setPost(postRepository.findById(postID).get());
        if(likeRepository.findById(newPi).isPresent()){
            likeRepository.delete(likeRepository.findById(newPi).get());
            return "Unlike Success!";
        }
        else {
            newLike.setPostIndentity(newPi);
            likeRepository.save(newLike);

//            System.out.println(postID);
            return "Like Success!";
        }

    }

    @ResponseBody
    @PostMapping("/singlepostReport")
    public String singlepostStar(@RequestParam(value = "postID") long postID,
                                 @RequestParam(value = "reason") String reason,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Post postToReport = postRepository.findPostByPostID(postID);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        if (reportInfoRepository.existsReportInfoByCreatedAtAfterAndReporteeAndReporter(
                calendar.getTime(), postToReport.getUser(), user)) {
            return "Please do not report the same user in one day.";
        }

        ReportInfo newReport = new ReportInfo();
        newReport.setPost(postToReport);
        newReport.setReportee(postToReport.getUser());
        newReport.setReporter(user);
        newReport.setReason(reason);
        reportInfoRepository.save(newReport);

        if (reportInfoRepository.countReportInfoByReporteeAndCreatedAtAfter(postToReport.getUser(), calendar.getTime()) >= 3) {
            User reportee = postToReport.getUser();
            reportee.setBlockStatus("1");
            reportee.setBlockedSince(new Date());
            userRepository.save(reportee);

            List<Post> postList = postRepository.findByUser(reportee);
            for (Post p : postList) {
                List<ReportInfo> reportInfoList = reportInfoRepository.findReportInfoByPost(p);
                if (reportInfoList.size() > 5) {
                    for (ReportInfo ri : reportInfoList) {
                        reportInfoRepository.delete(ri);
                    }
                    if (p.isRepost()) {
                        postRepository.delete(p);
                    } else {
                        List<PostContent> postContentList = postContentRepository.findByPostIndentityPostPostID(p.getPostID());
                        for (PostContent postContent : postContentList) {
                            Work work = postContent.getPostIndentity().getWork();
                            postContentRepository.delete(postContent);
                            workRepository.delete(work);
                        }
                        List<Post> repostList = postRepository.findByOriginalPostIDAndIsRepost(p.getPostID(), true);
                        for (Post repost : repostList) {
                            postRepository.delete(repost);
                        }
                        List<Like> likeList = likeRepository.findByPostIndentityPost(p);
                        for (Like like : likeList) {
                            likeRepository.delete(like);
                        }
                        List<Star> starList = starRepository.findByPostIndentityPost(p);
                        for (Star star : starList) {
                            starRepository.delete(star);
                        }
                        postRepository.delete(p);
                    }
                }
            }

        }

        return "Report has been sent. Please wait for our reply.";
    }

    @ResponseBody
    @PostMapping("/singlepostStar")
    public String singlepostReport(@RequestParam(value = "postID") long postID,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Star newStar = new Star();
        PostIndentity newPi = new PostIndentity();
        newPi.setUser(user);
        newPi.setPost(postRepository.findById(postID).get());
        if(starRepository.findById(newPi).isPresent()){
            starRepository.delete(starRepository.findById(newPi).get());
            return "Unstar Success!";
        }
        else {
            newStar.setPostIndentity(newPi);
            starRepository.save(newStar);

            System.out.println(postID);
            return "Star Success!";
        }

    }
    @ResponseBody
    @PostMapping("/getoriginalImageSrc")
    public String getoriginalImageSrc(@RequestParam(value = "postID") long postID,
                                      @RequestParam(value = "index") int index,
                                   ModelMap model, HttpServletRequest request) throws Exception {
        List<PostContent> temp = postContentRepository.findByPostIndentityPostPostID(postID);
        String src = temp.get(index).getPostIndentity().getWork().getContent();
        return src;

    }
    @ResponseBody
    @PostMapping("/singlepostRepost")
    public String singlepostRepost(@RequestParam(value = "postID") long postID,
                                   @RequestParam(value = "comment") String comment,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Post newRepost = new Post();
        newRepost.setOriginalPostID(postID);
        newRepost.setRepost(true);
        newRepost.setUser(user);
        newRepost.setPostComment(comment);
        postRepository.save(newRepost);
        return "Repost Success!";


    }

}

