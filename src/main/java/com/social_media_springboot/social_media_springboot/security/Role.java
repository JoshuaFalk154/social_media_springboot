package com.social_media_springboot.social_media_springboot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.social_media_springboot.social_media_springboot.security.Permission.*;

@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(Set.of(
            ADMIN_CREATE,
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            MANAGER_CREATE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE
    )),
    MANAGER(Set.of(
            MANAGER_CREATE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE,
            ADMIN_READ
    ));

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getPermissions() {
        List<SimpleGrantedAuthority> result = new java.util.ArrayList<>(permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList());

        result.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return result;
    }
}
