package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.TagProcessor;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.SeriesTag;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TagController {

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
    ReportInfoRepository reportInfoRepository;

    @Autowired
    SeriesTagRepository seriesTagRepository;


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

