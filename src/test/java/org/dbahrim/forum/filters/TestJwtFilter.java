package org.dbahrim.forum.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dbahrim.forum.configuration.security.JwtFilter;
import org.dbahrim.forum.configuration.security.JwtService;
import org.dbahrim.forum.services.SecurityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestJwtFilter {
    @Mock
    JwtService jwtService;

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    JwtFilter jwtFilter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    SecurityContext securityContext;

    @Mock
    SecurityService securityService;

    @Mock
    FilterChain filterChain;

    @Test
    public void testSkipRequest() throws Exception {
        when(request.getRequestURI()).thenReturn("signup");
        when(request.getHeader("Authorization")).thenReturn("Bearer asdasdsadasdsa");
        jwtFilter.doFilter(request, response, filterChain);
    }

    @Test
    public void testThrowException() throws Exception {
//         TODO: FIX ME
//        when(request.getRequestURI()).thenReturn("/aswefsdd");
//        when(request.getHeader("Authorization")).thenReturn("Bearer asdasdsadasdsa");
//        when(jwtService.extractUserName(anyString())).thenReturn("asasdasdd");
//        when(securityContext.getAuthentication()).thenReturn(null);
//        when(securityService.getSecurityContext()).thenReturn(securityContext);
//        doThrow(new UsernameNotFoundException("asdas")).when(userDetailsService).loadUserByUsername(any());
////        when(userDetailsService.loadUserByUsername(any())).thenThrow(new UsernameNotFoundException("asd"));
//        Assertions.assertThrows(UsernameNotFoundException.class, new Executable() {
//            @Override
//            public void execute() throws Throwable {
//                jwtFilter.doFilter(request, response, filterChain);
//            }
//        });
    }
}
