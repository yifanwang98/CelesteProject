package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesFollowIndentity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt = new Date();

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

