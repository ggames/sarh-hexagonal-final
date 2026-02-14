package com.fich.sarh.auth.Infrastructure.adapter.configuration.datasource;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseRoutingFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) request;
        String path = http.getRequestURI();

        try {
            if(path.startsWith("/auth/")){
                DatabaseContextHolder.setDatabaseType(DatabaseType.AUTH);

                System.out.println("ROUTING FILTER EXECUTED");
                System.out.println("PATH = " + path);


            }else{
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if(auth != null && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_DEVELOPER"))){
                    DatabaseContextHolder.setDatabaseType(DatabaseType.TEST);
                }else {
                    DatabaseContextHolder.setDatabaseType(DatabaseType.PROD);
                }
            }
            chain.doFilter(request, response);
        } finally {
            DatabaseContextHolder.clear();
        }
    }
}

/*
    try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DEVELOPER"))){
                DatabaseContextHolder.setDatabaseType(DatabaseType.TEST);
                System.out.println(">>> USANDO BASE TEST");
            }else {
                DatabaseContextHolder.setDatabaseType(DatabaseType.PROD);
                System.out.println(">>> USANDO BASE PROD");
            }

            chain.doFilter(request, response);


        }finally {
            DatabaseContextHolder.clear();
        }
 */