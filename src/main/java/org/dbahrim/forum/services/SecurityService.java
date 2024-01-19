package org.dbahrim.forum.services;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    public SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }
}
