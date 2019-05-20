package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.miscellaneous.HashtagResultData;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.*;
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
public class HashTagController {

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
    SeriesContentRepository seriesContentRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    SeriesTagRepository seriesTagRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @Autowired
    SearchWordsRepository searchWordsRepository;

    @GetMapping("/hashtag")
    public String hashtag(@RequestParam(value = "tag") String hashtag,
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

        List<String> postImageList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();

        List<String> seriesImageList = new ArrayList<>();
        List<Long> seriesIdList = new ArrayList<>();

        // Tag In Post
        List<PostTag> postTagList = postTagRepository.findPostTagByTag(hashtag);
        for (PostTag pt : postTagList) {
            Post post = pt.getPost();
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(post.getPostID());
            for (int j = 0; j < postContents.size(); j++) {
                Work work = postContents.get(j).getPostIndentity().getWork();
                postImageList.add(work.getThumbnail());
                postIdList.add(post.getPostID());
                break;
            }
        }
        List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagByTag(hashtag);
        for (SeriesTag st : seriesTags) {
            seriesImageList.add(st.getSeries().getCover());
            seriesIdList.add(st.getSeries().getSeriesID());
        }

        HashtagResultData hrd = new HashtagResultData(hashtag, postImageList, postIdList, seriesImageList, seriesIdList);
        model.addAttribute("hashtagResultData", hrd);


        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());

        model.addAttribute("postsCount", postRepository.findByUser(user).size());
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        model.addAttribute("displayHashTag", true);

        return "hashtag";
    }

    @GetMapping("/genre")
    public String genre(@RequestParam(value = "genre") String genre,
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
        }// Blocked User
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

        List<String> postImageList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();

        List<String> seriesImageList = new ArrayList<>();
        List<Long> seriesIdList = new ArrayList<>();

        // genre In Post
        List<Post> postList = postRepository.findDistinctByPrimaryGenreOrSecondaryGenre(genre, genre);
        for (Post post : postList) {
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(post.getPostID());
            for (int j = 0; j < postContents.size(); j++) {
                Work work = postContents.get(j).getPostIndentity().getWork();
                postImageList.add(work.getThumbnail());
                postIdList.add(post.getPostID());
                break;
            }
        }
        List<Series> seriesList = seriesRepository.findDistinctByPrimaryGenreOrSecondaryGenre(genre, genre);
        for (Series series : seriesList) {
            seriesImageList.add(series.getCover());
            seriesIdList.add(series.getSeriesID());
        }

        HashtagResultData hrd = new HashtagResultData(genre, postImageList, postIdList, seriesImageList, seriesIdList);
        model.addAttribute("hashtagResultData", hrd);


        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());

        model.addAttribute("postsCount", postRepository.findByUser(user).size());
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        model.addAttribute("displayHashTag", false);

        return "hashtag";
    }

}

