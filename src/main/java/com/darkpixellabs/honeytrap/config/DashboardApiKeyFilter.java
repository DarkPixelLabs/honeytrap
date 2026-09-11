package com.darkpixellabs.honeytrap.config;

import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException;

@Component
public class DashboardApiKeyFilter extends OncePerRequestFilter {
 @Value("${honeytrap.api-key.enabled:false}") boolean enabled; @Value("${honeytrap.api-key.value:}") String configured;
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String p=req.getRequestURI(); boolean dashboard=p.startsWith("/api/stats/")||p.equals("/api/hits")||p.startsWith("/api/hits/");
  if(enabled&&dashboard&&!configured.equals(req.getHeader("X-HoneyTrap-Api-Key"))){res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"error\":\"dashboard API key required\"}");return;}
  chain.doFilter(req,res);
 }
}
