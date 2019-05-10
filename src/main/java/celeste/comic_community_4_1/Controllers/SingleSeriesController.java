package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.SeriesContent;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SingleSeriesController {

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
    SeriesRepository seriesRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @Autowired
    SeriesContentRepository seriesContentRepository;

    @GetMapping("/singleSeries")
    public String singleSeries(@RequestParam(value = "id") long seriesId,
                               @RequestParam(value = "index") int index,
                               ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        // Find Series
        if (!seriesRepository.existsSeriesBySeriesID(seriesId)) {
            return "singleSeries_Error";
        }
        Series seriesToView = seriesRepository.findSeriesBySeriesID(seriesId);
        List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentitySeriesOrderByCreatedAtAsc(seriesToView);
        int selectedIndex = index >= seriesContentList.size() ? seriesContentList.size() - 1 : index;
        selectedIndex = selectedIndex < 0 ? 0 : selectedIndex;
        int endIndex = selectedIndex + 2;
        endIndex = endIndex > seriesContentList.size() - 1 ? seriesContentList.size() - 1 : endIndex;
        int fromIndex = endIndex - 5;
        fromIndex = fromIndex < 0 ? 0 : fromIndex;
        endIndex = fromIndex + 5;
        endIndex = endIndex > seriesContentList.size() - 1 ? seriesContentList.size() - 1 : endIndex;
        // SubList
        List<SeriesContent> subContent = seriesContentList.subList(fromIndex, endIndex + 1);
        List<String> thumbnails = new ArrayList<>();
        for (SeriesContent seriesContent : subContent) {
            thumbnails.add(workRepository.findWorkByWorkID(seriesContent.getSeriesContentIndentity().getWork().getWorkID()).getThumbnail());
        }
        String selectedImage = null;
        if (!thumbnails.isEmpty())
            selectedImage = workRepository.findWorkByWorkID(subContent.get(selectedIndex - fromIndex).getSeriesContentIndentity().getWork().getWorkID()).getContent();

        model.addAttribute("thumbnails", thumbnails);
        model.addAttribute("selectedImage", selectedImage);
        model.addAttribute("selectedSubIndex", selectedIndex);
        model.addAttribute("fromIndex", fromIndex);
        model.addAttribute("seriesToView", seriesToView);
        model.addAttribute("seriesTotalContent", seriesContentList.size());

        model.addAttribute("createdAt", Notification.getDateString(seriesToView.getCreatedAt()));
        model.addAttribute("lastUpdate", Notification.getDateString(seriesToView.getLastUpdate()));

        model.addAttribute("numSubscribers", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(seriesToView));

        // Other Info
        model.addAttribute("isOwner", username.equals(seriesToView.getUser().getUsername()));
        model.addAttribute("subscribed",
                seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(seriesToView, user));

        return "singleSeries";
    }
}

