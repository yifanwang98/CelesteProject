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
import java.util.*;

@Controller
public class SinglePostController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostContentRepository postContentRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    SeriesContentRepository seriesContentRepository;

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
    public String singlePost(@RequestParam(value = "id") long postId,
                             ModelMap model, HttpServletRequest request) throws Exception {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            return "index";
        }
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            request.getSession().removeAttribute("username");
            request.getSession().removeAttribute("postDraft");
            request.getSession().removeAttribute("discoverList");
            request.getSession().removeAttribute("mainPageList");
            request.getSession().removeAttribute("discoverListIndex");
            request.getSession().removeAttribute("mainPageListIndex");
            return "index";
        }
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if ((user.getBlockedSince().after(Notification.getDaysBefore(3)) && user.getMembership().equals("none")) ||
                    (user.getBlockedSince().after(Notification.getDaysBefore(1)) && user.getMembership().equals("1"))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                request.getSession().removeAttribute("discoverList");
                request.getSession().removeAttribute("mainPageList");
                request.getSession().removeAttribute("discoverListIndex");
                request.getSession().removeAttribute("mainPageListIndex");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

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

        Post originalPost = postToDisplay;
        if (postToDisplay.isRepost()) {
            originalPost = postRepository.findPostByPostID(postToDisplay.getOriginalPostID());
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
                Post originalPost1 = postRepository.findPostByPostID(postToDisplay.getOriginalPostID());
                pa.setPost(originalPost1);
            } else {
                pa.setPost(postToDisplay);
            }
            pa.setUser(user);
            pa.setViewedAt(today);
            postAnalysisRepository.save(pa);
        }

        Set<Series> fromSeries = new HashSet<>();
        for (int j = 0; j < temp.size(); j++) {
            Work work = temp.get(j).getPostIndentity().getWork();
            List<SeriesContent> seriesContents = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
            for (SeriesContent content : seriesContents) {
                fromSeries.add(content.getSeriesContentIndentity().getSeries());
            }
        }
        model.addAttribute("fromSeries", fromSeries);

        // Tag
        List<String> postTags = new ArrayList<>();
        List<PostTag> postTagList = postTagRepository.findPostTagByPost(originalPost);
        for (PostTag tag : postTagList) {
            postTags.add(tag.getTag());
        }
        model.addAttribute("postTags", postTags);

        if (user.getMembership().equalsIgnoreCase("None")
                && !originalPost.getUser().getUsername().equals(user.getUsername())) {
            for (Series series : fromSeries) {
                List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentitySeriesOrderByCreatedAtAsc(series);
                for (int i = 0; i < seriesContentList.size(); i++) {
                    for (int j = 0; j < temp.size(); j++) {
                        long workid = temp.get(j).getPostIndentity().getWork().getWorkID();
                        if (seriesContentList.get(i).getSeriesContentIndentity().getWork().getWorkID() == workid) {
                            if (i < 3) {
                                break;
                            } else {
                                return "singlePost_Error";
                            }
                        }
                    }
                }
            }
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
        Comment newComment = new Comment();
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
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            return "index";
        }
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            request.getSession().removeAttribute("username");
            request.getSession().removeAttribute("postDraft");
            request.getSession().removeAttribute("discoverList");
            request.getSession().removeAttribute("mainPageList");
            request.getSession().removeAttribute("discoverListIndex");
            request.getSession().removeAttribute("mainPageListIndex");
            return "index";
        }
        Like newLike = new Like();
        PostIndentity newPi = new PostIndentity();
        newPi.setUser(user);
        newPi.setPost(postRepository.findById(postID).get());
        if (likeRepository.findById(newPi).isPresent()) {
            likeRepository.delete(likeRepository.findById(newPi).get());
            return "Unlike Success!";
        } else {
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
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            return "index";
        }
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            request.getSession().removeAttribute("username");
            request.getSession().removeAttribute("postDraft");
            request.getSession().removeAttribute("discoverList");
            request.getSession().removeAttribute("mainPageList");
            request.getSession().removeAttribute("discoverListIndex");
            request.getSession().removeAttribute("mainPageListIndex");
            return "index";
        }
        Star newStar = new Star();
        PostIndentity newPi = new PostIndentity();
        newPi.setUser(user);
        newPi.setPost(postRepository.findById(postID).get());
        if (starRepository.findById(newPi).isPresent()) {
            starRepository.delete(starRepository.findById(newPi).get());
            return "Unstar Success!";
        } else {
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
        return temp.get(index).getPostIndentity().getWork().getContent();

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

        Post currentPost = postRepository.findPostByPostID(postID);

        Post newRepost = new Post();
        newRepost.setOriginalPostID(currentPost.getOriginalPostID());
        newRepost.setRepost(true);
        newRepost.setUser(user);
        newRepost.setPostComment(comment.trim());
        postRepository.save(newRepost);
        return "Repost Success!";
    }

    @ResponseBody
    @PostMapping("/deleteComment")
    public String deleteComment(@RequestParam(value = "postID") long postID,
                                @RequestParam(value = "commentID") long commentID,
                                ModelMap model, HttpServletRequest request) throws Exception {
//        if (request.getSession().getAttribute("username") == null) {
//            return "index";
//        }
//
//        // Session User
//        String username = (String) request.getSession().getAttribute("username");
//        User user = userRepository.findById(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Post post = postRepository.findPostByPostID(postID);
        if (post == null)
            return "The post does not exist";

        Comment comment = commentRepository.findCommentByCommentID(commentID);
        if (comment == null)
            return "The comment does not exist";

        commentRepository.delete(comment);

        return "Success";
    }


}

