package org.acme.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

@Path("/api")
public class UserInfoResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/userinfo")
    @RolesAllowed("user")
    public String getUserInfo() {
        return "Hello, " + securityIdentity.getPrincipal().getName();
    }
}