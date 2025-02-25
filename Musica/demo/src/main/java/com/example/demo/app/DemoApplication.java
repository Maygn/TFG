package com.example.demo.app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.app.ColeccionJson;
import com.example.demo.app.ColeccionJsonRepository;

@SpringBootApplication
public class DemoApplication  implements CommandLineRunner {
	@Autowired
	ColeccionJsonRepository crepo;
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ColeccionJson a=new ColeccionJson();
		a.setContenido("{\n"
				+ "        a1: {\n"
				+ "          11: [\"string1\", \"string2\", \"string3\"],\n"
				+ "          12: [\"string4\", \"string5\", \"string6\"],\n"
				+ "          13: [\"string7\", \"string8\", \"string9\"],\n"
				+ "          14: [\"string10\", \"string11\", \"string12\"],\n"
				+ "          15: [\"string13\", \"string14\", \"string15\"],\n"
				+ "        },\n"
				+ "        a2: {\n"
				+ "          21: {\n"
				+ "            211: [\"String extra 1\", \"String extra 2\"],\n"
				+ "            212: [\"String extra doble 1\", \"String extra doble 2\"],\n"
				+ "          },\n"
				+ "          22: [\"string19\", \"string20\", \"string21\"],\n"
				+ "          23: [\"string22\", \"string23\", \"string24\"],\n"
				+ "          24: [\"string25\", \"string26\", \"string27\"],\n"
				+ "          25: [\"string28\", \"string29\", \"string30\"],\n"
				+ "        },\n"
				+ "        a3: {\n"
				+ "          31: [\"string31\", \"string32\", \"string33\"],\n"
				+ "          32: [\"string34\", \"string35\", \"string36\"],\n"
				+ "          33: [\"string37\", \"string38\", \"string39\"],\n"
				+ "          34: [\"string40\", \"string41\", \"string42\"],\n"
				+ "          35: [\"string43\", \"string44\", \"string45\"],\n"
				+ "        },\n"
				+ "        a4: {\n"
				+ "          41: [\"string46\", \"string47\", \"string48\"],\n"
				+ "          42: [\"string49\", \"string50\", \"string51\"],\n"
				+ "          43: [\"string52\", \"string53\", \"string54\"],\n"
				+ "          44: [\"string55\", \"string56\", \"string57\"],\n"
				+ "          45: [\"string58\", \"string59\", \"string60\"],\n"
				+ "        },\n"
				+ "        a5: [\"string61\", \"string62\", \"string63\"],\n"
				+ "      }");
		crepo.save(a);
	}

}
