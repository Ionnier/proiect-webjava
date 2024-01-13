package org.dbahrim.forum;

import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.services.AuthenticationService;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Service
public class AuthenticationTestUtils {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    public String authorization = "Authorization";

    public User getUser() {
        return userRepository.findByEmail("user@cti.ro");
    }

    public User getAdmin() {
        return userRepository.findByEmail("admin@cti.ro");
    }

    public String user() {
        return "Bearer " + authenticationService.signIn(new UserRequest("user@cti.ro", "ciscoconpa55"));
    }

    public String admin() {
        return "Bearer " + authenticationService.signIn(new UserRequest("admin@cti.ro", "ciscoconpa55"));
    }

    public MockHttpServletRequestBuilder addUserHeader(MockHttpServletRequestBuilder builder) {
        return builder.header(authorization, user());
    }

    public MockHttpServletRequestBuilder addAdminHeader(MockHttpServletRequestBuilder builder) {
        return builder.header(authorization, admin());
    }

}
