package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.AnalysisData;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;

@Controller
public class AnalysisController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    AnalysisRepository analysisRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostAnalysisRepository postAnalysisRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    SeriesFollowRepository seriesFollowRepository;

    @GetMapping("/profile_analysis")
    public String getAnalysis(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }
        // Find Current User
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

        model.addAttribute("profileOwner", user);

        List<Post> postList = postRepository.findByUser(user);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Analysis data
        AnalysisData analysisData = new AnalysisData();

        // Get Contribution
        long[] contribution = new long[]{0, 0, 0, 0};
        calendar.add(Calendar.DATE, -7);
        contribution[0] = postRepository.countPostByCreatedAtAfterAndUserAndIsRepost(calendar.getTime(), user, false);
        calendar.add(Calendar.DATE, 7);

        calendar.add(Calendar.DATE, -30);
        contribution[1] = postRepository.countPostByCreatedAtAfterAndUserAndIsRepost(calendar.getTime(), user, false);
        calendar.add(Calendar.DATE, 30);

        calendar.add(Calendar.DATE, -180);
        contribution[2] = postRepository.countPostByCreatedAtAfterAndUserAndIsRepost(calendar.getTime(), user, false);
        calendar.add(Calendar.DATE, 180);

        calendar.add(Calendar.DATE, -365);
        contribution[3] = postRepository.countPostByCreatedAtAfterAndUserAndIsRepost(calendar.getTime(), user, false);
        calendar.add(Calendar.DATE, 365);
        analysisData.setContribution(contribution);


        // Get Views
        long[] views = new long[]{0, 0, 0, 0};
        for (Post p : postList) {
            calendar.add(Calendar.DATE, -7);
            views[0] += postAnalysisRepository.countPostAnalysisByViewedAtAfterAndPost(calendar.getTime(), p);
            calendar.add(Calendar.DATE, 7);

            calendar.add(Calendar.DATE, -30);
            views[1] += postAnalysisRepository.countPostAnalysisByViewedAtAfterAndPost(calendar.getTime(), p);
            calendar.add(Calendar.DATE, 30);

            calendar.add(Calendar.DATE, -180);
            views[2] += postAnalysisRepository.countPostAnalysisByViewedAtAfterAndPost(calendar.getTime(), p);
            calendar.add(Calendar.DATE, 180);

            calendar.add(Calendar.DATE, -365);
            views[3] += postAnalysisRepository.countPostAnalysisByViewedAtAfterAndPost(calendar.getTime(), p);
            calendar.add(Calendar.DATE, 365);
        }
        analysisData.setView(views);

        // Get Subscribers
        long[] followCount = new long[]{0, 0, 0, 0};
        calendar.add(Calendar.DATE, -7);
        followCount[0] = followRepository.countFollowByCreatedAtAfterAndAndFollowIndentityUsertwo(calendar.getTime(), user);
        calendar.add(Calendar.DATE, 7);

        calendar.add(Calendar.DATE, -30);
        followCount[1] = followRepository.countFollowByCreatedAtAfterAndAndFollowIndentityUsertwo(calendar.getTime(), user);
        calendar.add(Calendar.DATE, 30);

        calendar.add(Calendar.DATE, -180);
        followCount[2] = followRepository.countFollowByCreatedAtAfterAndAndFollowIndentityUsertwo(calendar.getTime(), user);
        calendar.add(Calendar.DATE, 180);

        calendar.add(Calendar.DATE, -365);
        followCount[3] = followRepository.countFollowByCreatedAtAfterAndAndFollowIndentityUsertwo(calendar.getTime(), user);
        calendar.add(Calendar.DATE, 365);
        analysisData.setSubscriber(followCount);

        // Get Profit
        analysisData.setProfit(new String[]{
                String.format("%.2f", getProfit(contribution[0], views[0])),
                String.format("%.2f", getProfit(contribution[1], views[1])),
                String.format("%.2f", getProfit(contribution[2], views[2])),
                String.format("%.2f", getProfit(contribution[3], views[3])),
        });

        model.addAttribute("analysisData", analysisData);

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());
        //All the post by this user
        model.addAttribute("postsCount", postRepository.countPostByUser(user));
        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("subscriptionCount", seriesFollowRepository.countSeriesFollowBySeriesFollowIndentityUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        return "profile_analysis";
    }

    private double getProfit(long contribution, long view) {
        return contribution * view * Math.PI / 100.0;
    }

}