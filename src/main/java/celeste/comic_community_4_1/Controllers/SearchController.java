package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.ComicGenre;
import celeste.comic_community_4_1.miscellaneous.PostComparator;
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
    LikeRepository likeRepository;

    @Autowired
    StarRepository starRepository;

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


        request.getSession().setAttribute("username", username);
        model.addAttribute("genreList", ComicGenre.GENRE);
        return "search";
    }

    @PostMapping("/searchForm")
    public String search(ModelMap model, HttpServletRequest request,
                            @RequestParam(value = "genre",required=false) String[] genres,
                            @RequestParam(value = "filter",required=false) String[] filter,
                             @RequestParam(value = "searchContent") String searchContent) throws Exception {
//        System.out.println(Arrays.toString(genres));
//        System.out.println(Arrays.toString(filter));
//        System.out.println(searchContent);
        if(genres==null && filter==null){
            List<Work> worklistByAuthor = workRepository.findByUserUsername(searchContent);
            List<Work> worklistByGenre  = workRepository.findByGenre(searchContent);
            if(worklistByAuthor.isEmpty()){
                System.out.println("null1");
            }
            if(worklistByGenre.isEmpty()){
                System.out.println("null2");
            }
            List<Work> finalworklist =new ArrayList<>();
            worklistByAuthor.removeAll(worklistByGenre);
            worklistByAuthor.addAll(worklistByGenre);
            finalworklist = worklistByAuthor;

            ArrayList<Post> postlist = new ArrayList<>();
            for(Work eachWork : finalworklist){
                List<PostContent> temp = postContentRepository.findByPostIndentityWork(eachWork);
                if(!temp.isEmpty()){
                    for(PostContent pc : temp){
                        Post po = postRepository.findById(pc.getPostIndentity().getPost().getPostID()).get();
                        if(!postlist.contains(po)){
                            postlist.add(po);
                        }
                    }
                }
            }
            Collections.sort(postlist, new PostComparator());

            System.out.println(postlist.size());

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
        }
//        else if(genres==null && filter!=null){
//
//        }


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


        request.getSession().setAttribute("username", username);
        model.addAttribute("genreList", ComicGenre.GENRE);
        return "search";
    }

}

