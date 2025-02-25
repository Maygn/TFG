package trabajo.tfg;

import org.springframework.boot.SpringApplication;

public class TestTfgApplication {

	public static void main(String[] args) {
		SpringApplication.from(TfgApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
