package eu.estilolibre.tfgunir.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		// TODO - Lambda DSL
		http
				.csrf().disable()
				.authorizeHttpRequests(requests -> requests
						.requestMatchers(HttpMethod.GET, "/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/*").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/*").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/*").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/*").authenticated()
						.anyRequest().denyAll())
				.httpBasic();
		return http.build();
	}
}
