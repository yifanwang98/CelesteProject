package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.PostComparator;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.PostContent;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Controller
public class MainPageController {


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

    @GetMapping("/mainPage")
    public String mainPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        model.addAttribute("User", user);

        //All the post by this user
        List<Post> postlist = postRepository.findByUser(user);
        //All the post by this user's follows
        List<Follow> followlist = followRepository.findByFollowIndentityUserone(user);
        for (int i = 0; i < followlist.size(); i++) {
            postlist.addAll(postRepository.findByUser(followlist.get(i).getFollowIndentity().getUser2()));
        }

        Collections.sort(postlist, new PostComparator());

        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
        List<PostContent> temp;
        for (int i = 0; i < postlist.size(); i++) {
            ArrayList<String> list = new ArrayList<String>();
            temp = postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID());
            for (int j = 0; j < temp.size(); j++) {
                list.add(temp.get(j).getPostIndentity().getWork().getThumbnail());
            }
            imgsForeachPost.put(postlist.get(i).getPostID(), list);
        }


        model.addAttribute("postlist", postlist);
        model.addAttribute("imgsForeachPost", imgsForeachPost);


        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(user);
        model.addAttribute("following", c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(user);
        model.addAttribute("followers", d.size());

        model.addAttribute("postsCount", postRepository.findByUser(user).size());

        HashMap<Long, Integer> likesForeachPost = new HashMap<Long, Integer>();
        for (int i = 0; i < postlist.size(); i++) {
            likesForeachPost.put(postlist.get(i).getPostID(), likeRepository.findByPostIndentityPost(postlist.get(i)).size());
        }
        HashMap<Long, Integer> starsForeachPost = new HashMap<Long, Integer>();
        for (int i = 0; i < postlist.size(); i++) {
            starsForeachPost.put(postlist.get(i).getPostID(), starRepository.findByPostIndentityPost(postlist.get(i)).size());
        }

        HashMap<Long, Integer> repostsForeachPost = new HashMap<Long, Integer>();
        for (int i = 0; i < postlist.size(); i++) {
            repostsForeachPost.put(postlist.get(i).getPostID(), postRepository.countByoriginalPostIDAndIsRepost(postlist.get(i).getOriginalPostID(), true));
        }

        model.addAttribute("repostsForeachPost", repostsForeachPost);
        model.addAttribute("likesForeachPost", likesForeachPost);
        model.addAttribute("starsForeachPost", starsForeachPost);


        request.getSession().setAttribute("username", username);
        return "home";

    }

}

