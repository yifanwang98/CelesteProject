package celeste.comic_community_4_1.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "SearchWords")
@EntityListeners(AuditingEntityListener.class)
public class SearchWords implements Serializable {
    @Id
    private String SearchWord;

    @NotNull
    private long heat = 1;

    public String getSearchWord() {
        return SearchWord;
    }

    public void setSearchWord(String searchWord) {
        SearchWord = searchWord;
    }

    public long getHeat() {
        return heat;
    }

    public void setHeat(long heat) {
        this.heat = heat;
    }
}
