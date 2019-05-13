package com.github.senyast4745.config;

import com.github.senyast4745.model.Role;
import com.github.senyast4745.security.jwt.JwtConfigurer;
import com.github.senyast4745.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        try {
            http
                    .httpBasic().disable()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/").permitAll()
                    .antMatchers(HttpMethod.GET, "/admin").hasAuthority(Role.ADMIN.name())
                    .antMatchers(HttpMethod.GET, "/admin/*").hasAuthority(Role.ADMIN.name())
                    .antMatchers(HttpMethod.POST, "/sensor").hasAuthority(Role.SENSOR.name())
                    .anyRequest().authenticated()
                    .and()
                    .apply(new JwtConfigurer(jwtTokenProvider));
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}

