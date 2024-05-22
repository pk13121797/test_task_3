package by.pavvel.config;

import by.pavvel.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private Environment environment;

    private final static String secretKey = "4261656C64756E67";

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public SecurityConfig(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/reg/**", "/login","/change","/forgot", "/confirm-reg", "/confirm-reset").anonymous()
                        .requestMatchers("/resources/**","/img/**").permitAll()
                        .requestMatchers("/projects/edit/**","/tasks/edit/**","/employees/edit/**").hasRole("ADMIN")
                        .requestMatchers("/projects/**","/tasks/**","/employees/**").fullyAuthenticated()
                        .requestMatchers("/").authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                )
                .oauth2Login(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")
                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedPage("/"))
                .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
                        .tokenValiditySeconds(24 * 60 * 60)
                        .key(secretKey)
                );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        ClientRegistration githubRegistration =
                CommonOAuth2Provider.GITHUB.getBuilder("github")
                        .clientId(environment.getProperty("spring.security.oauth2.client.registration.github.client-id"))
                        .clientSecret(environment.getProperty("spring.security.oauth2.client.registration.github.client-secret"))
                        .build();

        ClientRegistration googleRegistration =
                CommonOAuth2Provider.GOOGLE.getBuilder("google")
                        .clientId(environment.getProperty("spring.security.oauth2.client.registration.google.client-id"))
                        .clientSecret(environment.getProperty("spring.security.oauth2.client.registration.google.client-secret"))
                        .build();

        return new InMemoryClientRegistrationRepository(githubRegistration, googleRegistration);
    }
}