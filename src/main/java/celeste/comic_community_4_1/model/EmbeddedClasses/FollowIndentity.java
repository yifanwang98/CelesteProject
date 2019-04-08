package celeste.comic_community_4_1.model.EmbeddedClasses;

import celeste.comic_community_4_1.model.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowIndentity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "username1")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "username2")
    private User user2;

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowIndentity)) return false;
        FollowIndentity that = (FollowIndentity) o;
        return Objects.equals(getUser1(), that.getUser1()) &&
                Objects.equals(getUser2(), that.getUser2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser1(), getUser2());
    }
}
