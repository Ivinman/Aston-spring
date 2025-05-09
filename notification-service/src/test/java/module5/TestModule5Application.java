package module5;

import org.springframework.boot.SpringApplication;
import ru.aston.module5.Module5Application;

public class TestModule5Application {

	public static void main(String[] args) {
		SpringApplication.from(Module5Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
