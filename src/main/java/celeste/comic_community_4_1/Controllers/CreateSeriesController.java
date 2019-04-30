package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.SeriesComparator;
import celeste.comic_community_4_1.miscellaneous.SeriesData;
import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class CreateSeriesController {

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

    @GetMapping("/createSeries")
    public String mainPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);
        model.addAttribute("genreList", ComicGenre.GENRE);
        return "createSeries";

    }

    @PostMapping("/createSeriesForm")
    public String createSeriesForm(ModelMap model, HttpServletRequest request,
                                   @RequestParam(value = "title") String title,
                                   @RequestParam(value = "description", required = false) String description,
                                   @RequestParam(value = "genre1", required = false) String genre1,
                                   @RequestParam(value = "genre2", required = false) String genre2,
                                   @RequestParam(value = "wiki") String wiki) throws Exception {

        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);


        Series newSeries = new Series();
        if (description != null) {
            newSeries.setDescription(description);
        }
        newSeries.setPrimaryGenre(genre1);
        newSeries.setSecondaryGenre(genre2);
        newSeries.setSeriesName(title);
        newSeries.setPublicEditing(wiki.equals("Yes") ? true : false);
        newSeries.setUser(user);

        String coverPath = "src/main/resources/static/images/samplePost/default-upload.png";
        BufferedImage bImage = ImageIO.read(new File(coverPath));
        String base64 = ThumbnailConverter.toBase64(ThumbnailConverter.convertSquare(bImage), "png");
        newSeries.setCover(base64);

        seriesRepository.save(newSeries);

        // Profile Info
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
            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
            boolean owner = series.getUser().getUsername().equals(username);

            seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
        }
        model.addAttribute("seriesDataList", seriesDataList);

        return "profile_series";
    }


}

