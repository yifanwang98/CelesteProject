package celeste.comic_community_4_1.Controllers;


import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.DrawingSaving;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.DrawingSavingRepository;
import celeste.comic_community_4_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class DrawingController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DrawingSavingRepository drawingSavingRepository;

    @GetMapping("/drawingInstruction")
    public String goToDrawingInstruction(ModelMap model,
                                         HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getMembership().equals("None")) {
                if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                    return "blocked";
                }
            } else {
                if (user.getBlockedSince().after(Notification.getDaysBefore(1))) {
                    return "blocked";
                }
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

        model.addAttribute("User", user);


        return "drawingInstruction";
    }

    @GetMapping("/drawing")
    public String goTodrawing(ModelMap model,
                              HttpServletRequest request) throws Exception {

        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getMembership().equals("None")) {
                if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                    return "blocked";
                }
            } else {
                if (user.getBlockedSince().after(Notification.getDaysBefore(1))) {
                    return "blocked";
                }
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

        model.addAttribute("User", user);

        return "drawing";
    }

    @ResponseBody
    @PostMapping("/save_drawing")
    public String save_drawing(ModelMap model, HttpServletRequest request,
                                @RequestBody String data) throws Exception {

        String username = (String) request.getSession().getAttribute("username");

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<DrawingSaving> x = drawingSavingRepository.findByUserone(user);
        if(!x.isEmpty()){
            DrawingSaving exist = x.get(0);
            exist.setDrawing(data);
            drawingSavingRepository.save(exist);
            return "Your work is saved!(exist)";
        }
        else {
            DrawingSaving newdrawsave = new DrawingSaving();
            newdrawsave.setUserone(user);
            newdrawsave.setDrawing(data);
            drawingSavingRepository.save(newdrawsave);
            return "Your work is saved!(new)";
        }
    }

    @ResponseBody
    @PostMapping("/load_drawing")
    public String load_drawing(ModelMap model, HttpServletRequest request) throws Exception {

        String username = (String) request.getSession().getAttribute("username");

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<DrawingSaving> x = drawingSavingRepository.findByUserone(user);

        if(!x.isEmpty()){
            DrawingSaving exist = x.get(0);
            String drawing = exist.getDrawing();
            drawing=drawing.replaceAll("\\\\","");
            drawing=drawing.substring(10);
            drawing=drawing.substring(0,drawing.length()-2);
            return drawing;
        }
        else {
            return "You do not have any saved work!";
        }
    }

}
