package celeste.comic_community_4_1.model.EmbeddedClasses;

import celeste.comic_community_4_1.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Embeddable
public class DrawingSavingIndentity implements Serializable {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "username")
    private User userone;


    @NotBlank
//    @Lob
    private String drawing;

    public User getUserone() {
        return userone;
    }

    public void setUserone(User userone) {
        this.userone = userone;
    }

    public String getDrawing() {
        return drawing;
    }

    public void setDrawing(String drawing) {
        this.drawing = drawing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawingSavingIndentity)) return false;
        DrawingSavingIndentity that = (DrawingSavingIndentity) o;
        return Objects.equals(getUserone(), that.getUserone()) &&
                Objects.equals(getDrawing(), that.getDrawing());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserone(), getDrawing());
    }
}
