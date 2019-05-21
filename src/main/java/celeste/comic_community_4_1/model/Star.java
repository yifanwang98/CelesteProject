package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "star")
@EntityListeners(AuditingEntityListener.class)
public class Star implements Serializable {

    @EmbeddedId
    private PostIndentity postIndentity;


    public PostIndentity getPostIndentity() {
        return postIndentity;
    }

    public void setPostIndentity(PostIndentity postIndentity) {
        this.postIndentity = postIndentity;
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
        if (!(o instanceof Star)) return false;
        Star star = (Star) o;
        return Objects.equals(getPostIndentity(), star.getPostIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPostIndentity());
    }
}

