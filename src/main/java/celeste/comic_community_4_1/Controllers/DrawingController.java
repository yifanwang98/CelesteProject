package celeste.comic_community_4_1.Controllers;


import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.AnalysisData;
import celeste.comic_community_4_1.model.DrawingSaving;
import celeste.comic_community_4_1.model.EmbeddedClasses.DrawingSavingIndentity;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.AnalysisRepository;
import celeste.comic_community_4_1.repository.DrawingSavingRepository;
import celeste.comic_community_4_1.repository.FollowRepository;
import celeste.comic_community_4_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Controller
public class DrawingController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

//    @Autowired
//    DrawingSavingRepository drawingSavingRepository;

    @ResponseBody
    @PostMapping("/save_drawing")
    public String save_drawing(ModelMap model, HttpServletRequest request,
                                @RequestBody String data) throws Exception {

//        String username = (String) request.getSession().getAttribute("username");
//
//        User user = userRepository.findById(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//
//        Optional<DrawingSaving> x = drawingSavingRepository.findByDrawingSavingIndentityUserone(user);
//        if(x.isPresent()){
//            DrawingSaving exist = x.get();
//            DrawingSavingIndentity y = exist.getDrawingSavingIndentity();
//            y.setDrawing(data);
//            exist.setDrawingSavingIndentity(y);
//            drawingSavingRepository.save(exist);
//            return "Your work is saved!";
//        }
//        else {
//            DrawingSavingIndentity y = new DrawingSavingIndentity();
//            y.setUserone(user);
//            y.setDrawing(data);
//            DrawingSaving newSave = new DrawingSaving();
//            newSave.setDrawingSavingIndentity(y);
//            drawingSavingRepository.save(newSave);
//            return "Your work is saved!";
//        }
        return "123";
    }

    @ResponseBody
    @GetMapping("/load_drawing")
    public String load_drawing(ModelMap model, HttpServletRequest request) throws Exception {
        return "123";
    }

}
