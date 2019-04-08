package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "post_Content")
@EntityListeners(AuditingEntityListener.class)
public class PostContent implements Serializable {

    @EmbeddedId
    PostContentIndentity postIndentity;

//    @MapsId("postID")
//    @ManyToOne(fetch=FetchType.LAZY, optional=false)
//    @JoinColumn(name="post_ID", referencedColumnName = "postid")
//    private Post post;

//    @MapsId("workID")
//    @ManyToOne
//    @JoinColumn(name="work_ID", referencedColumnName = "workid")
//    private Work work;


    //    @ManyToOne(optional = false)
//    @JoinColumns(value = {
//            @JoinColumn(name = "postID", referencedColumnName = "postID"),
//            @JoinColumn(name = "workID", referencedColumnName = "workID") })
//    private Post post;


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

