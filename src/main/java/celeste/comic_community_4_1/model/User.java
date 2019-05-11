package celeste.comic_community_4_1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
public class User implements Serializable {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String gender;

    @NotBlank
    private String email;

    @Lob
    @NotBlank
    //We use string from byte[] to show image in the html
    private String avatar;

    @NotBlank
    private String blockStatus="none";

    @NotBlank
    private String membership="none";

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt=new Date();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date lastViewNotification = new Date();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date blockedSince = new Date();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(String blockStatus) {
        this.blockStatus = blockStatus;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastViewNotification() {
        return lastViewNotification;
    }

    public void setLastViewNotification(Date lastViewNotification) {
        this.lastViewNotification = lastViewNotification;
    }

    public Date getBlockedSince() {
        return blockedSince;
    }

    public void setBlockedSince(Date blockedSince) {
        this.blockedSince = blockedSince;
    }
}
