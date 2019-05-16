package celeste.comic_community_4_1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "post")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt"},
        allowGetters = true)
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postID;

    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "username")
    private User user;


    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt = new Date();


    @NotNull
    private long viewCount = 0;

    @NotNull
    private String postComment = "";

    @NotNull
    private boolean isRepost = false;

    @NotNull
    private long originalPostID = postID;

    @NotNull
    private String primaryGenre = "None";

    @NotNull
    private String secondaryGenre = "None";

    public long getPostID() {
        return postID;
    }

    public void setPostID(long postID) {
        this.postID = postID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public String getPostComment() {
        return postComment;
    }

    public void setPostComment(String postComment) {
        this.postComment = postComment;
    }

    public boolean isRepost() {
        return isRepost;
    }

    public void setRepost(boolean repost) {
        isRepost = repost;
    }

    public long getOriginalPostID() {
        return originalPostID;
    }

    public void setOriginalPostID(long originalPostID) {
        this.originalPostID = originalPostID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postID == post.postID;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postID);
    }
}
