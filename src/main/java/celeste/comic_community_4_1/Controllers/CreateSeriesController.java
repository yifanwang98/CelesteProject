package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.Star;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CreateSeriesController {


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
    SeriesRepository seriesRepository;

    @GetMapping("/createSeries")
    public String mainPage(ModelMap model, HttpServletRequest request) throws Exception {
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


        request.getSession().setAttribute("username", username);
        return "createSeries";

    }

    @PostMapping("/createSeriesForm")
    public String createSeriesForm(ModelMap model, HttpServletRequest request,
                                    @RequestParam(value = "title") String title,
                                    @RequestParam(value = "description",required = false) String description,
                                    @RequestParam(value = "genre1",required = false) String genre1,
                                    @RequestParam(value = "genre2",required = false) String genre2,
                                     @RequestParam(value = "wiki") String wiki)throws Exception {
        String username = (String) request.getSession().getAttribute("username");

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));


        Series newseries = new Series();
        if(description!=null){newseries.setDescription(description);}
        if (genre1 != null) {
            newseries.setPrimaryGenre(genre1);
        }
        if (genre2 != null) {
            newseries.setSecondaryGenre(genre2);
        }
        newseries.setSeriesName(title);
        newseries.setPublicEditing(wiki.equals("Yes")?true:false);
        newseries.setUser(user);

        seriesRepository.save(newseries);

        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User

        model.addAttribute("User", user);

        // Is the same user
        model.addAttribute("isOthersProfile", false);
        model.addAttribute("profileOwner", user);

        //All Counts
        //Get Num of Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Num of Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());
        // Get Num of Post
        model.addAttribute("num_post", postRepository.findByUser(user).size());

        // Get Star List
        List<Star> starList = starRepository.findByPostIndentityUser(user);
        model.addAttribute("num_star", starList.size());
        model.addAttribute("starList", starList);

        return "profile_star";
    }


}

