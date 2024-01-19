package org.dbahrim.forum;

import io.jsonwebtoken.lang.Assert;
import org.dbahrim.forum.configuration.SecurityConfig;
import org.dbahrim.forum.configuration.security.JwtService;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.models.Event;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.Vote;
import org.dbahrim.forum.models.convertors.TypesEnumConvertor;
import org.dbahrim.forum.models.convertors.WayEnumConvertor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@SpringBootTest
public class MiscTests {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    TypesEnumConvertor typesEnumConvertor;

    @Autowired
    WayEnumConvertor wayEnumConvertor;

    @Autowired
    JwtService jwtService;

    @Mock
    Event event;

    @Mock
    User user;

    @Test
    public void testUserDetails() {
        Assertions.assertThrows(UsernameNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDetailsService.loadUserByUsername("asd");
            }
        });
    }

    @Test
    public void testEventPreCreate() throws ErrorController.BadRequest {
        Mockito.doCallRealMethod().when(event).preCreate();
        Assertions.assertThrows(ErrorController.BadRequest.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                event.preCreate();
            }
        });
    }

    @Test
    public void testVote() throws Exception {
        Vote vote = new Vote(1L, false);
        vote.getId();
        vote.getIncremented();
    }

    @Test
    public void testConvertor() throws Exception {
        Assertions.assertNull(wayEnumConvertor.convert("asdasd"));
        Assertions.assertNull(typesEnumConvertor.convert("asdasd"));
    }

    @Test
    public void testUserPreCreate() throws ErrorController.BadRequest {
        Mockito.doCallRealMethod().when(user).prePersist();
        user.prePersist();
        Assertions.assertEquals(user.role, SecurityConfig.ROLE_USER);
    }
}


