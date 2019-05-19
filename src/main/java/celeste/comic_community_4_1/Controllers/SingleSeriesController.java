package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.miscellaneous.TagProcessor;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    SeriesTagRepository seriesTagRepository;

    @GetMapping("/singleSeries")
    public String singleSeries(@RequestParam(value = "id") long seriesId,
                               @RequestParam(value = "index") int index,
                               ModelMap model, HttpServletRequest request) throws Exception {
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
            if ((user.getBlockedSince().after(Notification.getDaysBefore(3)) && user.getMembership().equals("none")) ||
                    (user.getBlockedSince().after(Notification.getDaysBefore(1)) && user.getMembership().equals("1"))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
                request.getSession().removeAttribute("discoverList");
                request.getSession().removeAttribute("mainPageList");
                request.getSession().removeAttribute("discoverListIndex");
                request.getSession().removeAttribute("mainPageListIndex");
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }
        model.addAttribute("User", user);

        // Find Series
        if (!seriesRepository.existsSeriesBySeriesID(seriesId)) {
            return "singleSeries_Error";
        }
        Series seriesToView = seriesRepository.findSeriesBySeriesID(seriesId);
        List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentitySeriesOrderByCreatedAtAsc(seriesToView);
        int selectedIndex = index >= seriesContentList.size() ? seriesContentList.size() - 1 : index;
        selectedIndex = selectedIndex < 0 ? 0 : selectedIndex;
//        int endIndex = selectedIndex + 2;
//        endIndex = endIndex > seriesContentList.size() - 1 ? seriesContentList.size() - 1 : endIndex;
//        int fromIndex = endIndex - 5;
//        fromIndex = fromIndex < 0 ? 0 : fromIndex;
//        endIndex = fromIndex + 5;
//        endIndex = endIndex > seriesContentList.size() - 1 ? seriesContentList.size() - 1 : endIndex;
        // SubList
//        List<SeriesContent> subContent = seriesContentList.subList(fromIndex, endIndex + 1);
        int fromIndex = 0;
//        int endIndex = seriesContentList.size() - 1;
        List<SeriesContent> subContent = seriesContentList;
        List<String> thumbnails = new ArrayList<>();
        for (SeriesContent seriesContent : subContent) {
            thumbnails.add(workRepository.findWorkByWorkID(seriesContent.getSeriesContentIndentity().getWork().getWorkID()).getThumbnail());
        }
        String selectedImage = null;
        Post postContainsTheWork = null;
        if (!thumbnails.isEmpty()) {
            Work work = workRepository.findWorkByWorkID(subContent.get(selectedIndex - fromIndex).getSeriesContentIndentity().getWork().getWorkID());
            selectedImage = work.getContent();

            // Get Post info
            PostContent postContent = postContentRepository.findByPostIndentityWork(work);
            if (postContent != null)
                postContainsTheWork = postContent.getPostIndentity().getPost();
        }
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

        // Series tags
        model.addAttribute("seriesTags", seriesTagRepository.findSeriesTagBySeries(seriesToView));

        // Post Info
        model.addAttribute("postContainsTheWork", postContainsTheWork);

        return "singleSeries";
    }

    @ResponseBody
    @PostMapping("/addSeriesTag")
    public String addSeriesTag(@RequestParam(value = "id") long seriesID,
                               @RequestParam(value = "tag") String tag,
                               ModelMap model, HttpServletRequest request) throws Exception {

        if (!TagProcessor.validTag(tag)) {
            return "Invalid Tag";
        }

        String processedTag = TagProcessor.process(tag);

        if (processedTag.length() > TagProcessor.MAX_TAG_LENGTH) {
            return "Tag Exceeds Max Length(10) After Parsing";
        }

        Series series = seriesRepository.findSeriesBySeriesID(seriesID);

        if (seriesTagRepository.existsSeriesTagBySeriesAndTag(series, processedTag)) {
            return "Duplicate Tag";
        }

        // Check Genre
        if (!series.getPrimaryGenre().equals("None")) {
            if (processedTag.equals(TagProcessor.process(series.getPrimaryGenre()))) {
                return "Duplicate Genre Tag";
            }
        }
        if (!series.getSecondaryGenre().equals("None")) {
            if (processedTag.equals(TagProcessor.process(series.getSecondaryGenre()))) {
                return "Duplicate Genre Tag";
            }
        }

        SeriesTag newSeriesTag = new SeriesTag();
        newSeriesTag.setSeries(series);
        newSeriesTag.setTag(processedTag);
        seriesTagRepository.save(newSeriesTag);

        return "#" + processedTag + " Successfully Added";
    }

}

