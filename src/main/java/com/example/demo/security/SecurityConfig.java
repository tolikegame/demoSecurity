package com.example.demo.security;

import com.example.demo.controller.CommonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
//以下@控制開啟@PreAuthorize 過濾權限,加在哪都有效果，只能用一個
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Configuration
    @Order(3)
    public static class TestConfig extends WebSecurityConfigurerAdapter{

        @Override
        protected void configure(HttpSecurity http) throws Exception{
            http.csrf().disable();
            http.authorizeRequests()
                    .antMatchers("/two").permitAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .passwordEncoder(new BCryptPasswordEncoder())
                    .withUser("yu")
                    .password(new BCryptPasswordEncoder().encode("123"))
                    .roles("TEST");
        }
    }

    @Configuration
    @Order(2)
    public static class MainConfig extends WebSecurityConfigurerAdapter {

        @Autowired
//        @Qualifier("customProvider")
        CustomProvider customProvider;

        @Autowired
        SessionRegistry sessionRegistry;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.cors();
            http.csrf().disable();
//            http.csrf().ignoringAntMatchers("/login");
//            http.csrf().ignoringAntMatchers("/logout");
//            http.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());

            http.authorizeRequests()
                    .antMatchers("/login").permitAll()
                    .anyRequest().authenticated()
                    .and().formLogin()//改用filter驗證登入
                    .defaultSuccessUrl("/hello")
//                    .successForwardUrl("")    //要用post去接
                    .and().logout()
//                    .logoutSuccessUrl("/logout")
//                    .deleteCookies("JSESSIONID")
                        ;

            //測試重複登入(改寫了loginFilter而無作用)
            http.sessionManagement().maximumSessions(1)//後登會踢掉前登的
                    .expiredUrl("/login")
            //false之后登录踢掉之前登录,true则不允许之后登录
                    .maxSessionsPreventsLogin(true)
                    //搭配上面的false狀態
//                    .expiredSessionStrategy(new CustomExpiredSessionStrategy())
                    .sessionRegistry(sessionRegistry()).and().init(http);   //取得所有登入session

            //session失效跳转的链接
            http.sessionManagement().invalidSessionUrl("/sessionError");

            //使用自訂filiter要先初始化SessionManagementConfigurer
            SessionAuthenticationStrategy  strategy = http.getSharedObject(SessionAuthenticationStrategy.class);
            //設置SessionAuthenticationStrategy 到自訂的 CustomFilter
            CustomLoginFilter customFilter = customloginFilter();
            customFilter.setSessionAuthenticationStrategy(strategy);

            //設置 CustomLoginFilter 在 UsernamePasswordAuthenticationFilter 的位置
            http.addFilterAt(customFilter, UsernamePasswordAuthenticationFilter.class);

            //錯誤訊息
            http.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint());
            http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler());

        }


        @Bean
        public CustomLoginFilter customloginFilter() throws Exception {
            //設置登入url
            CustomLoginFilter filter = new CustomLoginFilter("/login");
            //設置AuthenticationManager
            filter.setAuthenticationManager(authenticationManager());
            return filter;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            // 不拦截静态资源的访问
            web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .withUser("admin")
//                .password(new BCryptPasswordEncoder().encode("123"))
//                .roles("ADMIN");
//
//        auth.inMemoryAuthentication()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .withUser("user")
//                .password(new BCryptPasswordEncoder().encode("123"))
//                .roles("USER");

            auth.authenticationProvider(customProvider);
        }


        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
            return new HttpSessionEventPublisher();
        }

    }


}
