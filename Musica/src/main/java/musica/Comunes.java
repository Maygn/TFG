package musica;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comunes")
public class Comunes {

    @Id
    private Long id = 1L; // MongoDB lo usará como _id

    private Musica musicaPublica;

    public Comunes() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Musica getMusicaPublica() {
        return musicaPublica;
    }

    public void setMusicaPublica(Musica musicaPublica) {
        this.musicaPublica = musicaPublica;
    }
}
