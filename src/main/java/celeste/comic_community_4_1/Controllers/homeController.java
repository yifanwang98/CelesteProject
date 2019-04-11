package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import celeste.comic_community_4_1.repository.*;
import com.mysql.cj.jdbc.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

@Controller
public class homeController {


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

    @GetMapping("/profile_post")
    public String userprofile(ModelMap model, HttpServletRequest request) throws Exception{
        String username = (String)request.getSession().getAttribute("username");
        String password = (String)request.getSession().getAttribute("password");

        User founduser = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Optional<User> a = userRepository.findById(username);
        User b= a.get();
        model.addAttribute("User",b);
        //All the post by this user
        List<Post> postlist = postRepository.findByUser(b);
        //All the post by this user's follows
//        List<Follow> followlist = followRepository.findByFollowIndentityUserone(b);
//        for(int i =0; i<followlist.size();i++){
//            postlist.addAll(postRepository.findByUser(followlist.get(i).getFollowIndentity().getUser2()));
//        }
//        postContentRepository.findByPostIndentityPost()
        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
        for(int i =0; i<postlist.size();i++){
            ArrayList<String> list = new ArrayList<String>();
            for(int j =0; j<postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).size();j++){
                list.add(postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).get(j).getPostIndentity().getWork().getContent());
            }
            imgsForeachPost.put(postlist.get(i).getPostID(),list);
        }

        model.addAttribute("postlist",postlist);
        model.addAttribute("imgsForeachPost",imgsForeachPost);

        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(b);
        model.addAttribute("following",c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(b);
        model.addAttribute("followers",d.size());

        return "profile_post";
    }
    @ResponseBody
    @GetMapping("/checkusername")
    public String testtt(@RequestParam(value = "username" ,required = false) String username){
        Optional<User> a=  userRepository.findById(username);
//        System.out.println(a);
        if(a.isPresent()){
            return "Taken";
        }
        else {
            return "Not taken";
        }
    }
}

