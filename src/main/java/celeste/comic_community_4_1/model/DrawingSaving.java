package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.model.EmbeddedClasses.DrawingSavingIndentity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "DrawingSave")
@EntityListeners(AuditingEntityListener.class)
public class DrawingSaving implements Serializable {

    @EmbeddedId
    private DrawingSavingIndentity drawingSavingIndentity;

    public DrawingSavingIndentity getDrawingSavingIndentity() {
        return drawingSavingIndentity;
    }

    public void setDrawingSavingIndentity(DrawingSavingIndentity drawingSavingIndentity) {
        this.drawingSavingIndentity = drawingSavingIndentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawingSaving)) return false;
        DrawingSaving that = (DrawingSaving) o;
        return Objects.equals(getDrawingSavingIndentity(), that.getDrawingSavingIndentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDrawingSavingIndentity());
    }
}

