package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesFollowIndentity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "series_follow")
@EntityListeners(AuditingEntityListener.class)
public class SeriesFollow implements Serializable {

    @EmbeddedId
    private SeriesFollowIndentity seriesFollowIndentity;


    public SeriesFollowIndentity getSeriesFollowIndentity() {
        return seriesFollowIndentity;
    }

    public void setSeriesFollowIndentity(SeriesFollowIndentity seriesFollowIndentity) {
        this.seriesFollowIndentity = seriesFollowIndentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesFollow)) return false;
        SeriesFollow that = (SeriesFollow) o;
        return Objects.equals(getSeriesFollowIndentity(), that.getSeriesFollowIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeriesFollowIndentity());
    }
}

