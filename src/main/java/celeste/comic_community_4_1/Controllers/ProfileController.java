package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.*;
import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesFollowIndentity;
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
import java.util.*;

@Controller
public class ProfileController {

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
    SeriesContentRepository seriesContentRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @Autowired
    SeriesTagRepository seriesTagRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    GenreRepository genreRepository;

    @GetMapping("/view_profile")
    public String viewProfile(@RequestParam(value = "user") String linkedUsername,
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

        User profileOwner = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        List<Post> postList = postRepository.findByUser(profileOwner);
        model.addAttribute("postsCount", postList.size());
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Sort
        Collections.sort(postList, new PostComparator());

        // Organize Info
        List<PostData> postDataList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            // Post Content
            Post post = postList.get(i);
            Post originalPost = post;
            if (post.isRepost()) {
                originalPost = postRepository.findPostByPostID(post.getOriginalPostID());
            }
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(postList.get(i).getOriginalPostID());
            List<String> images = new ArrayList<>();
            Set<Series> fromSeries = new TreeSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                images.add(postContents.get(j).getPostIndentity().getWork().getThumbnail());
            }

            // Count
            long shareCount = postRepository.countByoriginalPostIDAndIsRepost(post.getOriginalPostID(), true);
            long commentCount = commentRepository.countCommentByPost(post);
            long starCount = starRepository.countStarByPostIndentityPost(post);
            long likeCount = likeRepository.countLikeByPostIndentityPost(post);

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, profileOwner);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, profileOwner);

            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(originalPost);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            postDataList.add(new PostData(post, originalPost, images, shareCount, commentCount, starCount, likeCount,
                    myStar, myLike, fromSeries, postTags));
        }

        model.addAttribute("postDataList", postDataList);
        model.addAttribute("isFollowing", followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user, profileOwner));

        return "profile_post";
    }

    @GetMapping("/view_profile_series")
    public String viewProfileSeries(@RequestParam(value = "user") String linkedUsername,
                                    ModelMap model, HttpServletRequest request) throws Exception {
        // Session User
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

        User profileOwner = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        model.addAttribute("postsCount", postRepository.countPostByUser(profileOwner));
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Series List
        List<Series> seriesList = seriesRepository.findByUser(profileOwner);
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
        model.addAttribute("isFollowing", followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user, profileOwner));

        return "profile_series";
    }

    @GetMapping("/view_profile_subscription")
    public String viewProfileSubscription(@RequestParam(value = "user") String linkedUsername,
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

        User profileOwner = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        model.addAttribute("postsCount", postRepository.countPostByUser(profileOwner));
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Subscription List
        List<SeriesFollow> subscriptionList = seriesFollowRepository.findSeriesFollowBySeriesFollowIndentityUser(profileOwner);
        Collections.sort(subscriptionList, new SubscriptionComparator());
        List<SeriesData> seriesDataList = new ArrayList<>();
        for (SeriesFollow subscription : subscriptionList) {
            Series series = subscription.getSeriesFollowIndentity().getSeries();
            List<String> tags = new ArrayList<>();
            long subscriptionCount = seriesFollowRepository.countSeriesFollowBySeriesFollowIndentitySeries(series);
            boolean subscribed = seriesFollowRepository.existsSeriesFollowBySeriesFollowIndentitySeriesAndSeriesFollowIndentityUser(series, user);
            boolean owner = series.getUser().getUsername().equals(user.getUsername());

            seriesDataList.add(new SeriesData(series, tags, subscriptionCount, subscribed, owner));
        }
        model.addAttribute("seriesDataList", seriesDataList);
        model.addAttribute("isFollowing", followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user, profileOwner));

        return "profile_subscription";
    }

    @ResponseBody
    @PostMapping("/subscribe_Unsubscribe_Series")
    public String subscribe_Unsubscribe_Series(@RequestParam("seriesID") Long seriesID,
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
        List<SeriesFollow> x = seriesFollowRepository.findSeriesFollowBySeriesFollowIndentityUserAndSeriesFollowIndentitySeriesSeriesID(user, seriesID);
        if (x.size() == 0) {
            SeriesFollow newsf = new SeriesFollow();
            SeriesFollowIndentity newsfi = new SeriesFollowIndentity();
            newsfi.setUser(user);
            newsfi.setSeries(seriesRepository.findById(seriesID).get());
            newsf.setSeriesFollowIndentity(newsfi);
            seriesFollowRepository.save(newsf);
            return "Subscribe Success!";
        } else {
            seriesFollowRepository.delete(x.get(0));
            return "Unsubscribe Success!";
        }
    }

    @ResponseBody
    @PostMapping("/subscribeSeries")
    public String subscribeSeries(@RequestParam("seriesID") Long seriesID,
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

        List<SeriesFollow> x = seriesFollowRepository.findSeriesFollowBySeriesFollowIndentityUserAndSeriesFollowIndentitySeriesSeriesID(user, seriesID);
        if (x.size() == 0) {
            SeriesFollow newsf = new SeriesFollow();
            SeriesFollowIndentity newsfi = new SeriesFollowIndentity();
            newsfi.setSeries(seriesRepository.findById(seriesID).get());
            newsfi.setUser(user);
            newsf.setSeriesFollowIndentity(newsfi);
            seriesFollowRepository.save(newsf);
            return "Subscribe Success!";
        }
        return "Subscribed";
    }

    @ResponseBody
    @PostMapping("/unsubscribeSeries")
    public String unsubscribeSeries(@RequestParam("seriesID") Long seriesID,
                                    ModelMap model, HttpServletRequest request) throws Exception {

        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<SeriesFollow> x = seriesFollowRepository.findSeriesFollowBySeriesFollowIndentityUserAndSeriesFollowIndentitySeriesSeriesID(user, seriesID);
        if (x.size() != 0) {
            seriesFollowRepository.delete(x.get(0));
        }
        return "Unsubscribed";
    }

    @GetMapping("/view_profile_star")
    public String viewProfileStar(@RequestParam(value = "user") String linkedUsername,
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

        User profileOwner = user;
        model.addAttribute("profileOwner", profileOwner);

        model.addAttribute("isOthersProfile", false);

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(profileOwner));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(profileOwner));

        //All the post by this user
        model.addAttribute("postsCount", postRepository.countPostByUser(profileOwner));
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(profileOwner));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(profileOwner));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(profileOwner));

        // Get Star List
        List<Star> starList = starRepository.findByPostIndentityUser(user);

        // Organize Info
        List<PostData> postDataList = new ArrayList<>();
        List<FollowStatus> followingStatus = new ArrayList<>();
        for (int i = 0; i < starList.size(); i++) {
            // Post Content
            Post post = starList.get(i).getPostIndentity().getPost();
            Post originalPost = null;
            if (post.isRepost()) {
                originalPost = postRepository.findPostByPostID(post.getOriginalPostID());
            }
            List<PostContent> postContents = postContentRepository.
                    findByPostIndentityPostPostID(starList.get(i).getPostIndentity().getPost().getOriginalPostID());
            List<String> images = new ArrayList<>();
            Set<Series> fromSeries = new TreeSet<>();
            for (int j = 0; j < postContents.size(); j++) {
                images.add(postContents.get(j).getPostIndentity().getWork().getThumbnail());
            }

            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(post);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            postDataList.add(new PostData(post, originalPost, images, 0, 0, 0, 0,
                    true, myLike, fromSeries, postTags));
        }

        Collections.sort(postDataList);

        for (int i = 0; i < postDataList.size(); i++) {
            if (profileOwner.getUsername().equals(postDataList.get(i).post.getUser().getUsername())) {
                followingStatus.add(FollowStatus.SELF);
            } else if (followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(profileOwner, postDataList.get(i).post.getUser())) {
                followingStatus.add(FollowStatus.FOLLOWING);
            } else {
                followingStatus.add(FollowStatus.NOT_FOLLOWED);
            }
        }

        model.addAttribute("followingStatus", followingStatus);
        model.addAttribute("postDataList", postDataList);

        return "profile_star";
    }


    @PostMapping("/deleteStarredPost")
    public String deleteStarredPost(@RequestParam(value = "postID") long postID,
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

        Post postToBeDeleted = postRepository.findPostByPostID(postID);
        if (postToBeDeleted == null)
            return viewProfileStar(username, model, request);

        deletePost(postID, model, request);
        return viewProfileStar(username, model, request);
    }

    @PostMapping("/deletePost")
    public String deletePost(@RequestParam(value = "postID") long postID,
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

        Post postToBeDeleted = postRepository.findPostByPostID(postID);
        if (postToBeDeleted == null)
            return viewProfile(username, model, request);

        if (!postToBeDeleted.isRepost()) {
            List<PostContent> postContentList = postContentRepository.findByPostIndentityPostPostID(postToBeDeleted.getPostID());
            for (PostContent postContent : postContentList) {
                Work work = postContent.getPostIndentity().getWork();
                postContentRepository.delete(postContent);
                List<SeriesContent> seriesContentList = seriesContentRepository.findSeriesContentBySeriesContentIndentityWork(work);
                for (SeriesContent sc : seriesContentList)
                    seriesContentRepository.delete(sc);
                workRepository.delete(work);
            }

            List<Post> repostList = postRepository.findByOriginalPostIDAndIsRepost(postToBeDeleted.getPostID(), true);
            for (Post repost : repostList) {
                postRepository.delete(repost);
            }
            List<Like> likeList = likeRepository.findByPostIndentityPost(postToBeDeleted);
            for (Like like : likeList) {
                likeRepository.delete(like);
            }
            List<Star> starList = starRepository.findByPostIndentityPost(postToBeDeleted);
            for (Star star : starList) {
                starRepository.delete(star);
            }
        }
        postRepository.delete(postToBeDeleted);
        updateGenre();

        return viewProfile(username, model, request);
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
                    PostContent tempPostContent = postContentRepository.findFirstByPostIndentityPostPostID(tempPost.getOriginalPostID());
                    if(tempPostContent != null)
                        genreCover = tempPostContent.getPostIndentity().getWork().getThumbnail();
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

