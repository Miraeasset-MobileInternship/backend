package miraeassetmobile.backend;

import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


import java.util.Locale;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling //spring scheduler
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
