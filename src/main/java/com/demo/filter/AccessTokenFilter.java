package com.demo.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by ajinkya on 10/19/16.
 */

@Component
@WebFilter(urlPatterns = "/user", filterName = "AccessTokenControllerName")
public class AccessTokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter Config: " + filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Inside Filter");
        System.out.println(servletRequest.getRemoteAddr());
    }

    @Override
    public void destroy() {

    }
}
