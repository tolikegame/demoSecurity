package com.example.demo.security;

import com.example.demo.model.response.CommonResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper mapper =new ObjectMapper();

    protected CustomLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        /**
         *設置驗證成功後續處理
         */
        this.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                CommonResponse response =new CommonResponse(true,200,"login OK!");
                httpServletResponse.getWriter().println(mapper.writeValueAsString(response));
            }
        });
        /**
         *設置驗證失敗後續處理
         */
        this.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, org.springframework.security.core.AuthenticationException e) throws IOException, ServletException {
                CommonResponse response =new CommonResponse(false,400,e.getMessage());
                httpServletResponse.setStatus(400);
                httpServletResponse.getWriter().println(mapper.writeValueAsString(response));
            }
        });
    }

    /*
     *實作調用驗證的方法
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        HttpSession session = httpServletRequest.getSession(false);
        if(session != null){
            session.invalidate();
        }
        if (httpServletRequest.getMethod().equals("POST")&&httpServletRequest.getContentType().equals("application/json")) {
            JsonNode root =mapper.readTree(httpServletRequest.getInputStream());
            //取得 AuthenticationManager() 並調用驗證方法
            return this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(root.get("account").asText(),root.get("password").asText()));
        }else{
            throw new BadCredentialsException("method not support");
        }

    }
}
