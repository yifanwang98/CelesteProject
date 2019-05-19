package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "post_Content")
@EntityListeners(AuditingEntityListener.class)
public class PostContent implements Serializable {

    @EmbeddedId
    PostContentIndentity postIndentity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostContent)) return false;
        PostContent that = (PostContent) o;
        return Objects.equals(getPostIndentity(), that.getPostIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPostIndentity());
    }

    public PostContentIndentity getPostIndentity() {
        return postIndentity;
    }

    public void setPostIndentity(PostContentIndentity postIndentity) {
        this.postIndentity = postIndentity;
    }
}

