package com.example.check_in_app.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManagerBuilder auth) throws Exception {

        http.exceptionHandling()
                .accessDeniedPage("/error");

        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                                authorize.requestMatchers("/registro").permitAll()
                                        .requestMatchers("/index").authenticated()
                                        .requestMatchers("/userstable").hasAnyAuthority("ROLE_ADMIN")
                                        .requestMatchers("/userstable").hasRole("ROLE_ADMIN")
                                        .requestMatchers("/registro/guardar/**").permitAll()
                                        .requestMatchers("/").authenticated()
                                        .requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
                                        .requestMatchers("/files/**").permitAll()
                                        .requestMatchers("/enviar/**").authenticated()
                                        .requestMatchers("/admin").hasAnyAuthority("ROLE_ADMIN")
                                        .requestMatchers("/admin").hasRole("ROLE_ADMIN")
                                        .requestMatchers("/save/**").authenticated()
                                        .requestMatchers("/blog/**").authenticated()
                                        .requestMatchers("/delete/**").hasAnyAuthority("ROLE_ADMIN")
                                        .requestMatchers("/deleteUser/**").hasAnyAuthority("ROLE_ADMIN")
                                        .requestMatchers("/chat/**").authenticated()
                                        .requestMatchers("/api/v1/**").hasAnyAuthority("ROLE_ADMIN")
                                        .requestMatchers("/ficharEntrada").permitAll()
                                        .requestMatchers("/ficharSalida").permitAll()
                                        .requestMatchers("/error").permitAll()
                                        .requestMatchers("/buscarUsuario/**").permitAll()
                                        .requestMatchers("/editar/**").authenticated()

                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/")
                                .permitAll()

                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}