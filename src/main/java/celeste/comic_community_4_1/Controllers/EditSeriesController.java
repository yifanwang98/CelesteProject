package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.SeriesComparator;
import celeste.comic_community_4_1.miscellaneous.SeriesData;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class EditSeriesController {


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

    @GetMapping("/editSeries")
    public String goToEditSeries(@RequestParam(value = "id") long seriesId,
                                 ModelMap model,
                                 HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        boolean exists = seriesRepository.existsSeriesBySeriesID(seriesId);
        Series seriesToBeEdited = null;
        if (exists) {
            seriesToBeEdited = seriesRepository.findSeriesBySeriesID(seriesId);
            if (!seriesToBeEdited.getUser().getUsername().equals(username)) {
                exists = false;
            }
        }
        if (!exists) {
            model.addAttribute("profileOwner", user);
            model.addAttribute("isOthersProfile", false);

            // Get Following & Followers
            model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(user));
            model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(user));

            //All the post by this user
            model.addAttribute("postsCount", postRepository.countPostByUser(user));
            model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
            model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(user));
            model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

            // Series List
            List<Series> seriesList = seriesRepository.findByUser(user);
            Collections.sort(seriesList, new SeriesComparator());
            List<SeriesData> seriesDataList = new ArrayList<>();
            for (Series series : seriesList) {
                List<String> tags = new ArrayList<>();
                seriesDataList.add(new SeriesData(series, tags,
                        seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series),
                        seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user),
                        series.getUser().getUsername().equals(user.getUsername())));
            }
            model.addAttribute("seriesDataList", seriesDataList);
            return "view_profile_series";
        }

        // Series Info
        model.addAttribute("seriesToBeEdited", seriesToBeEdited);
        model.addAttribute("genreList", ComicGenre.GENRE);
        return "editSeries";
    }

}

