package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import celeste.comic_community_4_1.model.Work;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

@Controller
public class IndexController {


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

    @PostMapping("/home")
    public String FirstLogin(@RequestParam(value = "username" ,required = false) String username,
                             @RequestParam(value = "password" ,required = false) String password,
                             ModelMap model, HttpServletRequest request) throws Exception{
//        System.out.println("123123");
        if(request.getSession().getAttribute("username")!=null){
            request.getSession().removeAttribute("username");
        }
        //----------------------work---------------
//        Work newwork = new Work();
//        newwork.setGenre("genre1");
//        newwork.setName("work1");
//        newwork.setUser(userRepository.findById("1").get());
//        String avatarPath = "src/main/resources/static/images/samplePost/T-2-1.jpg";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "jpg", bos );
//        byte [] data = bos.toByteArray();
//        String base64 = Base64.getEncoder().encodeToString(data);
//        newwork.setContent(base64);
//        workRepository.save(newwork);

//        Work newwork = new Work();
//        newwork.setWorkID(222222);
//        newwork.setGenre("genre2");
//        newwork.setName("work2");
//        newwork.setUser(userRepository.findById("2").get());
//        String avatarPath = "src/main/resources/static/images/samplePost/S-1-2.jpg";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "jpg", bos );
//        byte [] data = bos.toByteArray();
//        String base64 = Base64.getEncoder().encodeToString(data);
//        newwork.setContent(base64);
//        workRepository.save(newwork);
        //----------------------work---------------
        //----------------------post---------------
//        Post newPost = new Post();
//        newPost.setUser(userRepository.findById("1").get());
//        newPost.setRepost(false);
//        newPost.setOriginalPostID(2);
//
//        postRepository.save(newPost);
        //----------------------post---------------
        //----------------------avatar---------------
//       String avatarPath = "src/main/resources/static/images/default-avatar.png";
//        File x = new File(avatarPath);
//        BufferedImage bImage = ImageIO.read(x);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "png", bos );
//        byte [] data = bos.toByteArray();
//        String base64 = Base64.getEncoder().encodeToString(data);
////        b.setAvatar(base64);
//        userRepository.findById("2").get().setAvatar(base64);
//        userRepository.save(userRepository.findById("2").get());
        //----------------------avatar---------------
        if (!userRepository.existsById(username)) {
            model.addAttribute("errors","This username doesn't exist.");
            return "index";
        }
        User founduser = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if(!password.equals(founduser.getPassword())){
            model.addAttribute("errors","Your password is incorrect.");
            return "index";
        }
        Optional<User> a = userRepository.findById(username);
        User b= a.get();
        model.addAttribute("User",b);
        //All the post by this user
        List<Post> postlist = postRepository.findByUser(b);
        //All the post by this user's follows
        List<Follow> followlist = followRepository.findByFollowIndentityUserone(b);
        for(int i =0; i<followlist.size();i++){
            postlist.addAll(postRepository.findByUser(followlist.get(i).getFollowIndentity().getUser2()));
        }
//        postContentRepository.findByPostIndentityPost()
        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
        for(int i =0; i<postlist.size();i++){
            ArrayList<String> list = new ArrayList<String>();
            for(int j =0; j<postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).size();j++){
                String content = postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).get(j).getPostIndentity().getWork().getContent();
                list.add(content);
            }
            imgsForeachPost.put(postlist.get(i).getPostID(),list);
        }

        int x = 1;
        model.addAttribute("postlist",postlist);
        model.addAttribute("imgsForeachPost",imgsForeachPost);


        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(b);
        model.addAttribute("following",c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(b);
        model.addAttribute("followers",d.size());

        model.addAttribute("postsCount",postRepository.findByUser(b).size());

        HashMap<Long, Integer> likesForeachPost = new HashMap<Long, Integer>();
        for(int i =0; i<postlist.size();i++){
            likesForeachPost.put(postlist.get(i).getPostID(),likeRepository.findByPostIndentityPost(postlist.get(i)).size());
        }
        HashMap<Long, Integer> starsForeachPost = new HashMap<Long, Integer>();
        for(int i =0; i<postlist.size();i++){
            starsForeachPost.put(postlist.get(i).getPostID(),starRepository.findByPostIndentityPost(postlist.get(i)).size());
        }

        HashMap<Long, Integer> repostsForeachPost = new HashMap<Long, Integer>();
        for(int i =0; i<postlist.size();i++){
            repostsForeachPost.put(postlist.get(i).getPostID(),postRepository.countByoriginalPostIDAndIsRepost(postlist.get(i).getOriginalPostID(),true));
        }

        model.addAttribute("repostsForeachPost",repostsForeachPost);
        model.addAttribute("likesForeachPost",likesForeachPost);
        model.addAttribute("starsForeachPost",starsForeachPost);


//        System.out.println("Following:" + c.size() + ". Followers:" + d.size()+".");
        request.getSession().setAttribute("username",username);
        request.getSession().setAttribute("password",password);
        return "home";

    }
    @GetMapping("/home")
    public String getlogin(ModelMap model, HttpServletRequest request) throws Exception{
        String username = (String)request.getSession().getAttribute("username");
        String password = (String)request.getSession().getAttribute("password");

        Optional<User> a = userRepository.findById(username);
        User b= a.get();
        model.addAttribute("User",b);
        //All the post by this user
        List<Post> postlist = postRepository.findByUser(b);
        //All the post by this user's follows
        List<Follow> followlist = followRepository.findByFollowIndentityUserone(b);
        for(int i =0; i<followlist.size();i++){
            postlist.addAll(postRepository.findByUser(followlist.get(i).getFollowIndentity().getUser2()));
        }
//        postContentRepository.findByPostIndentityPost()
        HashMap<Long, List<String>> imgsForeachPost = new HashMap<Long, List<String>>();
        for(int i =0; i<postlist.size();i++){
            ArrayList<String> list = new ArrayList<String>();
            for(int j =0; j<postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).size();j++){
                String content = postContentRepository.findByPostIndentityPostPostID(postlist.get(i).getOriginalPostID()).get(j).getPostIndentity().getWork().getContent();
                list.add(content);
            }
            imgsForeachPost.put(postlist.get(i).getPostID(),list);
        }

        int x = 1;
        model.addAttribute("postlist",postlist);
        model.addAttribute("imgsForeachPost",imgsForeachPost);


        //Get Follows
        List<Follow> c = followRepository.findByFollowIndentityUserone(b);
        model.addAttribute("following",c.size());
        //Get Followers
        List<Follow> d = followRepository.findByFollowIndentityUsertwo(b);
        model.addAttribute("followers",d.size());

        model.addAttribute("postsCount",postRepository.findByUser(b).size());

        HashMap<Long, Integer> likesForeachPost = new HashMap<Long, Integer>();
        for(int i =0; i<postlist.size();i++){
            likesForeachPost.put(postlist.get(i).getPostID(),likeRepository.findByPostIndentityPost(postlist.get(i)).size());
        }
        HashMap<Long, Integer> starsForeachPost = new HashMap<Long, Integer>();
        for(int i =0; i<postlist.size();i++){
            starsForeachPost.put(postlist.get(i).getPostID(),starRepository.findByPostIndentityPost(postlist.get(i)).size());
        }

        HashMap<Long, Integer> repostsForeachPost = new HashMap<Long, Integer>();
        for(int i =0; i<postlist.size();i++){
            repostsForeachPost.put(postlist.get(i).getPostID(),postRepository.countByoriginalPostIDAndIsRepost(postlist.get(i).getOriginalPostID(),true));
        }

        model.addAttribute("repostsForeachPost",repostsForeachPost);
        model.addAttribute("likesForeachPost",likesForeachPost);
        model.addAttribute("starsForeachPost",starsForeachPost);


//        System.out.println("Following:" + c.size() + ". Followers:" + d.size()+".");
        request.getSession().setAttribute("username",username);
        request.getSession().setAttribute("password",password);
        return "home";

    }

    @GetMapping("/signup")
    public String signUp() {
        return "signUp";
    }

}

