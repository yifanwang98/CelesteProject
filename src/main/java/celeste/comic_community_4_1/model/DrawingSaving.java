package celeste.comic_community_4_1.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "DrawingSave")
@EntityListeners(AuditingEntityListener.class)
public class DrawingSaving implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long drawsaveID;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "username")
    private User userone;


    @NotBlank
    @Lob
    private String drawing;

    public long getDrawsaveID() {
        return drawsaveID;
    }

    public void setDrawsaveID(long drawsaveID) {
        this.drawsaveID = drawsaveID;
    }

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
        if (!(o instanceof DrawingSaving)) return false;
        DrawingSaving that = (DrawingSaving) o;
        return getDrawsaveID() == that.getDrawsaveID() &&
                Objects.equals(getUserone(), that.getUserone()) &&
                Objects.equals(getDrawing(), that.getDrawing());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDrawsaveID(), getUserone(), getDrawing());
    }
}

