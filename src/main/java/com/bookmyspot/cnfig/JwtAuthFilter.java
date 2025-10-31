package com.bookmyspot.cnfig;

import com.bookmyspot.service.JwtService;
import com.bookmyspot.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// We mark this as @Component so Spring can find it and use it
@Component
// This filter runs ONCE for every request
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Our "Key Maker"

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Our "User Finder"

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException{
        // 1. Get the "Authorization" header from the request
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Check if the header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extract the token string (after "Bearer ")
            try {
                // 3. Ask the JwtService to get the username from the token
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                // If the token is invalid (expired, fake, etc.), it will throw an exception
                logger.warn("Invalid JWT token: " + e.getMessage());
            }
        }

        // 4. If we have a username AND the user is not *already* authenticated...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. ...load the UserDetails from our service (this hits the database)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Ask the JwtService to validate the token (check expiration, signature)
            if (jwtService.validateToken(token, userDetails)) {

                // 7. If the token is valid, we manually "log in" the user for this request
                // This tells Spring Security "This user is authenticated"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Finally, pass the request on to the next filter (or to our controller)
        filterChain.doFilter(request, response);

    }

}
