package celeste.comic_community_4_1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "series")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt"},
        allowGetters = true)
public class Series implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seriesID;

    @NotBlank
    private String seriesName = "newSeries";

    @NotNull
    private String description = "";

    @NotNull
    private String primaryGenre = "None";

    @NotNull
    private String secondaryGenre = "None";


    @NotNull
    private boolean isPublicEditing = false;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt = new Date();

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "createdBy")
    private User user;

    @Lob
    @NotBlank
    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPrimaryGenre() {
        return primaryGenre;
    }

    public void setPrimaryGenre(String primaryGenre) {
        this.primaryGenre = primaryGenre;
    }

    public String getSecondaryGenre() {
        return secondaryGenre;
    }

    public void setSecondaryGenre(String secondaryGenre) {
        this.secondaryGenre = secondaryGenre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSeriesID() {
        return seriesID;
    }

    public void setSeriesID(long seriesID) {
        this.seriesID = seriesID;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public boolean isPublicEditing() {
        return isPublicEditing;
    }

    public void setPublicEditing(boolean publicEditing) {
        isPublicEditing = publicEditing;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

