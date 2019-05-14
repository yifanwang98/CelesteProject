package celeste.comic_community_4_1.Controllers;

import celeste.comic_community_4_1.exception.ResourceNotFoundException;
import celeste.comic_community_4_1.miscellaneous.Notification;
import celeste.comic_community_4_1.miscellaneous.PostComparator;
import celeste.comic_community_4_1.miscellaneous.PostData;
import celeste.comic_community_4_1.model.*;
import celeste.comic_community_4_1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DiscoverController {


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
    SearchWordsRepository searchWordsRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @GetMapping("/discover")
    public String mainPage(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("username") == null) {
            return "index";
        }

        // Session User
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if (user.getBlockStatus().equals("1")) {
            if (user.getBlockedSince().after(Notification.getDaysBefore(3))) {
                return "blocked";
            }
            user.setBlockStatus("none");
            userRepository.save(user);
        }

        model.addAttribute("User", user);

        // Get Following & Followers
        model.addAttribute("following", followRepository.countFollowByFollowIndentityUserone(user));
        model.addAttribute("followers", followRepository.countFollowByFollowIndentityUsertwo(user));

        //All the post by this user
        List<Post> postList = postRepository.findByUser(user);
        model.addAttribute("postsCount", postList.size());

        postList.clear();

        List<SearchWords> wordslist = searchWordsRepository.findTop5ByOrderByHeatDesc();
        Collections.reverse(wordslist);
        for(int i =0;i<wordslist.size();i++){
            String word = wordslist.get(i).getWord();
            List<Post> pg = postRepository.findByPrimaryGenre(word);
            List<Post> sg = postRepository.findBySecondaryGenre(word);
            pg.removeAll(sg);
            pg.addAll(sg);
            List<Post> temp=pg;


            if(temp.size()==0){
                continue;
            }
            else if(temp.size()<=i){
                postList.addAll(temp);
                continue;
            }
            else {
                temp.subList(i, temp.size()).clear();
                postList.addAll(temp);
            }

        }

        postList = postList.stream().distinct().collect(Collectors.toList());

        // Sort
        Collections.sort(postList, new PostComparator());

        // Organize Info
        List<PostData> postDataList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            // Post Content
            Post post = postList.get(i);
            Post originalPost = null;
            if (post.isRepost()) {
                originalPost = postRepository.findPostByPostID(post.getOriginalPostID());
            }
            List<PostContent> postContents = postContentRepository.findByPostIndentityPostPostID(postList.get(i).getOriginalPostID());
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

            boolean myStar = starRepository.existsStarByPostIndentityPostAndPostIndentityUser(post, user);
            boolean myLike = likeRepository.existsLikeByPostIndentityPostAndPostIndentityUser(post, user);

            // Tag
            List<String> postTags = new ArrayList<>();
            List<PostTag> postTagList = postTagRepository.findPostTagByPost(post);
            for (PostTag tag : postTagList) {
                postTags.add(tag.getTag());
            }

            postDataList.add(new PostData(post, originalPost, images, myStar, myLike, fromSeries, postTags));
        }

        model.addAttribute("postDataList", postDataList);

        model.addAttribute("seriesCount", seriesRepository.countSeriesByUser(user));
        model.addAttribute("starCount", starRepository.countStarByPostIndentityUser(user));

        // Top Search
        List<SearchWords> top10Searches = searchWordsRepository.findTop10ByOrderByHeatDesc();
        model.addAttribute("top10Searches", top10Searches);

        return "discover";

    }

}

