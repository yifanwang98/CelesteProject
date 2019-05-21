package celeste.comic_community_4_1.model;

import celeste.comic_community_4_1.miscellaneous.ThumbnailConverter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Genre")
@EntityListeners(AuditingEntityListener.class)
public class Genre implements Serializable, Comparable<Genre> {

    @Id
    @NotBlank
    private String genre;

    @NotNull
    private long count = 0;

    @NotNull
    @Lob
    private String images = ThumbnailConverter.DEFAULT_SERIES_COVER;

    public Genre() {
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public int compareTo(Genre o) {
        return Long.compare(o.count, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre1 = (Genre) o;
        return Objects.equals(genre, genre1.genre);
    }

    @Override
    public int hashCode() {

        return Objects.hash(genre);
    }
}

