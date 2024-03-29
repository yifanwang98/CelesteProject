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
import java.util.Objects;

@Entity
@Table(name = "work")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
public class Work implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long workID;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "username")
    private User user;

    @NotBlank
    private String name="default";

    @NotBlank
    private String genre="default";

    @Lob
    @NotBlank
    private String content;

    @Lob
    @NotBlank
    private String thumbnail;


    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt=new Date();

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date updatedAt=new Date();

    public long getWorkID() {
        return workID;
    }

    public void setWorkID(long workID) {
        this.workID = workID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Work)) return false;
        Work work = (Work) o;
        return getWorkID() == work.getWorkID() &&
                Objects.equals(getUser(), work.getUser()) &&
                Objects.equals(getName(), work.getName()) &&
                Objects.equals(getGenre(), work.getGenre()) &&
                Objects.equals(getContent(), work.getContent()) &&
                Objects.equals(getThumbnail(), work.getThumbnail()) &&
                Objects.equals(getCreatedAt(), work.getCreatedAt()) &&
                Objects.equals(getUpdatedAt(), work.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkID(), getUser(), getName(), getGenre(), getContent(), getThumbnail(), getCreatedAt(), getUpdatedAt());
    }
}
