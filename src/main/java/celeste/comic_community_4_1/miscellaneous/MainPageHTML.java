package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.User;

public class MainPageHTML {

    public static String generateHTML(PostData postData, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"home-posts-card\">");
        sb.append("<table style=\"border-spacing: 0em; padding-top: 0.75em; margin-bottom: 0.5em;\" width=\"100%\">");
        sb.append("<tr><td class=\"post-avatar\">");

        sb.append("<a href=\"view_profile?user=").append(postData.post.getUser().getUsername()).append("\">");
        sb.append("<image src=\"data:image/png;base64,").append(postData.post.getUser().getAvatar()).append("\" ");
        sb.append("class=\"post-avatar-image\"></image></a></td>");
        sb.append("<td><table width=\"100%\"><tr><td><a class=\"post-author\" ");
        sb.append("href=\"view_profile?user=").append(postData.post.getUser().getUsername()).append("\">");
        sb.append(postData.post.getUser().getUsername()).append("</a><br>");

        sb.append("<span class=\"post-time\">").append(postData.postCreatedAt()).append("</span></td>");

        sb.append("<td width=\"10%\" style=\"vertical-align: top; text-align: right; padding-right: 1em; min-width: 10em;\">");
        if (postData.starred) {
            sb.append("<span class=\"post-action-stared-top\"><i class='fas fa-star'></i></span>");
        }
        if (postData.liked) {
            sb.append("<span class=\"post-action-liked-top\"><i class='fas fa-thumbs-up'></i></span>");
        }
        if (!user.getUsername().equals(postData.post.getUser().getUsername())) {
            sb.append("<div class=\"dropdown\" hidden><button class=\"dropbtn\"><i class='fas fa-ellipsis-v'></i></button>");
            sb.append("<div class=\"dropdown-content\"><a href=\"javascript:reportAPost(");
            sb.append(postData.post.getPostID()).append(");\">");
            sb.append("<i class=\"fas fa-exclamation-triangle\"></i> Report</a></div></div>");
        }

        sb.append("</td></tr></table></td></tr>");

        sb.append("<tr><td></td><td style=\"word-break: break-word;\">");

        if (!postData.post.getPostComment().isEmpty()) {
            sb.append("<div class=\"post-text\">");
            sb.append(postData.post.getPostComment()).append("</div>");
        }

        if (!postData.post.isRepost()) {
            sb.append("<div class=\"post-genre\">");
            if (!postData.post.getPrimaryGenre().equalsIgnoreCase("None")) {
                sb.append("<a class=\"post-genre-text\" href=\"genre?genre=");
                sb.append(postData.post.getPrimaryGenre()).append("\"><i class='far fa-bookmark'></i> ");
                sb.append(postData.post.getPrimaryGenre()).append(" </a>");
            }

            if (!postData.post.getSecondaryGenre().equalsIgnoreCase("None")) {
                sb.append("<a class=\"post-genre-text\" href=\"genre?genre=");
                sb.append(postData.post.getSecondaryGenre()).append("\"><i class='far fa-bookmark'></i> ");
                sb.append(postData.post.getSecondaryGenre()).append(" </a>");
            }
            sb.append("</div>");

            for (String tag : postData.postTags) {
                sb.append("<a style=\"text-decoration: none\" href=\"/hashtag?tag=");
                sb.append(tag).append("\"><span class=\"post-genre-text\">#");
                sb.append(tag).append("</span> </a>");
            }
        }
        sb.append("</td></tr>");

        if (!postData.fromSeries.isEmpty() && !postData.post.isRepost()) {
            sb.append("<tr><td></td><td style=\"word-break: break-word;\">");
            sb.append("<span class=\"post-genre semibold\">From Series: </span>");

            for (Series fromSeries : postData.fromSeries) {
                sb.append("<a class=\"post-genre-text\" ").append("href=\"singleSeries?id=");
                sb.append(fromSeries.getSeriesID()).append("&index=0\">");
                sb.append(fromSeries.getSeriesName()).append(" </a>");
            }

            sb.append("</td></tr>");
        }

        sb.append("<tr><td></td><td>");

