package celeste.comic_community_4_1.model.EmbeddedClasses;

import celeste.comic_community_4_1.model.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowIndentity implements Serializable {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "username1")
    private User userone;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "username2")
    private User usertwo;

    public User getUser1() {
        return userone;
    }

    public void setUser1(User user1) {
        this.userone = user1;
    }

    public User getUser2() {
        return usertwo;
    }

    public void setUser2(User user2) {
        this.usertwo = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowIndentity that = (FollowIndentity) o;
        return Objects.equals(userone, that.userone) &&
                Objects.equals(usertwo, that.usertwo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userone, usertwo);
    }
}
