package com.example.demo.security;

import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.ExpressionBasedFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomFilterSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private SecurityExpressionHandler<FilterInvocation> expressionHandler;

    private  FilterInvocationSecurityMetadataSource proxy;

    private  Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;

    private HttpSecurity http;

    public CustomFilterSecurityMetadataSource() {
    }

    /**
     * 初始化權限映射表
     *
     * @param proxy  代理物件
     * @param http  HttpSecurity 物件
     */
    public void init(FilterInvocationSecurityMetadataSource proxy,HttpSecurity http){
        this.http = http;
        this.proxy = proxy;
        Map<RequestMatcher, Collection<ConfigAttribute>> map = null;
        try {
            Field field = DefaultFilterInvocationSecurityMetadataSource.class.getDeclaredField("requestMap");
            field.setAccessible(true);
            map= (Map<RequestMatcher, Collection<ConfigAttribute>>)field.get(proxy);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();

        }
        this.requestMap = map;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        return proxy.getAttributes(o);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return proxy.getAllConfigAttributes();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return proxy.supports(aClass);
    }

    /**
     *  設定新的權限設定
     * @param url  指定url ant格式
     * @param rule  權限判斷
     */
    public void determineNewPermissions(String url,String rule) {

        RequestMatcher key = null;
        for(Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry:requestMap.entrySet()){
            RequestMatcher var1 = entry.getKey();
            if( var1 instanceof AntPathRequestMatcher && ((AntPathRequestMatcher) var1).getPattern().equals(url)){
                key = entry.getKey();
            }
        }

        LinkedHashMap<RequestMatcher,Collection<ConfigAttribute>> newConfigAttributes = new LinkedHashMap<>();
        if(key != null){
            //注意 SecurityConfig 的 package
            newConfigAttributes.put(key,org.springframework.security.access.SecurityConfig.createList(rule));
        }

        Method method = null;
        try {
            method = ExpressionBasedFilterInvocationSecurityMetadataSource.class.getDeclaredMethod("processMap", LinkedHashMap.class, ExpressionParser.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        try {
            LinkedHashMap result = (LinkedHashMap) method.invoke(null,newConfigAttributes,getExpressionHandler(http).getExpressionParser());
            requestMap.putAll(result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *  from ExpressionUrlAuthorizationConfigurer  80~112
     * @param http HttpSecurity 物件
     * @return SecurityExpressionHandler
     */
    private SecurityExpressionHandler<FilterInvocation> getExpressionHandler(HttpSecurity http) {
        if (this.expressionHandler == null) {
            DefaultWebSecurityExpressionHandler defaultHandler = new DefaultWebSecurityExpressionHandler();
            AuthenticationTrustResolver trustResolver = (AuthenticationTrustResolver)http.getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                defaultHandler.setTrustResolver(trustResolver);
            }

            ApplicationContext context = (ApplicationContext)http.getSharedObject(ApplicationContext.class);
            if (context != null) {
                String[] roleHiearchyBeanNames = context.getBeanNamesForType(RoleHierarchy.class);
                if (roleHiearchyBeanNames.length == 1) {
                    defaultHandler.setRoleHierarchy((RoleHierarchy)context.getBean(roleHiearchyBeanNames[0], RoleHierarchy.class));
                }

                String[] grantedAuthorityDefaultsBeanNames = context.getBeanNamesForType(GrantedAuthorityDefaults.class);
                if (grantedAuthorityDefaultsBeanNames.length == 1) {
                    GrantedAuthorityDefaults grantedAuthorityDefaults = (GrantedAuthorityDefaults)context.getBean(grantedAuthorityDefaultsBeanNames[0], GrantedAuthorityDefaults.class);
                    defaultHandler.setDefaultRolePrefix(grantedAuthorityDefaults.getRolePrefix());
                }

                String[] permissionEvaluatorBeanNames = context.getBeanNamesForType(PermissionEvaluator.class);
                if (permissionEvaluatorBeanNames.length == 1) {
                    PermissionEvaluator permissionEvaluator = (PermissionEvaluator)context.getBean(permissionEvaluatorBeanNames[0], PermissionEvaluator.class);
                    defaultHandler.setPermissionEvaluator(permissionEvaluator);
                }
            }

            this.expressionHandler = defaultHandler;
        }
        return this.expressionHandler;
    }
}
