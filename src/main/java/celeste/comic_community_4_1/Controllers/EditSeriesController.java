package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.*;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Autowired
    SeriesContentRepository seriesContentRepository;

    @Autowired
    SeriesTagRepository seriesTagRepository;

    @Autowired
    GenreRepository genreRepository;

    @GetMapping("/editSeries")
    public String goToEditSeries(@RequestParam(value = "id") long seriesId,
                                 ModelMap model,
                                 HttpServletRequest request) throws Exception {
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
                List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(series);
                for (SeriesTag tag : seriesTags) {
                    tags.add(tag.getTag());
                }
                while (tags.size() < TagProcessor.MAX_TAG_PER_SERIES) {
                    tags.add(null);
                }

                long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
                boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
                boolean owner = series.getUser().getUsername().equals(user.getUsername());

                seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
            }
            model.addAttribute("seriesDataList", seriesDataList);

            return "profile_series";
        }

        // Series Info
        model.addAttribute("seriesToBeEdited", seriesToBeEdited);
        model.addAttribute("genreList", ComicGenre.GENRE);

        List<String> tags = new ArrayList<>();
        List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(seriesToBeEdited);
        for (SeriesTag tag : seriesTags) {
            tags.add(tag.getTag());
        }
        while (tags.size() < TagProcessor.MAX_TAG_PER_SERIES) {
            tags.add(null);
        }
        model.addAttribute("tags", tags);

        return "editSeries";
    }

    @ResponseBody
    @PostMapping("/tempChangeCover")
    public String tempChangeCover(@RequestParam("file") MultipartFile file,
                                  ModelMap model,
                                  HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        if (!file.isEmpty()) {
            String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);// 取文件格式后缀名
            if (!type.equals("jpg") && !type.equals("png")) {
                return "Only .png or .jpg is accepted!";
            }

            return ThumbnailConverter.toBase64Square(file);
        }
        return "error";
    }

    @PostMapping("/editSeriesInfo")
    public String editSeriesInfo(@RequestParam(value = "file") MultipartFile file,
                                 @RequestParam(value = "sid") long seriesId,
                                 @RequestParam(value = "title") String title,
                                 @RequestParam(value = "description") String description,
                                 @RequestParam(value = "genre1") String genre1,
                                 @RequestParam(value = "genre2") String genre2,
                                 @RequestParam(value = "wiki") String wiki,
                                 @RequestParam("tag1") String tag1,
                                 @RequestParam("tag2") String tag2,
                                 @RequestParam("tag3") String tag3,
                                 @RequestParam("tag4") String tag4,
                                 @RequestParam("tag5") String tag5,
                                 ModelMap model,
                                 HttpServletRequest request) throws Exception {

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
        model.addAttribute("User", user);

        if (seriesRepository.existsSeriesBySeriesID(seriesId)) {
            Set<String> set = new HashSet<>();
            if (!(tag1 == null || tag1.length() <= 0)) {
                set.add(TagProcessor.process(tag1));
            }
            if (!(tag2 == null || tag2.length() <= 0)) {
                set.add(TagProcessor.process(tag2));
            }
            if (!(tag3 == null || tag3.length() <= 0)) {
                set.add(TagProcessor.process(tag3));
            }
            if (!(tag4 == null || tag4.length() <= 0)) {
                set.add(TagProcessor.process(tag4));
            }
            if (!(tag5 == null || tag5.length() <= 0)) {
                set.add(TagProcessor.process(tag5));
            }

            if (genre1.equals(genre2) && !genre1.equals("None")) {
                genre2 = "None"; // Prevent Duplicate Genre
            }

            if (!genre1.equals("None")) {
                set.remove(TagProcessor.process(genre1));
            }
            if (!genre2.equals("None")) {
                set.remove(TagProcessor.process(genre2));
            }

            // Update Info
            Series seriesToBeEdited = seriesRepository.findSeriesBySeriesID(seriesId);

            List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(seriesToBeEdited);
            for (SeriesTag tag : seriesTags) {
                seriesTagRepository.delete(tag);
            }

            for (String tag : set) {
                SeriesTag seriesTag = new SeriesTag();
                seriesTag.setSeries(seriesToBeEdited);
                seriesTag.setTag(tag);
                seriesTagRepository.save(seriesTag);
            }

            if (!file.isEmpty())
                seriesToBeEdited.setCover(ThumbnailConverter.toBase64Square(file));
            seriesToBeEdited.setSeriesName(title);
            seriesToBeEdited.setDescription(description);
            seriesToBeEdited.setPrimaryGenre(genre1);
            seriesToBeEdited.setSecondaryGenre(genre2);
            seriesToBeEdited.setPublicEditing(wiki.equals("Yes"));
            seriesRepository.save(seriesToBeEdited);
        }

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
            List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(series);
            for (SeriesTag tag : seriesTags) {
                tags.add(tag.getTag());
            }
            while (tags.size() < TagProcessor.MAX_TAG_PER_SERIES) {
                tags.add(null);
            }

            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
            boolean owner = series.getUser().getUsername().equals(user.getUsername());

            seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
        }
        model.addAttribute("seriesDataList", seriesDataList);
        updateGenre();
        return "profile_series";
    }

    @PostMapping("/deleteSeries")
    public String deleteSeries(@RequestParam(value = "sid") long seriesId,
                               ModelMap model,
                               HttpServletRequest request) throws Exception {

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
        model.addAttribute("User", user);

        if (seriesRepository.existsSeriesBySeriesID(seriesId)) {
            Series seriesToBeDeleted = seriesRepository.findSeriesBySeriesID(seriesId);

            List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(seriesToBeDeleted);
            for (SeriesTag tag : seriesTags) {
                seriesTagRepository.delete(tag);
            }

            // Remove Subscription
            List<SeriesFollow> seriesFollowList = seriesFollowRepository.findBySeriesFollowIndentitySeries(seriesToBeDeleted);
            for (SeriesFollow seriesFollow : seriesFollowList) {
                seriesFollowRepository.delete(seriesFollow);
            }
            // Remove Content
            List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentitySeries(seriesToBeDeleted);
            for (SeriesContent seriesContent : seriesContentList) {
                seriesContentRepository.delete(seriesContent);
            }
            // Remove Series
            seriesRepository.delete(seriesToBeDeleted);
        }

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
            List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(series);
            for (SeriesTag tag : seriesTags) {
                tags.add(tag.getTag());
            }
            while (tags.size() < TagProcessor.MAX_TAG_PER_SERIES) {
                tags.add(null);
            }

            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
            boolean owner = series.getUser().getUsername().equals(user.getUsername());

            seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
        }
        model.addAttribute("seriesDataList", seriesDataList);
        updateGenre();
        return "profile_series";
    }

    private void updateGenre() {
        for (String genreName : ComicGenre.GENRE) {
            if (genreName.equalsIgnoreCase("none"))
                continue;

            long total = postRepository.countPostByPrimaryGenreOrSecondaryGenre(genreName, genreName);
            total += seriesRepository.countSeriesByPrimaryGenreOrSecondaryGenre(genreName, genreName);

            String genreCover = null;
            if (total != 0) {
                Post tempPost = postRepository.findFirstByPrimaryGenreOrSecondaryGenre(genreName, genreName);
                if (tempPost != null) {
                    genreCover = postContentRepository.findFirstByPostIndentityPostPostID(tempPost.getOriginalPostID()).getPostIndentity().getWork().getThumbnail();
                }
                if (genreCover == null) {
                    Series tempSeries = seriesRepository.findFirstBySecondaryGenreOrPrimaryGenre(genreName, genreName);
                    if (tempSeries != null) {
                        genreCover = tempSeries.getCover();
                    }
                }
            }

            if (genreCover == null) {
                genreCover = ThumbnailConverter.DEFAULT_SERIES_COVER;
            }

            Genre genre = genreRepository.findGenreByGenre(genreName);
            genre.setImages(genreCover);
            genre.setCount(total);
            genreRepository.save(genre);
        }
    }

}

