package org.dbahrim.forum;

import org.dbahrim.forum.configuration.security.AuthenticationService;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.hibernate.mapping.Map;
import org.hibernate.sql.results.graph.collection.internal.MapInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;
import java.util.Collections;

@Service
public class AuthenticationTestUtils {
    @Autowired
    private AuthenticationService authenticationService;

    public String authorization = "Authorization";

    public String user() {
        return "Bearer " + authenticationService.signIn(new UserRequest("user@cti.ro", "ciscoconpa55"));
    }

    public String admin() {
        return "Bearer " + authenticationService.signIn(new UserRequest("admin@cti.ro", "ciscoconpa55"));
    }

}
