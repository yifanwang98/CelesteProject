package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesContentIndentity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "series_content")
@EntityListeners(AuditingEntityListener.class)
public class SeriesContent implements Serializable {

    @EmbeddedId
    private SeriesContentIndentity seriesContentIndentity;


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

    public SeriesContentIndentity getSeriesContentIndentity() {
        return seriesContentIndentity;
    }

    public void setSeriesContentIndentity(SeriesContentIndentity seriesContentIndentity) {
        this.seriesContentIndentity = seriesContentIndentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesContent)) return false;
        SeriesContent that = (SeriesContent) o;
        return Objects.equals(getSeriesContentIndentity(), that.getSeriesContentIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeriesContentIndentity());
    }
}

