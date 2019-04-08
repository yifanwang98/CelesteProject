package celeste.comic_community_4_1.model.EmbeddedClasses;

import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SeriesFollowIndentity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "seriesID")
    private Series series;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesFollowIndentity)) return false;
        SeriesFollowIndentity that = (SeriesFollowIndentity) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getSeries(), that.getSeries());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getSeries());
    }
}
