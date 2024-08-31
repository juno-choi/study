package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration  //다른 bean들 보다 우선순위를 앞으로
@EnableWebSecurity  //security 어노테이션
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment env;

    //권한
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();   //actuator는 모두 허용
        //http.authorizeRequests().antMatchers("/users/**").permitAll();    //기존 모두 ok
        http.authorizeRequests().antMatchers("/**")
            .permitAll()    //ip 설정
            .and()
            .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();    //h2 console error 해결을 위해
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env);
        authenticationFilter.setAuthenticationManager(authenticationManager()); //spring security에서 제공하는 manager 객체

        return authenticationFilter;
    }

    //인증
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);   //사용자가 전달한 id와 pw를 통해 로그인 처리를 security가 해줌
    }
}
