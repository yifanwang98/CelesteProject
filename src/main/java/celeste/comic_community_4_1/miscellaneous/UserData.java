package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.User;

public class UserData {
    public final User user;
    public final boolean followed;

    public UserData(User user, boolean followed) {
        this.user = user;
        this.followed = followed;
    }
}
