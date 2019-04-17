package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.AnalysisData;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.AnalysisRepository;
import celeste.comic_community_4_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AnalysisController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    AnalysisRepository analysisRepository;

    @GetMapping("/profile_analysis")
    public String getAnalysis(ModelMap model, HttpServletRequest request) throws Exception {
        // Find Current User
        String username = (String) request.getSession().getAttribute("username");
        User founduser = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", founduser);

        // Analysis data
        AnalysisData analysisData = new AnalysisData();

        // Get Profit
        analysisData.setProfit(new double[4]);

        // Get Contribution
        analysisData.setContribution(new long[4]);

        // Get Views
        analysisData.setView(new long[4]);

        // Get Subscribers
        analysisData.setSubscriber(new long[4]);

        model.addAttribute("analysisData", analysisData);
        return "/profile_analysis";
    }

}