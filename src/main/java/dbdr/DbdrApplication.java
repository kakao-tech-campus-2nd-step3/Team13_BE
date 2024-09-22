package dbdr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DbdrApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbdrApplication.class, args);
	}

}
