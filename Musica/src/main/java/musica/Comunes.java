package musica;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="Comunes")
public class Comunes implements Serializable{
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="musicaPublica")
    private Musica musicaPublica;

	public Musica getMusicaPublica() {
		return musicaPublica;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMusicaPublica(Musica musicaPublica) {
		this.musicaPublica = musicaPublica;
	}

	public Comunes() {
		super();
	}
    
}
