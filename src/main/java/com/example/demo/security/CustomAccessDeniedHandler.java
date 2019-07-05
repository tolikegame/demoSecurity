package com.example.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode objNode1 = mapper.createObjectNode();
//        objNode1.put(e.getMessage(), "403");
//
//        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//        httpServletResponse.getWriter().write(objNode1.toString());
//        httpServletResponse.getWriter().flush();
//        httpServletResponse.getWriter().close();

        httpServletResponse.setHeader("Content-type","application/json;charset=UTF-8");
        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN,"權限不足");
    }
}
