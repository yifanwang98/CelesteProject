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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Autowired
    SearchWordsRepository searchWordsRepository;

    @GetMapping("/search")
    public String goToSearch(ModelMap model, HttpServletRequest request) throws Exception {
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


        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        return "search";
    }

    @PostMapping("/topSearchForm")
    public String search(ModelMap model, HttpServletRequest request,
                         @RequestParam(value = "searchContent") String searchContent) throws Exception {
        return search(model, request, ComicGenre.GENRE, ComicGenre.FILTER, searchContent);
    }

    @GetMapping("/searchForm")
    public String search(ModelMap model, HttpServletRequest request) throws Exception {
        return search(model,
                request,
                (String[]) request.getSession().getAttribute("lastSearchGenre"),
                ComicGenre.FILTER,
                (String) request.getSession().getAttribute("lastSearch"));
    }


    @PostMapping("/searchForm")
    public String search(ModelMap model, HttpServletRequest request,
                         @RequestParam(value = "genre", required = false) String[] genres,
                         @RequestParam(value = "filter", required = false) String[] filter,
                         @RequestParam(value = "searchContent") String searchContent) throws Exception {

        searchContent = searchContent.trim();
        if (searchContent.isEmpty()) {
            return goToSearch(model, request);
        }

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
        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());
        model.addAttribute("postsCount", postRepository.findByUser(user).size());
        model.addAttribute("genreList", ComicGenre.GENRE);

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
            } else if (type.equalsIgnoreCase("hashtag")) {
                results = findHashTag(searchContent);
                searchResultList.addAll(results);
                searchResultCount[1] = results.size();
            } else {
                results = findPost(searchContent, user, genres);
                searchResultList.addAll(results);
                searchResultCount[2] = results.size();
            }
        }
        searchResultCount[0] = searchResultCount[1] + searchResultCount[2]
                + searchResultCount[3] + searchResultCount[4];

        Collections.sort(searchResultList);

        model.addAttribute("searchResultList", searchResultList);
        model.addAttribute("searchResultCount", searchResultCount);
        model.addAttribute("searched", true);


        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Add to heat
        if (searchResultCount[0] != 0) {
            HashMap<String, Double> heatResult = calculateHeat(searchContent);
            for (String key : heatResult.keySet()) {
                SearchWords sw = searchWordsRepository.findSearchWordsByWord(key);
                if (sw == null) {
                    sw = new SearchWords();
                    sw.setWord(key);
                    sw.setHeat(0);
                }
                sw.setHeat(sw.getHeat() + heatResult.get(key));
                searchWordsRepository.save(sw);
            }
        }

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        request.getSession().setAttribute("lastSearch", searchContent);
        request.getSession().setAttribute("lastSearchGenre", genres);
        return "search";
    }

    private HashMap<String, Double> calculateHeat(String content) {
        HashMap<String, Double> result = new HashMap<>();
        while (content.contains("  ")) {
            content = content.replaceAll("  ", " ");
        }
        String[] words = content.toLowerCase().split(" ");
        for (int currentLength = words.length; currentLength > 0; currentLength--) {
            for (int startIndex = 0; startIndex <= words.length - currentLength; startIndex++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < currentLength; j++) {
                    sb.append(words[j + startIndex]).append(" ");
                }
                String subword = sb.toString().trim();
                if (subword.length() < 2)
                    continue;
                if (EXCLUDED_WORDS.contains("_" + subword + "_"))
                    continue;
                double heat = Math.pow(0.9, words.length - currentLength);
                if (result.containsKey(subword)) {
                    heat += result.get(subword);
                }
                result.put(subword, heat);
            }
        }
        return result;
    }

    private static final String EXCLUDED_WORDS = "_a_an_the_is_and_or_this_that_these_those_how_what_why_who_which_";

    private List<SearchResult> findAuthor(String keyword, User currentUser) {
        List<SearchResult> searchResultList = new ArrayList<>();
        // Full Search
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getUsername().toLowerCase().contains(keyword) || user.equals(currentUser))
                continue;

            SearchResult searchResult = new SearchResult();
            searchResult.relavence = (double) keyword.length() / (double) user.getUsername().length();
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
            searchResult.relavence = (double) keyword.length() / (double) series.getSeriesName().length();

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
        return searchResultList;
    }

    private List<SearchResult> findPost(String keyword, User currentUser, String[] genre) {
        List<SearchResult> searchResultList = new ArrayList<>();
        if (genre == null)
            return searchResultList;

        keyword = TagProcessor.process(keyword).toLowerCase();
        String lowercased = keyword.toLowerCase();
        List<Post> allPosts = postRepository.findAll();
        for (Post p : allPosts) {
            // Check Genre
            boolean valid = false;
            double relevance = 0.0;
            for (String g : genre) {
                if (p.getPrimaryGenre().equals(g) || p.getSecondaryGenre().equals(g)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) continue;
            if (!p.getPostComment().toLowerCase().contains(lowercased))
                valid = false; // Either in comment or tag
            else
                relevance = (double) keyword.length() / (double) p.getPostComment().length();

            List<PostTag> postTags = postTagRepository.findPostTagByPost(p);
            for (PostTag pt : postTags) {
                if (pt.getTag().toLowerCase().contains(keyword)) {
                    valid = true;
                    relevance += (double) keyword.length() / (double) pt.getTag().length();
                    relevance /= 2;
                    break;
                }
            }
            if (!valid) continue;

            Post originalPost = null;
            if (p.isRepost()) {
                originalPost = postRepository.findPostByPostID(p.getOriginalPostID());
            }
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(p.getOriginalPostID());
            List<String> images = new ArrayList<>();

            Set<Series> fromSeries = new HashSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                Work work = postContents.get(j).getPostIndentity().getWork();
                images.add(work.getThumbnail());
                List<SeriesContent> seriesContents = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                for (SeriesContent content : seriesContents) {
                    fromSeries.add(content.getSeriesContentIndentity().getSeries());
                }
            }

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(p, currentUser);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(p, currentUser);

            // Tag
            List<String> tags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(postRepository.findPostByPostID(p.getOriginalPostID()));
            for (PostTag tag : postTagList) {
                tags.add(tag.getTag());
            }

            SearchResult searchResult = new SearchResult();
            searchResult.resultType = "POST";
            searchResult.relavence = relevance;
            searchResult.postData = new PostData(p, originalPost, images, myStar, myLike, fromSeries, tags);
            searchResultList.add(searchResult);
        }
        return searchResultList;
    }

    private List<SearchResult> findHashTag(String keyword) {
        List<SearchResult> searchResultList = new ArrayList<>();
        // Full Search
        keyword = TagProcessor.process(keyword).toLowerCase();

        Set<String> tags = new HashSet<>();
        List<SeriesTag> seriesTagList = seriesTagRepository.findAll();
        for (SeriesTag tag : seriesTagList) {
            if (tag.getTag().toLowerCase().contains(keyword))
                tags.add(tag.getTag());
        }
        List<PostTag> postTagList = postTagRepository.findAll();
        for (PostTag tag : postTagList) {
            if (tag.getTag().toLowerCase().contains(keyword))
                tags.add(tag.getTag());
        }

        for (String tag : tags) {
            SearchResult searchResult = new SearchResult();
            searchResult.resultType = "HASHTAG";
            searchResult.relavence = (double) keyword.length() / (double) tag.length();
            searchResult.hashTagData = new HashTagData(tag,
                    postTagRepository.countPostTagByTag(tag),
                    seriesTagRepository.countSeriesTagByTag(tag));
            searchResultList.add(searchResult);
        }
        return searchResultList;
    }

}