        if (postData.post.isRepost()) {
            sb.append(getRepostHTML(postData, user));
        } else {
            sb.append("<div>");
            for (String work : postData.images) {
                sb.append("<a href=\"singlePost?id=").append(postData.post.getPostID()).append("\" class=\"imageLink\">");
                sb.append("<image class=\"post-image-single\" ").append("src=\"data:image/png;base64,").append(work);
                sb.append("\"></image></a>");
            }
            sb.append("</div>");
        }

        sb.append("</td></tr></table><!-- Old 转赞评 --><div style=\"padding: 0.1em;\"></div></div>");
        return sb.toString();
    }

    private static String getRepostHTML(PostData postData, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"border-spacing: 0em; padding-top: 0.5em; margin-bottom: 0.5em; width:100%; border-left: 2px solid lightgrey;\">");

        sb.append("<tr><td class=\"post-avatar\">");

        sb.append("<a href=\"view_profile?user=").append(postData.originalPost.getUser().getUsername()).append("\">");
        sb.append("<image src=\"data:image/png;base64,").append(postData.originalPost.getUser().getAvatar()).append("\" ");
        sb.append("class=\"post-avatar-image\"></image></a></td>");
        sb.append("<td><table width=\"100%\"><tr><td><a class=\"post-author\" ");
        sb.append("href=\"view_profile?user=").append(postData.originalPost.getUser().getUsername()).append("\">");
        sb.append(postData.originalPost.getUser().getUsername()).append("</a><br>");

        sb.append("<span class=\"post-time\">").append(postData.postCreatedAt()).append("</span></td>");

        sb.append("<td></td></tr></table></td></tr>");

        sb.append("<tr><td></td><td style=\"word-break: break-word;\">");

        if (!postData.originalPost.getPostComment().isEmpty()) {
            sb.append("<div class=\"post-text\">");
            sb.append(postData.originalPost.getPostComment()).append("</div>");
        }

        if (postData.post.isRepost()) {
            sb.append("<div class=\"post-genre\">");
            if (!postData.originalPost.getPrimaryGenre().equalsIgnoreCase("None")) {
                sb.append("<a class=\"post-genre-text\" href=\"genre?genre=");
                sb.append(postData.originalPost.getPrimaryGenre()).append("\"><i class='far fa-bookmark'></i> ");
                sb.append(postData.originalPost.getPrimaryGenre()).append(" </a>");
            }

            if (!postData.originalPost.getSecondaryGenre().equalsIgnoreCase("None")) {
                sb.append("<a class=\"post-genre-text\" href=\"genre?genre=");
                sb.append(postData.originalPost.getSecondaryGenre()).append("\"><i class='far fa-bookmark'></i> ");
                sb.append(postData.originalPost.getSecondaryGenre()).append(" </a>");
            }
            sb.append("</div>");

            for (String tag : postData.postTags) {
                sb.append("<a style=\"text-decoration: none\" href=\"/hashtag?tag=");
                sb.append(tag).append("\"><span class=\"post-genre-text\">#");
                sb.append(tag).append(" </span></a>");
            }
        }
        sb.append("</td></tr>");

        if (!postData.fromSeries.isEmpty() && postData.post.isRepost()) {
            sb.append("<tr><td></td><td style=\"word-break: break-word;\">");
            sb.append("<span class=\"post-genre semibold\">From Series: </span>");

            for (Series fromSeries : postData.fromSeries) {
                sb.append("<a class=\"post-genre-text\" ").append("href=\"singleSeries?id=");
                sb.append(fromSeries.getSeriesID()).append("&index=0\">");
                sb.append(fromSeries.getSeriesName()).append(" </a>");
            }

            sb.append("</td></tr>");
        }

        sb.append("<tr><td></td><td><div>");
        for (String work : postData.images) {
            sb.append("<a href=\"singlePost?id=").append(postData.post.getPostID()).append("\" ");
            sb.append("class=\"imageLink\"><image src=\"data:image/png;base64,");
            sb.append(work).append("\" class=\"post-image-multiple\"></image></a>");
        }

        sb.append("</div></td></tr></table></div>");
        return sb.toString();
    }
}
