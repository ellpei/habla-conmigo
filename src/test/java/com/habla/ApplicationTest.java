package com.habla;

import com.habla.controller.GameController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTest {
	@Autowired
	private GameController gameController;

	@Test
	void contextLoads() {
		assertThat(gameController).isNotNull();
	}

}
