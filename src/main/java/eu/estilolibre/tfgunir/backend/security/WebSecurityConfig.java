package eu.estilolibre.tfgunir.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
    private static final String[] DOCS_PATHS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    };

    /** Catálogo de solo lectura: cursos, categorías y valoraciones. */
    private static final String[] PUBLIC_CATALOG = {
            "/api/cursos/**", "/api/categorias/**", "/api/valoraciones/**"
    };

    /**
     * Protege Swagger/OpenAPI con autenticación básica.
     *
     * <p>La UI de Swagger se sirve en el navegador y no puede enviar el JWT de la
     * API en la navegación inicial, por eso usa HTTP Basic con credenciales
     * independientes configuradas por entorno. Si {@code SWAGGER_USER} o
     * {@code SWAGGER_PASSWORD} no están definidos, la documentación queda
     * cerrada (403) en lugar de pública.</p>
     */
    @Bean
    @Order(1)
    public SecurityFilterChain docsFilterChain(HttpSecurity http,
            PasswordEncoder passwordEncoder,
            @Value("${swagger.auth.user:}") String swaggerUser,
            @Value("${swagger.auth.password:}") String swaggerPassword) throws Exception {
        http.securityMatcher(DOCS_PATHS)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (swaggerUser.isBlank() || swaggerPassword.isBlank()) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().denyAll());
            return http.build();
        }

        UserDetails docsUser = User.withUsername(swaggerUser)
                .password(passwordEncoder.encode(swaggerPassword))
                .roles("SWAGGER")
                .build();
        UserDetailsService docsUserDetailsService = new InMemoryUserDetailsManager(docsUser);

        http.authorizeHttpRequests(auth -> auth.anyRequest().hasRole("SWAGGER"))
                .httpBasic(withDefaults())
                .userDetailsService(docsUserDetailsService);
        return http.build();
    }

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
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        // La página de inicio (pública) consume /actuator/info para mostrar
                        // versión, build time y datos del commit.
                        .requestMatchers("/actuator/info").permitAll()
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
