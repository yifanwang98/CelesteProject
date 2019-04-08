package celeste.comic_community_4_1.model.EmbeddedClasses;

import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.Work;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SeriesContentIndentity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "seriesID")
    private Series series;

    @ManyToOne
    @JoinColumn(name = "workID")
    private Work work;

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesContentIndentity)) return false;
        SeriesContentIndentity that = (SeriesContentIndentity) o;
        return Objects.equals(getSeries(), that.getSeries()) &&
                Objects.equals(getWork(), that.getWork());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeries(), getWork());
    }
}
