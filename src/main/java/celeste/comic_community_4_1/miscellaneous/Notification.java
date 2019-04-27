package celeste.comic_community_4_1.miscellaneous;

import java.util.Date;

public class Notification {

    public final NotificationType type;
    public final String fromWhom;
    public final Date time;
    public final String comment;
    public final long postOrSeriesID;

    public Notification(NotificationType type, String fromWhom, Date time) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.comment = null;
        this.postOrSeriesID = 0;
    }

    public Notification(NotificationType type, String fromWhom, Date time, long postOrSeriesID) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.postOrSeriesID = postOrSeriesID;
        this.comment = null;
    }

    public Notification(NotificationType type, String fromWhom, Date time, long postOrSeriesID, String comment) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.comment = comment;
        this.postOrSeriesID = postOrSeriesID;
    }

    @Override
    public String toString() {
        switch (type) {
            case LIKE:
                return " liked your post ";
            case STAR:
                return " starred your post ";
            case SHARE:
                return " shared your post ";
            case FOLLOW:
                return " starts following you ";
            case COMMENT:
                return " commented your post ";
            case SUBSCRIBE:
                return " subscribed your series ";
            default:
                return "[Unknown Notification Type]";
        }
    }
}