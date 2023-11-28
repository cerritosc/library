/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.focusservices.library.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.focusservices.library.dto.RoleDTO;
import com.focusservices.library.dto.RolePermisoDTO;

/**
 *
 * @author ASanchez
 */
@Component
public class SecurityAuthenticationProvider  implements AuthenticationProvider{
     
     
    private SecurityUserDetails dbAuthentication(String userName) throws BadCredentialsException {
        try {
            // TODO implementar acorde a lo requerido
//            Optional<SecUser> userOpt = secUserService.findByCuser(userName);
//            
//            if(!userOpt.isPresent()){
//                 throw new BadCredentialsException("Usuario no encontrado");
//            }
//            
//            SecUser user = userOpt.get();

            SecurityUserDetails securityUser = new SecurityUserDetails();
            securityUser.setCuser(userName);
            
            
//            List<RolePermisoDTO> roles = secUserService.getRolesDeUsuario(userName);
//            
//            if (roles.isEmpty()) {
//                throw new BadCredentialsException("El usuario no tiene configurado roles");
//            }
//            
//            // se agregan los roles a la coleccion de permisos
//            roles.stream()
//                    .map(rolePermiso -> new RoleDTO(rolePermiso.getIdRole(), rolePermiso.getDescripcionRole()))
//                    .distinct()
//                    .forEach(role -> {
//                        securityUser.addGrantedAuthority(role.getNombreRol());
//                        securityUser.addRole(role.getDescripcionRol());
//                    });
//            
//            // se agregan los permisos a la coleccion de permisos.
//            roles.stream().forEach(rolePermiso -> {
//                securityUser.addGrantedAuthority(rolePermiso.getIdPrivilege());
//            });
            
            return securityUser;
        } catch (BadCredentialsException e) {
           
            throw new BadCredentialsException("Error al obtener información del usuario " + e.getMessage());
        }
    }

    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        String username = a.getName();
        String password = (String) a.getCredentials();
        //Implementar password dependiendo del tipo de autenticacion (ldap, db, etc..)
        SecurityUserDetails user = dbAuthentication(username);
        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> type) {
         return true; //To change body of generated methods, choose Tools | Templates.
    }
    
}
