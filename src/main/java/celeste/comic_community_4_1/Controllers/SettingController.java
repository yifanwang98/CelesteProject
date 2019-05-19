package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.miscellaneous.PasswordChecker;
import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import celeste.comic_community_4_1.service.PaypalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//import celeste.comic_community_4_1.service.PaypalService;
//import com.paypal.api.payments.Payment;
//import com.paypal.base.rest.PayPalRESTException;

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
    SeriesContentRepository seriesContentRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    ReportInfoRepository reportInfoRepository;

    @Autowired
    SeriesTagRepository seriesTagRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostAnalysisRepository postAnalysisRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    DrawingSavingRepository drawingSavingRepository;


    @GetMapping("/setting")
    public String setting(ModelMap model, HttpServletRequest request) throws Exception {
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
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
        model.addAttribute("User", user);


        return "setting";

    }

    @ResponseBody
    @PostMapping("/changeSetting")
    public String changeAvatar(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "new-password", required = false) String newpassword,
                               @RequestParam("new-email") String newemail,
                               @RequestParam("new-gender") String newgender,
                               ModelMap model, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) throws Exception {
        if (!PasswordChecker.validPassword(newemail)) {
            return "Invalid character detected!";
        }

        if (!newpassword.isEmpty()) {
            newpassword = PasswordChecker.encryptSHA512(newpassword);
            userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setPassword(newpassword);
        }
        userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setEmail(newemail);
        userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setGender(newgender);
        userRepository.save(userRepository.findById((String) (request.getSession().getAttribute("username"))).get());
        if (!file.isEmpty()) {
            String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
            type = type.substring(1);
            if (!type.equals("jpg") && !type.equals("png")) {
                return "Only .png or .jpg is accepted!";
            }

            String base64 = ThumbnailConverter.toBase64Square(file);
            String username = (String) (request.getSession().getAttribute("username"));
            userRepository.findById(username).get().setAvatar(base64);
            userRepository.save(userRepository.findById(username).get());
            return "Updated Success!";
        }

        return "Updated Success!";
    }


    @ResponseBody
    @PostMapping("/tmpchangeAvatar")
    public String tmpchangeAvatar(@RequestParam("file") MultipartFile file,
                                  ModelMap model, HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) throws Exception {

        if (!file.isEmpty()) {
            String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
            type = type.substring(1);
            if (!type.equals("jpg") && !type.equals("png")) {
                return "Only .png or .jpg is accepted!";
            }

            String base64 = ThumbnailConverter.toBase64Square(file);
            return base64;

        }

        return "Error";
    }

    @ResponseBody
    @GetMapping("/upgrade_downgrade")
    public String upgrade(@RequestParam(value = "userstatus") String userstatus, ModelMap model, HttpServletRequest request) throws Exception {


        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        String returnstring;
        if (userstatus.equals("none")) {
            user.setMembership("1");
            returnstring = "Upgrade success!";
        } else {
            user.setMembership("none");
            returnstring = "Downgrade success!";
        }
        userRepository.save(user);
        return returnstring;
    }

    @Autowired
    private PaypalService paypalService;

    @GetMapping("/pay/pay_cancel")
    public String cancelPay() {
        return "paypal_medium";
    }

    @GetMapping("/pay/pay_success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             ModelMap model,
                             HttpServletRequest request) throws Exception {
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {

                // Blocked User
                user.setMembership("1");
                userRepository.save(user);
            }
        } catch (PayPalRESTException e) {
        }

        return "paypal_medium2";
    }

    @GetMapping({"upgradeAccount2"})
    public String upgrade2(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        user.setMembership("1");
        userRepository.save(user);
        model.addAttribute("User", user);

        return setting(model, request);
    }

    @GetMapping({"upgradeAccount"})
    public String upgrade(ModelMap model, HttpServletRequest request) throws Exception {
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
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

        model.addAttribute("User", user);

        return "setting_upgrade";
    }


    @GetMapping("/resetAccount")
    public String resetAccount(ModelMap model, HttpServletRequest request) throws Exception {

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

        reset(username);

        model.addAttribute("User", user);
        return "setting";
    }

    @GetMapping("/closeAccount")
    public String closeAccount(ModelMap model, HttpServletRequest request) throws Exception {
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

        reset(username);

        List<SeriesFollow> seriesFollowList = seriesFollowRepository.findSeriesFollowBySeriesFollowIndentityUser(user);
        for (SeriesFollow seriesFollow : seriesFollowList)
            seriesFollowRepository.delete(seriesFollow);

        List<Follow> followList = followRepository.findFollowByFollowIndentityUseroneOrFollowIndentityUsertwo(user, user);
        for (Follow follow : followList)
            followRepository.delete(follow);

        List<ReportInfo> reportInfoList = reportInfoRepository.findReportInfoByReporteeOrReporter(user, user);
        for (ReportInfo reportInfo : reportInfoList)
            reportInfoRepository.delete(reportInfo);

        List<Like> likeList = likeRepository.findByPostIndentityUser(user);
        for (Like like : likeList)
            likeRepository.delete(like);

        List<Star> starList = starRepository.findByPostIndentityUser(user);
        for (Star star : starList)
            starRepository.delete(star);

        List<Comment> commentList = commentRepository.findCommentByUser(user);
        for (Comment comment : commentList)
            commentRepository.delete(comment);

        userRepository.delete(user);
        return "index";
    }

    private void reset(String username) {
        User user = userRepository.findUserByUsername(username);

        List<Post> postList = postRepository.findPostByUser(user);
        for (Post post : postList) {
            // Remove Likes/Star
            List<Like> likeList = likeRepository.findByPostIndentityPost(post);
            for (Like like : likeList)
                likeRepository.delete(like);
            List<Star> starList = starRepository.findByPostIndentityPost(post);
            for (Star star : starList)
                starRepository.delete(star);
            // Remove Comments
            List<Comment> commentList = commentRepository.findByPost(post);
            for (Comment comment : commentList)
                commentRepository.delete(comment);
            // Post Analysis
            List<PostAnalysis> postAnalysisList = postAnalysisRepository.findPostAnalysisByPost(post);
            for (PostAnalysis postAnalysis : postAnalysisList)
                postAnalysisRepository.delete(postAnalysis);
            // Tag
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(post);
            for (PostTag postTag : postTagList)
                postTagRepository.delete(postTag);
            // Report
            List<ReportInfo> reportInfoList = reportInfoRepository.findReportInfoByPost(post);
            for (ReportInfo reportInfo : reportInfoList)
                reportInfoRepository.delete(reportInfo);

            // Content
            List<PostContent> postContentList = postContentRepository.findByPostIndentityPostPostID(post.getPostID());
            for (PostContent postContent : postContentList) {
                Work work = postContent.getPostIndentity().getWork();
                List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                for (SeriesContent sc : seriesContentList)
                    seriesContentRepository.delete(sc);
                workRepository.delete(work);
                postContentRepository.delete(postContent);
            }

            // Repost
            if (post.isRepost()) {
                List<Post> repostList = postRepository.findByOriginalPostIDAndIsRepost(post.getPostID(), true);
                for (Post repost : repostList)
                    postRepository.delete(repost);
            }
            postRepository.delete(post);
        }

        List<Series> seriesList = seriesRepository.findByUser(user);
        for (Series series : seriesList) {
            List<SeriesFollow> seriesFollowList = seriesFollowRepository.findBySeriesFollowIndentitySeries(series);
            for (SeriesFollow seriesFollow : seriesFollowList)
                seriesFollowRepository.delete(seriesFollow);

            List<SeriesTag> seriesTagList = seriesTagRepository.findSeriesTagBySeries(series);
            for (SeriesTag seriesTag : seriesTagList)
                seriesTagRepository.delete(seriesTag);

            List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentitySeries(series);
            for (SeriesContent seriesContent : seriesContentList)
                seriesContentRepository.delete(seriesContent);

            seriesRepository.delete(series);
        }

        drawingSavingRepository.delete(drawingSavingRepository.findDrawingSavingByUserone(user));
        List<Work> workList = workRepository.findByUserUsername(username);
        for (Work work : workList)
            workRepository.delete(work);

    }

}

