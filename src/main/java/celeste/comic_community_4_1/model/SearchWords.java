package celeste.comic_community_4_1.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "SearchWords")
@EntityListeners(AuditingEntityListener.class)
public class SearchWords implements Serializable, Comparable<SearchWords> {
    @Id
    @NotNull
    private String word = "";

    @NotNull
    private double heat = 0.0;

    public String getWord() {
        return word;
    }

    public void setWord(String searchWord) {
        word = searchWord;
    }

    public double getHeat() {
        return heat;
    }

    public void setHeat(double heat) {
        this.heat = heat;
    }

    @Override
    public int compareTo(SearchWords o) {
        return Double.compare(o.getHeat(), this.heat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchWords that = (SearchWords) o;
        return Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}
