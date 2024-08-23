package app1;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

@Service
public class DataService {
	ArrayList<Usuario> listaUsu = new ArrayList<>();
	public DataService() {		
		for(int i=0; i<5; i++) {
			listaUsu.add(new Usuario("Usuario con codigo "+i, "correo", i));
		}
	}
	
	public ArrayList<Usuario> getListaUsu() {
		return listaUsu;
	}
	public void setListaUsu(ArrayList<Usuario> listaUsu) {
		this.listaUsu = listaUsu;
	}
	
	
}
