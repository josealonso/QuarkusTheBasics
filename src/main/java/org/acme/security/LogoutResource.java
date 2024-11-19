package org.acme.security;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.oidc.IdToken;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/logout")
public class LogoutResource {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Context
    UriInfo uriInfo;

    @GET
    public Response logout() {
        String redirectUri = uriInfo.getBaseUri().toString();
        return Response.status(302)
                .header("Location", 
                        "/auth/realms/your-realm/protocol/openid-connect/logout" +
                        "?post_logout_redirect_uri=" + redirectUri)
                .build();
    }

}