package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.SeriesFollow;

import java.util.Comparator;

public class SubscriptionComparator implements Comparator<SeriesFollow> {

    @Override
    public int compare(SeriesFollow o1, SeriesFollow o2) {
        int i = o2.getCreatedAt().compareTo(o1.getCreatedAt());
        if (i == 0) {
            if (o1.getSeriesFollowIndentity().getSeries().getSeriesID() < o2.getSeriesFollowIndentity().getSeries().getSeriesID()) {
                return -1;
            } else if (o1.getSeriesFollowIndentity().getSeries().getSeriesID() == o2.getSeriesFollowIndentity().getSeries().getSeriesID()) {
                return 0;
            } else {
                return 1;
            }
        }
        return i;
    }

}
