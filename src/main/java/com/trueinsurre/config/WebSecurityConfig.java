package com.trueinsurre.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.trueinsurre.user.service.DefaultUserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired
    private DefaultUserService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler successHandler;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//            .authorizeRequests()
//            .requestMatchers("/recurring/**","/recurring/*/*","/api/currencies","/sub-category/**","/category/**","/delete-profile","/request-delete", "/confirm-delete","/support/**","/support/*/*","/pdf/**","/api/auth/**","/contact/**","/premium/**","/login","/register/**","/sidebar","/api-reset","/password-request","/password-request/**", "free/**", "free/*/*","/reset-password","/reset-password/**","/master/**", "/images/**", "/index", "/en/**" , "error/**","web/**", "/web/*/*", "/web/*/*/*","/terms-conditions","/privacy","/contact","cssAdmin/**","css/**","/css/*/*","js/**","/video/**","demo","home","/opt/javaProjects/ibsInvoice/images/").permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .formLogin().loginPage("/login").successHandler(successHandler)
//            .and().oauth2Login().loginPage("/login").successHandler(successHandler)
//            .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
//            .and().sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false).sessionRegistry(sessionRegistry());
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        
//        return http.build();
//    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomSessionExpiredStrategy customSessionExpiredStrategy) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .requestMatchers("/","/index/**","/is-logged-in/**","/login/**","/delete-profile","/request-delete", "/confirm-delete","/api/auth/**","/sidebar","/password-request","/password-request/**","/reset-password","/reset-password/**","/admin/**", "/images/**","draft/**","/css/**","js/**","/video/**","/opt/javaProjects/ibsInvoice/images/").permitAll()
.anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/login").successHandler(successHandler)
            .and()
            .oauth2Login().loginPage("/login").successHandler(successHandler)
            .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login")
            .and()
            .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false) // Allow a new login to invalidate the old session
                .expiredSessionStrategy(customSessionExpiredStrategy) // Use custom strategy
                .sessionRegistry(sessionRegistry());
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
