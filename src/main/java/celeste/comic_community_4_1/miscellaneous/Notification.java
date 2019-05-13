package celeste.comic_community_4_1.miscellaneous;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Notification {

    public final NotificationType type;
    public final String fromWhom;
    public final Date time;
    public final String comment;
    public final long postOrSeriesID;
    public final long repostID;

    public Notification(NotificationType type, String fromWhom, Date time) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.comment = null;
        this.postOrSeriesID = 0;
        this.repostID = -1;
    }

    public Notification(NotificationType type, String fromWhom, Date time, long postOrSeriesID) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.postOrSeriesID = postOrSeriesID;
        this.comment = null;
        this.repostID = -1;
    }

    public Notification(NotificationType type, String fromWhom, Date time, long postOrSeriesID, String comment) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.comment = comment;
        this.postOrSeriesID = postOrSeriesID;
        this.repostID = -1;
    }

    public Notification(NotificationType type, String fromWhom, Date time, long postOrSeriesID, long repostID, String comment) {
        this.type = type;
        this.fromWhom = fromWhom;
        this.time = time;
        this.comment = comment;
        this.postOrSeriesID = postOrSeriesID;
        this.repostID = repostID;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY", Locale.ENGLISH);
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private static SimpleDateFormat datetimeFormat = new SimpleDateFormat("MMM dd, YYYY HH:mm", Locale.ENGLISH);

    public String getDateString() {
        Calendar c = Calendar.getInstance();
        // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (this.time.before(c.getTime())) {
            synchronized (dateFormat) {
                return dateFormat.format(this.time);
            }
        } else {
            synchronized (dateFormat) {
                return timeFormat.format(this.time);
            }
        }
    }

    public static Date getToday() {
        Calendar c = Calendar.getInstance();
        // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getDaysBefore(int before) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, before * -1);
        return c.getTime();
    }

    public static String getDateString(Date time) {
        synchronized (datetimeFormat) {
            return datetimeFormat.format(time);
        }
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
