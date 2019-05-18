package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/error")
    public String error(Exception ex, ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        final User user = userRepository.findUserByUsername(username);

        if (user == null) {
            request.getSession().removeAttribute("username");
            request.getSession().removeAttribute("postDraft");
            model.addAttribute("userNull", true);
            return "index";
        }
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                model.addAttribute("userNull", true);
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

        model.addAttribute("userNull", false);
        model.addAttribute("user", user);

        ex.printStackTrace();
        return "error";
    }

}

