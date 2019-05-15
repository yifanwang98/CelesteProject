package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
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

    @GetMapping("/setting")
    public String setting(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
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


        return "setting";

    }

    @ResponseBody
    @PostMapping("/changeSetting")
    public String changeAvatar(@RequestParam("file") MultipartFile file,
                               @RequestParam("new-password") String newpassword,
                               @RequestParam("new-email") String newemail,
                               @RequestParam("new-gender") String newgender,
                               ModelMap model, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) throws Exception {

        userRepository.findById((String) (request.getSession().getAttribute("username"))).get().setPassword(newpassword);
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
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
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

        return "setting_upgrade";
    }


    @GetMapping("/resetAccount")
    public String resetAccount(ModelMap model, HttpServletRequest request) throws Exception {

        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<Post> postList = postRepository.findByUser(user);
        for (Post post : postList) {
            if (!post.isRepost()) {
                List<PostContent> postContentList = postContentRepository.findByPostIndentityPostPostID(post.getPostID());
                for (PostContent postContent : postContentList) {
                    Work work = postContent.getPostIndentity().getWork();
                    postContentRepository.delete(postContent);
                    List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                    for (SeriesContent sc : seriesContentList)
                        seriesContentRepository.delete(sc);
                    workRepository.delete(work);
                }
                List<Post> repostList = postRepository.findByOriginalPostIDAndIsRepost(post.getPostID(), true);
                for (Post repost : repostList) {
                    postRepository.delete(repost);
                }
                List<Like> likeList = likeRepository.findByPostIndentityPost(post);
                for (Like like : likeList) {
                    likeRepository.delete(like);
                }
                List<Star> starList = starRepository.findByPostIndentityPost(post);
                for (Star star : starList) {
                    starRepository.delete(star);
                }
            }
            postRepository.delete(post);
        }

        List<Series> seriesList = seriesRepository.findByUser(user);
        for (Series i : seriesList) {
            seriesRepository.delete(i);
        }

        model.addAttribute("User", user);
        return "setting";
    }

    @GetMapping("/closeAccount")
    public String closeAccount(ModelMap model, HttpServletRequest request) throws Exception {
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        userRepository.delete(user);
        return "index";
    }

}

