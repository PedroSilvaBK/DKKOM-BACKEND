package dcom.cave_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class CaveServiceApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("context loaded");
	}

	@Test
	void test1() {
		System.out.println("teste1");
	}

	@Test
	void teste3() {
		System.out.println("teste3");
	}

}
