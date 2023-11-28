package com.focusservices.library.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class SecurityHelper {

    private SecurityHelper(){
        throw new IllegalStateException("Utility Class");
    }

    public static final SecurityUserDetails getLoggedInUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object o = auth.getPrincipal();
            if (o instanceof SecurityUserDetails) {
                return ((SecurityUserDetails) o);
            } else if (auth instanceof PreAuthenticatedAuthenticationToken) {
                PreAuthenticatedAuthenticationToken ptoken = (PreAuthenticatedAuthenticationToken) auth;
                return (SecurityUserDetails) ptoken.getDetails();
            } else {
                return null;
            }
        }
        return null;
    }
    
    /** Indica si el usuario actual posee o no la autoridad actual.
     * 
     * @param authority la cadena a validar
     * @return si el usuario posee esa autority, retorna true; sino false
     */
    public static boolean hasAuthority(String authority) {
        SecurityUserDetails details = getLoggedInUserDetails();
        if(details != null) {
            return details
                    .getAuthorities()
                    .stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority))
                    ;
        }
        else return false;
    }
}
