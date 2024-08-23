package Prac;

import java.util.ArrayList;
import java.util.Scanner;

public class ComprobarContador {
	public static void contadorPalabras() {

		ArrayList<String> listaPalabras = new ArrayList<>();
		Scanner sc = new Scanner(System.in);
		boolean fuera=false;
		// Pedir palabra
		
	while(!fuera) {	
		System.out.println("Introduzca palabra");
		String palabra = sc.nextLine();

		// Guardar palabra en el array
		listaPalabras.add(palabra);

		// Comprobar si la ultima palabra añadida es igual a la primera palabra añadida.
		if (listaPalabras.size()>1 && listaPalabras.get(0).equals(listaPalabras.get(listaPalabras.size() - 1))) {
			// Mostrar todas las palabras
			mostrarPalabras(listaPalabras);
			// terminar el programa
			fuera=true;
			
		}
	}

	}

	public static void mostrarPalabras(ArrayList<String> aMostrar) {
		for(int i=0;i<aMostrar.size();i++) {
			System.out.print(aMostrar.get(i)+" ");
		}
	}
}
