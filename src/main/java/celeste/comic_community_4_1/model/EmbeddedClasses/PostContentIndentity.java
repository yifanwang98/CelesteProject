package celeste.comic_community_4_1.model.EmbeddedClasses;

import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.Work;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostContentIndentity implements Serializable {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "postID")
    private Post post;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "workID")
    private Work work;



    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostContentIndentity)) return false;
        PostContentIndentity that = (PostContentIndentity) o;
        return Objects.equals(getPost(), that.getPost()) &&
                Objects.equals(getWork(), that.getWork());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPost(), getWork());
    }
}
