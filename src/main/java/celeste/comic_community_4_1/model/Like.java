package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "likes")
@EntityListeners(AuditingEntityListener.class)
public class Like implements Serializable {

    @EmbeddedId
    private PostIndentity postIndentity;

    public PostIndentity getPostIndentity() {
        return postIndentity;
    }

    public void setPostIndentity(PostIndentity postIndentity) {
        this.postIndentity = postIndentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like)) return false;
        Like like = (Like) o;
        return Objects.equals(getPostIndentity(), like.getPostIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPostIndentity());
    }
}

