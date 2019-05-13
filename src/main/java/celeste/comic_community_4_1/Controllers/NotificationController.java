package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.miscellaneous.NotificationComparator;
import celeste.comic_community_4_1.miscellaneous.NotificationType;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class NotificationController {


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
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @GetMapping("/notification")
    public String goToNotification(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        // Blocked User
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                request.getSession().removeAttribute("username");
                request.getSession().removeAttribute("postDraft");
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

        Date lastView = user.getLastViewNotification();
        // Get Notification
        ArrayList<Notification> notificationList = new ArrayList<>();
        // Notification - Follow
        for (Follow f : d) {
            if (f.getCreatedAt().after(lastView))
                notificationList.add(new Notification(NotificationType.FOLLOW,
                        f.getFollowIndentity().getUser1().getUsername(), f.getCreatedAt()));
        }

        // Notification - Like / Star / Comment / Share
        List<Post> postList = postRepository.findByUser(user);
        for (Post p : postList) {
            List<Like> likeList = likeRepository.findLikeByCreatedAtAfterAndPostIndentityPost(lastView, p);
            for (Like like : likeList) {
                if (like.getPostIndentity().getUser().getUsername().equals(username))
                    continue;
                if (like.getCreatedAt().after(lastView))
                    notificationList.add(new Notification(NotificationType.LIKE, like.getPostIndentity().getUser().getUsername(),
                            like.getCreatedAt(), p.getPostID()));
            }

            List<Star> starList = starRepository.findStarByCreatedAtAfterAndPostIndentityPost(lastView, p);
            for (Star star : starList) {
                if (star.getPostIndentity().getUser().getUsername().equals(username))
                    continue;
                if (star.getCreatedAt().after(lastView))
                    notificationList.add(new Notification(NotificationType.STAR, star.getPostIndentity().getUser().getUsername(),
                            star.getCreatedAt(), p.getPostID()));
            }

            List<Comment> commentList = commentRepository.findCommentByCreatedAtAfterAndPost(lastView, p);
            for (Comment comment : commentList) {
                if (comment.getUser().getUsername().equals(username))
                    continue;
                if (comment.getCreatedAt().after(lastView))
                    notificationList.add(new Notification(NotificationType.COMMENT,
                            comment.getUser().getUsername(), comment.getCreatedAt(),
                            p.getPostID(), comment.getContent()));
            }

            List<Post> shareList = postRepository.findByCreatedAtAfterAndOriginalPostIDAndIsRepost(lastView, p.getPostID(), true);
            for (Post shared : shareList) {
                if (shared.getUser().getUsername().equals(username))
                    continue;
                if (!shared.getCreatedAt().after(lastView))
                    continue;
                if (shared.getPostComment().length() > 0) {
                    String comment = shared.getPostComment();
                    if (comment.length() > 10) {
                        comment = comment.substring(0, 10) + "...";
                    }
                    notificationList.add(new Notification(NotificationType.SHARE, shared.getUser().getUsername(),
                            shared.getCreatedAt(), p.getPostID(), shared.getPostID(), comment));
                } else {
                    notificationList.add(new Notification(NotificationType.SHARE, shared.getUser().getUsername(),
                            shared.getCreatedAt(), p.getPostID(), shared.getPostID(), null));
                }
            }
        }
        // Notification - Series Subscription
        List<Series> seriesList = seriesRepository.findByUser(user);
        for (Series series : seriesList) {
            List<SeriesFollow> subscription = seriesFollowRepository.findByCreatedAtAfterAndSeriesFollowIndentitySeries(lastView, series);
            for (SeriesFollow sf : subscription) {
                if (sf.getCreatedAt().after(lastView))
                    notificationList.add(new Notification(NotificationType.SUBSCRIBE,
                            sf.getSeriesFollowIndentity().getUser().getUsername(), sf.getCreatedAt(), series.getSeriesID()));
            }
        }

        model.addAttribute("lastView", Notification.getDateString(lastView));
        user.setLastViewNotification(new Date());
        userRepository.save(user);

        Collections.sort(notificationList, new NotificationComparator());
        model.addAttribute("notificationList", notificationList);

        request.getSession().setAttribute("username", username);

        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        return "notification";
    }

}

