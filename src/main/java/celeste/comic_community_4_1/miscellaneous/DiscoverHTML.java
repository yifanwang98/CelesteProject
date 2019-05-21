package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Series;

public class DiscoverHTML {

    public static final String generateHTML(PostData postData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"home-posts-card\">");

        sb.append("<table style=\"border-spacing: 0em; padding-top: 0.75em; margin-bottom: 0.5em;\" width=\"100%\"><tr><td class=\"post-avatar\"><a href=\"view_profile?user=");
        sb.append(postData.post.getUser().getUsername()).append("\">");

        sb.append("<image class=\"post-avatar-image\" src=\"data:image/png;base64,");
        sb.append(postData.post.getUser().getAvatar()).append("\"></image></a></td>");

        sb.append("<td><table width=\"100%\"><tr><td><span class=\"post-author\">");
        sb.append(postData.post.getUser().getUsername()).append("</span><br><span class=\"post-time\">");
        sb.append(postData.postCreatedAt()).append("</span></td>");

        sb.append("<td width=\"10%\" style=\"vertical-align: top; text-align: right; padding-right: 1em; min-width: 10em;\"></td></tr></table></td></tr>");

        sb.append("<tr><td></td><td style=\"word-break: break-word;\">");

        if (!postData.post.getPostComment().isEmpty()) {
            sb.append("<div class=\"post-text\">");
            sb.append(postData.post.getPostComment()).append("</div>");
        }

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
            sb.append(tag).append(" </span></a>");
        }

        sb.append("</td></tr>");

        if (!postData.fromSeries.isEmpty()) {
            sb.append("<tr><td></td><td style=\"word-break: break-word;\">");
            sb.append("<span class=\"post-genre semibold\">From Series: </span>");

            for (Series fromSeries : postData.fromSeries) {
                sb.append("<a class=\"post-genre-text\" ").append("href=\"singleSeries?id=");
                sb.append(fromSeries.getSeriesID()).append("&index=0\">");
                sb.append(fromSeries.getSeriesName()).append("</a>");
            }

            sb.append("</td></tr>");
        }

        sb.append("<tr><td></td><td>");

        for (String work : postData.images) {
            sb.append("<a href=\"singlePost?id=").append(postData.post.getPostID()).append("\" class=\"imageLink\">");
            sb.append("<image class=\"post-image-single\" ").append("src=\"data:image/png;base64,").append(work);
            sb.append("\"></image></a>");
        }

        sb.append("</td></tr></table><div style=\"padding: 0.1em;\"></div></div>");
        return sb.toString();
    }
}
