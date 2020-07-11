package com.gutmox.large.files.process;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class LargeFileProcessTest {

	@Test
	void should_do_parse_it_all(Vertx vertx, VertxTestContext testContext) {

		new LargeFileProcess().doProcess(vertx).subscribe(() -> {
				assertThat(true).isTrue();
				testContext.completeNow();
			},
			Throwable::printStackTrace);
	}

}