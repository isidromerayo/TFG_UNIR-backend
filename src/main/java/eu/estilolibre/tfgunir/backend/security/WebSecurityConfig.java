package eu.estilolibre.tfgunir.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.Filter;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.estilolibre.tfgunir.backend.service.UsuarioService;

/**
 * Configuración de seguridad HTTP.
 *
 * <p>Política <em>deny-by-default</em>: solo las rutas declaradas explícitamente
 * como públicas quedan abiertas. La API es <em>stateless</em> y se autentica con
 * el JWT validado por {@link JwtAuthenticationFilter}.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    /** Rutas de documentación y recursos estáticos de la página de estado. */
    private static final String[] PUBLIC_RESOURCES = {
            "/", "/index.html", "/favicon.ico", "/error"
    };

    /** Rutas de Springdoc / Swagger UI. */
    private static final String[] PUBLIC_DOCS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
    };

    /** Catálogo de solo lectura: cursos, categorías y valoraciones. */
    private static final String[] PUBLIC_CATALOG = {
            "/api/cursos/**", "/api/categorias/**", "/api/valoraciones/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenService tokenService,
            UsuarioService usuarioService) throws Exception {
        Filter jwtFilter = new JwtAuthenticationFilter(tokenService, usuarioService);

        http
                // API stateless con token en cabecera: CSRF no aplica (no hay cookies de sesión).
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Público: login, registro y catálogo de solo lectura.
                        .requestMatchers(HttpMethod.POST, "/api/auth", "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_CATALOG).permitAll()
                        .requestMatchers(PUBLIC_RESOURCES).permitAll()
                        .requestMatchers(PUBLIC_DOCS).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        // Cualquier otra cosa exige token válido: incluye escrituras de
                        // Spring Data REST y los datos de usuario.
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(RestSecurityHandlers.unauthorized())
                        .accessDeniedHandler(RestSecurityHandlers.forbidden()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
