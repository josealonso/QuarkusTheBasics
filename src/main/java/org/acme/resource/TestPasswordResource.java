package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.acme.service.UserService;

@Path("/test-password")
public class TestPasswordResource {

    @Inject
    UserService userService;

    @GET
    public String testHash(@QueryParam("password") String password) {
        return userService.generateBcryptHash(password);
    }
}
