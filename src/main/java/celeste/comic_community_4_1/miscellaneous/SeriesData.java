package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Series;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SeriesData {
    public final Series series;
    public final List<String> tags;

    public final long subscriptionCount;

    public final boolean subscribed;
    public final boolean owner;

    public SeriesData(Series series, List<String> tags, long subscriptionCount, boolean subscribed, boolean owner) {
        this.series = series;
        this.tags = tags;
        this.subscriptionCount = subscriptionCount;
        this.subscribed = subscribed;
        this.owner = owner;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY", Locale.ENGLISH);

    public String creationDate() {
        synchronized (dateFormat) {
            return dateFormat.format(this.series.getCreatedAt());
        }
    }
}
