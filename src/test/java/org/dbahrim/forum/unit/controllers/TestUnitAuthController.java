package org.dbahrim.forum.unit.controllers;

import org.dbahrim.forum.configuration.security.UserRequest;
import org.dbahrim.forum.services.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUnitAuthController {

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    org.dbahrim.forum.controllers.AuthController authController;

    @Mock
    UserRequest userRequest;

    @Test
    public void executeTest1(){
        String asd = "asd";
        when(authenticationService.signIn(any())).thenReturn(asd);
        Assertions.assertEquals(asd, authController.logIn(userRequest));
        verify(authenticationService, times(1)).signIn(userRequest);
    }

    @Test
    public void executeTest2(){
        String asd = "asd";
        when(authenticationService.signUp(any())).thenReturn(asd);
        Assertions.assertEquals(asd, authController.signUp(userRequest));
        verify(authenticationService, times(1)).signUp(userRequest);
    }
}
