package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "follow")
@EntityListeners(AuditingEntityListener.class)
public class Follow implements Serializable {

    @EmbeddedId
    private FollowIndentity followIndentity;

    public FollowIndentity getFollowIndentity() {
        return followIndentity;
    }

    public void setFollowIndentity(FollowIndentity followIndentity) {
        this.followIndentity = followIndentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Follow)) return false;
        Follow follow = (Follow) o;
        return Objects.equals(getFollowIndentity(), follow.getFollowIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFollowIndentity());
    }
}
