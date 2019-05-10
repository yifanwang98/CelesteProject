package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.SearchResult;
import celeste.comic_community_4_1.miscellaneous.SeriesData;
import celeste.comic_community_4_1.miscellaneous.UserData;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.SeriesTag;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {

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

    @GetMapping("/search")
    public String goToSearch(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());

        model.addAttribute("postsCount", postRepository.findByUser(user).size());

        model.addAttribute("genreList", ComicGenre.GENRE);
        model.addAttribute("searched", false);
        model.addAttribute("searchContent", "");

        int[] searchResultCount = {0, 0, 0, 0, 0}; // All, Hashtag, Post, Series, User
        model.addAttribute("searchResultCount", searchResultCount);

        return "search";
    }

    @PostMapping("/searchForm")
    public String search(ModelMap model, HttpServletRequest request,
                         @RequestParam(value = "genre", required = false) String[] genres,
                         @RequestParam(value = "filter", required = false) String[] filter,
                         @RequestParam(value = "searchContent") String searchContent) throws Exception {

        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);
        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());
        model.addAttribute("postsCount", postRepository.findByUser(user).size());
        model.addAttribute("genreList", ComicGenre.GENRE);

        searchContent = searchContent.trim();
        model.addAttribute("searchContent", searchContent);

        List<SearchResult> searchResultList = new ArrayList<>();
        if (filter == null) {
            model.addAttribute("searchResultList", searchResultList);
            model.addAttribute("noResult", true);
            return "search";
        }

        int[] searchResultCount = {0, 0, 0, 0, 0}; // All, Hashtag, Post, Series, User

        for (String type : filter) {
            List<SearchResult> results;
            if (type.equalsIgnoreCase("user")) {
                results = findAuthor(searchContent, user);
                searchResultList.addAll(results);
                searchResultCount[4] = results.size();
            } else if (type.equalsIgnoreCase("series")) {
                results = findSeries(searchContent, user, genres);
                searchResultList.addAll(results);
                searchResultCount[3] = results.size();
            }
        }
        searchResultCount[0] = searchResultCount[1] + searchResultCount[2]
                + searchResultCount[3] + searchResultCount[4];

        Collections.sort(searchResultList);

        model.addAttribute("searchResultList", searchResultList);
        model.addAttribute("searchResultCount", searchResultCount);
        model.addAttribute("searched", true);

        return "search";
    }

    private List<SearchResult> findAuthor(String keyword, User currentUser) {
        List<SearchResult> searchResultList = new ArrayList<>();
        // Full Search
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getUsername().toLowerCase().contains(keyword))
                continue;
            SearchResult searchResult = new SearchResult();
            searchResult.relavence = keyword.length() / user.getUsername().length();// + SearchResult.USER_WEIGHT;
            searchResult.userData = new UserData(user,
                    followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(currentUser, user));
            searchResult.resultType = "USER";
            searchResultList.add(searchResult);
        }
        return searchResultList;
    }

    private List<SearchResult> findSeries(String keyword, User currentUser, String[] genre) {
        List<SearchResult> searchResultList = new ArrayList<>();
        if (genre == null)
            return searchResultList;
        // Full Search
        List<Series> seriesList = seriesRepository.findAll();
        //System.out.println(seriesList.size());
        for (Series series : seriesList) {
            // Find Keyword
            if (!series.getSeriesName().toLowerCase().contains(keyword))
                continue;
            // Check Genre
            boolean valid = false;
            for (String g : genre) {
                if (series.getPrimaryGenre().equals(g) || series.getSecondaryGenre().equals(g)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) continue;

            SearchResult searchResult = new SearchResult();
            searchResult.relavence = keyword.length() / series.getSeriesName().length();// + SearchResult.SERIES_WEIGHT;

            List<String> tags = new ArrayList<>();
            List<SeriesTag> seriesTags = seriesTagRepository.findSeriesTagBySeries(series);
            for (SeriesTag tag : seriesTags) {
                tags.add(tag.getTag());
            }

            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, currentUser);
            boolean owner = series.getUser().getUsername().equals(currentUser.getUsername());

            searchResult.seriesData = new SeriesData(series, tags, subscriptionCount, subscribed, owner);
            searchResult.resultType = "SERIES";
            searchResultList.add(searchResult);
        }
        //System.out.println(searchResultList.size());
        return searchResultList;
    }

}

