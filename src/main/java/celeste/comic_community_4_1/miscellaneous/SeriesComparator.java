package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Series;

import java.util.Comparator;

public class SeriesComparator implements Comparator<Series> {

    @Override
    public int compare(Series o1, Series o2) {
        int i = o2.getCreatedAt().compareTo(o1.getCreatedAt());
        if (i == 0) {
            if (o1.getSeriesID() < o2.getSeriesID()) {
                return -1;
            } else if (o1.getSeriesID() == o2.getSeriesID()) {
                return 0;
            } else {
                return 1;
            }
        }
        return i;
    }

}
