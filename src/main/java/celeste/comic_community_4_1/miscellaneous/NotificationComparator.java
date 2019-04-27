package celeste.comic_community_4_1.miscellaneous;

import java.util.Comparator;

public class NotificationComparator implements Comparator<Notification> {

    @Override
    public int compare(Notification o1, Notification o2) {
        return o2.time.compareTo(o1.time);
    }

}
