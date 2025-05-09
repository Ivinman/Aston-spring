package module5;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(module5.TestcontainersConfiguration.class)
@SpringBootTest
class Module5ApplicationTests {

	@Test
	void contextLoads() {
	}

}
