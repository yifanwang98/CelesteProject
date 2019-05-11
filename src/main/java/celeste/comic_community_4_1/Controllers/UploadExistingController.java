//package celeste.comic_community_4_1.Controllers;
//
//import celeste.comic_community_4_1.exception.ResourceNotFoundException;
//import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
//import celeste.comic_community_4_1.model.Post;
//import celeste.comic_community_4_1.model.PostContent;
//import celeste.comic_community_4_1.model.User;
//import celeste.comic_community_4_1.model.Work;
//import celeste.comic_community_4_1.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.imageio.ImageIO;
//import javax.servlet.http.HttpServletRequest;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.*;
//
//@Controller
//public class UploadExistingController {
//
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    FollowRepository followRepository;
//
//    @Autowired
//    WorkRepository workRepository;
//
//    @Autowired
//    PostRepository postRepository;
//
//    @Autowired
//    PostContentRepository postContentRepository;
//
//    @Autowired
//    LikeRepository likeRepository;
//
//    @Autowired
//    StarRepository starRepository;
//
//    @Autowired
//    SeriesRepository seriesRepository;
//
//
//    @GetMapping("/uploadPost")
//    public String getAnalysis(ModelMap model, HttpServletRequest request) throws Exception {
//        if (request.getSession().getAttribute("username") == null) {
//            return "index";
//        }
//        // Find Current User
//        String username = (String) request.getSession().getAttribute("username");
//        User user = userRepository.findById(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//        model.addAttribute("User", user);
//        return "uploadPost";
//    }
//
//    @GetMapping("/drawing")
//    public String goToDrawing(ModelMap model, HttpServletRequest request) throws Exception {
//        if (request.getSession().getAttribute("username") == null) {
//            return "index";
//        }
//        // Find Current User
//        String username = (String) request.getSession().getAttribute("username");
//        User user = userRepository.findById(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//        model.addAttribute("User", user);
//        return "drawing";
//    }
//
//
//
//    @ResponseBody
//    @PostMapping("/changeuploadimgs")
//    public List<String> changeAvatar(@RequestParam("file") MultipartFile[] file,
//                               ModelMap model, HttpServletRequest request,
//                               RedirectAttributes redirectAttributes) throws Exception{
//
//        if (file.length==0){
//            return Arrays.asList("No images selected!");
//        }
//        else if(file.length>9){
//            return Arrays.asList("You can have at most 9 images in one post!");
//        }
//        else {
//            ArrayList<String> imgs = new ArrayList<>();
//            for(MultipartFile each : file){
//                String type = each.getOriginalFilename().substring(each.getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
//                type = type.substring(1);
//                if(!type.equals("jpg") && !type.equals("png")){
//                    return Arrays.asList("Only .png or .jpg is accepted!");
//                }
//                File convFile = new File(each.getOriginalFilename());
//                convFile.createNewFile();
//                FileOutputStream fos = new FileOutputStream(convFile);
//                fos.write(each.getBytes());
//                fos.close();
//
//                BufferedImage bImage = ImageIO.read(convFile);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                ImageIO.write(bImage, type, bos );
//                byte [] data = bos.toByteArray();
//                String base64 = Base64.getEncoder().encodeToString(data);
//                imgs.add(base64);
//
//            }
//            imgs.add(0,"Images added!");
//            return imgs;
//        }
//
//    }
//
//    @ResponseBody
//    @PostMapping("/createpost")
//    public String createpost(@RequestParam("file") MultipartFile[] file,
//                                   @RequestParam("Comment") String comment,
//                                     ModelMap model, HttpServletRequest request,
//                                     RedirectAttributes redirectAttributes) throws Exception{
//
//
////        System.out.println(file.length+"32132131321231");
//        if (file.length==1 && file[0].getOriginalFilename().equals("")){
//            return ("No images selected!");
//        }
//        else if(file.length>9){
//            return ("You can have at most 9 images in one post!");
//        }
//        else {
//            User user = userRepository.findById((String)(request.getSession().getAttribute("username"))).get();
//            Post newpost = new Post();
//            newpost.setUser(user);
//            newpost.setPostComment(comment);
////            System.out.println(newpost.getPostID());
//            newpost.setOriginalPostID(newpost.getPostID());
//            postRepository.save(newpost);
//
//            Post newpost2 = (postRepository.findById(newpost.getPostID()).get());
//            newpost2.setOriginalPostID(newpost2.getPostID());
//            postRepository.save(newpost2);
//
//            for(MultipartFile each : file){
//                String type = each.getOriginalFilename().substring(each.getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
//                type = type.substring(1);
//                if(!type.equals("jpg") && !type.equals("png")){
//                    return ("Only .png or .jpg is accepted!");
//                }
//                File convFile = new File(each.getOriginalFilename());
//                convFile.createNewFile();
//                FileOutputStream fos = new FileOutputStream(convFile);
//                fos.write(each.getBytes());
//                fos.close();
//
//                BufferedImage bImage = ImageIO.read(convFile);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                ImageIO.write(bImage, type, bos );
//                byte [] data = bos.toByteArray();
//                String base64 = Base64.getEncoder().encodeToString(data);
//
//                Work newwork = new Work();
//                newwork.setUser(user);
//                newwork.setContent(base64);
//                workRepository.save(newwork);
//
//                PostContent newpostcontent = new PostContent();
//                PostContentIndentity newpostcontentid = new PostContentIndentity();
//                newpostcontentid.setPost(newpost);
//                newpostcontentid.setWork(newwork);
//                newpostcontent.setPostIndentity(newpostcontentid);
//                postContentRepository.save(newpostcontent);
//
//            }
//            return "Upload Success!";
//        }
//
//    }
//
//}