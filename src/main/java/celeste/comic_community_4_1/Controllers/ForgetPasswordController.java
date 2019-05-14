package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.SHA512;
import celeste.comic_community_4_1.model.ForgetPassword;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.ForgetPasswordRepository;
import celeste.comic_community_4_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

@Controller
public class ForgetPasswordController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ForgetPasswordRepository forgetPasswordRepository;


    @ResponseBody
    @PostMapping("/forgetPassword")
    public String addSeriesTag(@RequestParam(value = "username") String username,
                               ModelMap model, HttpServletRequest request) throws Exception {

        if (!userRepository.existsUserByUsername(username)) {
            return "No such user";
        }

        User user = userRepository.findUserByUsername(username);
        String encrypted = SHA512.encrypt(username);

        if (forgetPasswordRepository.existsForgetPasswordByEncrypted(encrypted)) {
            ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByEncrypted(encrypted);
//            Calendar c = Calendar.getInstance();
//            c.add(Calendar.MINUTE, -5);
//            if(c.getTime().after(forgetPassword.getCreatedAt())) {
            forgetPasswordRepository.delete(forgetPassword);
//            } else {
//                return "";
//            }
        }

        ForgetPassword forgetPassword = new ForgetPassword();
        forgetPassword.setUser(user);
        forgetPassword.setEncrypted(encrypted);
        forgetPassword.setCreatedAt(new Date());
        forgetPasswordRepository.save(forgetPassword);


        return "An email has been sent to your email address for resetting password.";
    }

    @GetMapping("/resetForgetPassword")
    public String reset(@RequestParam(value = "id") String encrypted,
                        ModelMap model, HttpServletRequest request) throws Exception {
        ForgetPassword fp;
        if (forgetPasswordRepository.existsForgetPasswordByEncrypted(encrypted)) {
            fp = forgetPasswordRepository.findForgetPasswordByEncrypted(encrypted);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, -5);
            if (c.getTime().after(fp.getCreatedAt())) {
                forgetPasswordRepository.delete(fp);
                return "index_fp_error";
            }
            model.addAttribute("username", fp.getUser().getUsername());
        } else {
            return "index_fp_error";
        }
        return "index_fp";
    }

    @PostMapping("/resetForgetPassword")
    public String reset(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        ModelMap model, HttpServletRequest request) throws Exception {
        User user = userRepository.findUserByUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        return "index";
    }

}

