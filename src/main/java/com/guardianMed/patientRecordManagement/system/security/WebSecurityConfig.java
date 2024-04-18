package com.guardianMed.patientRecordManagement.system.security;

import com.guardianMed.patientRecordManagement.system.security.jwt.AuthEntryPoint;
import com.guardianMed.patientRecordManagement.system.security.jwt.AuthTokenFilter;
import com.guardianMed.patientRecordManagement.system.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()

                .antMatchers("/home").permitAll()
                .antMatchers("/dashboard").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

//             Authentication
                .antMatchers("/api/auth/signin").permitAll()
                .antMatchers("/api/auth/signup").permitAll()

//             patients
                .antMatchers("/patients/all").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")
                .antMatchers("/patients/add").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/patients/byNIC/{patientNIC}").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")
                .antMatchers("/patients/update/{patientNIC}").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/patients/{patientNIC}").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

//             prescriptions
                .antMatchers("/prescription/add").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/prescription/all").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")
                .antMatchers("/prescription/byID/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")
                .antMatchers("/prescription/byNIC/{patientNIC}").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")
                .antMatchers("/prescription/update/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/prescription/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN","ROLE_ADMIN")

//             Admins
                .antMatchers("/api/admin/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/api/admin/reset-password/{id}").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/api/admin/delete/{username}").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/api/admin/all").hasAnyAuthority("ROLE_SUPER_ADMIN")

//             Bills
                .antMatchers("/bills/add/{prescriptionId}").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/bills/all").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/bills/byID/{id}").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/bills/{id}").hasAnyAuthority("ROLE_ADMIN")

                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
