package academy.devdojo.SpringBoot2.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
  * Basic AuthenticationFilter
  * UsernamePasswordAuthenticationFilter
  * DefaultLoginPageGenerationFilter
  * DefaultLogoutPageGenerationFilter
  * FilterSecurityInterceptor
  * Authentication -> Authorization
 */
@EnableWebSecurity
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
//         Para habilitar o csrfToken
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()

                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Passaword encoded {}",passwordEncoder.encode("test"));

        auth.inMemoryAuthentication()
                .withUser("Savio")
                .password(passwordEncoder.encode("123456"))
                .roles("USER","ADMIN")
                .and()
                .withUser("devdojo")
                .password(passwordEncoder.encode("123456"))
                .roles("USER");
    }
}
