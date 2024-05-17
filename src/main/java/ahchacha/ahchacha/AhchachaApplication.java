package ahchacha.ahchacha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AhchachaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AhchachaApplication.class, args);
	}

}
