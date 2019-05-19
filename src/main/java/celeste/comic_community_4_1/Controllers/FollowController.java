package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
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
public class FollowController {

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

    @GetMapping("/following")
    public String viewFollowing(@RequestParam(value = "user") String linkedUsername,
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

        // Is the same user
        model.addAttribute("isOthersProfile", !username.equals(linkedUsername));
        User linkedUser = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", linkedUsername));
        model.addAttribute("profileOwner", linkedUser);
        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        //All the post by this user
        List<Post> postList = postRepository.findByUser(linkedUser);
        model.addAttribute("postsCount", postList.size());
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(linkedUser));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(linkedUser));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(linkedUser));

        // Get Following
        List<Follow> following = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("followingList", following);
        model.addAttribute("following", following.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followers", d.size());

        if (!linkedUsername.equals(username)) {
            model.addAttribute("crossCheckedFollowList", crossCheckFollowing(user, following));
            model.addAttribute("isSubscribing", isSubscribing(user, linkedUser));
        }

        model.addAttribute("isFollowing", followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user, linkedUser));

        return "profile_following";
    }

    @GetMapping("/follower")
    public String viewFollowers(@RequestParam(value = "user") String linkedUsername,
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

        // Is the same user
        model.addAttribute("isOthersProfile", !username.equals(linkedUsername));
        User linkedUser = userRepository.findById(linkedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", linkedUsername));
        model.addAttribute("profileOwner", linkedUser);
        model.addAttribute("isOthersProfile", !linkedUsername.equals(username));

        //All the post by this user
        List<Post> postList = postRepository.findByUser(linkedUser);
        model.addAttribute("postsCount", postList.size());
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(linkedUser));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(linkedUser));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(linkedUser));

        // Get Following
        List<Follow> following = followRepository.findByFollowIndentityUserone(linkedUser);
        model.addAttribute("following", following.size());
        //Get Followers
        List<Follow> follower = followRepository.findByFollowIndentityUsertwo(linkedUser);
        model.addAttribute("followerList", follower);
        model.addAttribute("followers", follower.size());

        if (!linkedUsername.equals(username)) {
            model.addAttribute("crossCheckedFollowList", crossCheckFollower(user, follower));
            model.addAttribute("isSubscribing", isSubscribing(user, linkedUser));
        }

        model.addAttribute("isFollowing", followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user, linkedUser));
        return "profile_follower";
    }

    @ResponseBody
    @PostMapping("/Remove")
    public String unfollow(@RequestParam(value = "remove") String toBeUnfollow,
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
        model.addAttribute("User", user);
        model.addAttribute("profileOwner", user);
        model.addAttribute("isOthersProfile", false);


        List<Follow> x = followRepository.findByFollowIndentityUseroneUsernameAndFollowIndentityUsertwoUsername(toBeUnfollow, user.getUsername());
        if (x.size() != 0) {
            followRepository.delete(x.get(0));
            return "Remove Success!";
        }
        return "Unexpected Error!";
    }

    private List<Boolean> crossCheckFollowing(User me, List<Follow> othersFollowing) {
        List<Boolean> result = new ArrayList<>();
        for (Follow f : othersFollowing) {
            result.add(followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(me, f.getFollowIndentity().getUser2()));
        }
        return result;
    }

    private List<Boolean> crossCheckFollower(User me, List<Follow> othersFollowing) {
        List<Boolean> result = new ArrayList<>();
        for (Follow f : othersFollowing) {
            result.add(followRepository.existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(me, f.getFollowIndentity().getUser1()));
        }
        return result;
    }


    private boolean isSubscribing(User me, User others) {
        List<Follow> myFollowing = followRepository.findByFollowIndentityUserone(me);
        for (Follow f : myFollowing) {
            if (f.getFollowIndentity().getUser2().getUsername().equals(others.getUsername())) {
                return true;
            }
        }
        return false;
    }

    @ResponseBody
    @PostMapping("/followUser")
    public String followUser(@RequestParam(value = "username2") String username2,
                             ModelMap model, HttpServletRequest request) throws Exception {

        String username1 = (String) request.getSession().getAttribute("username");
        User user1 = userRepository.findUserByUsername(username1);
        User user2 = userRepository.findUserByUsername(username2);
        Follow x = followRepository.findFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user1, user2);
        if (x != null) {
            return "Following";
        } else {
            Follow newfollow = new Follow();
            FollowIndentity followIndentity = new FollowIndentity();
            followIndentity.setUser2(userRepository.findById(username2).get());
            followIndentity.setUser1(userRepository.findById(username1).get());
            newfollow.setFollowIndentity(followIndentity);
            followRepository.save(newfollow);
            return "Follow Success!";
        }
    }

    @ResponseBody
    @PostMapping("/unfollowUser")
    public String unfollowUser(@RequestParam(value = "username2") String username2,
                               ModelMap model, HttpServletRequest request) throws Exception {

        String username1 = (String) request.getSession().getAttribute("username");
        User user1 = userRepository.findUserByUsername(username1);
        User user2 = userRepository.findUserByUsername(username2);
        Follow x = followRepository.findFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(user1, user2);
        if (x != null) {
            followRepository.delete(x);
            return "Unfollow Success!";
        }

        return "Not Following";
    }
}

