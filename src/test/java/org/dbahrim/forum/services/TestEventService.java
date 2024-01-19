package org.dbahrim.forum.services;

import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.EventRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Event;
import org.dbahrim.forum.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestEventService {
    @InjectMocks
    EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    User user;

    @Mock
    User user2;

    private final Event validEventCreate = new Event(null, "A valid event title", Instant.now().toEpochMilli(), user);

    @Test
    void testCallWithIdThrows() throws Exception {
        ErrorController.MessagedException messagedException = assertThrows(ErrorController.MessagedException.class, () -> {
            eventService.createEvent(validEventCreate.toBuilder().id(4L).build());
        });
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, messagedException.httpStatus);
        Assertions.assertEquals("ID should be null when creating", messagedException.message);
    }

    @Test
    void testSaveSameNameInSameDayFails() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of(validEventCreate.toBuilder().timestamp(1705097146351L).build()));
        ErrorController.MessagedException messagedException = assertThrows(ErrorController.MessagedException.class, () -> {
            eventService.createEvent(validEventCreate.toBuilder().timestamp(1705097146326L).build());
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, messagedException.httpStatus);
        Assertions.assertEquals("Can't create event with same name in the same day", messagedException.message);
    }

    @Test
    void assertMoogleStart() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Event) args[0];
            }
        });
        Event result = eventService.createEvent(validEventCreate.toBuilder().title("moogle").build());
        String value = result.title.substring(result.title.length()-4);
        Assertions.assertEquals("kupo", value);
    }
    @Test

    void assertMoogleSarcasmCase() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Event) args[0];
            }
        });
        Event result = eventService.createEvent(validEventCreate.toBuilder().title("mOoGlE").build());
        String value = result.title.substring(result.title.length()-4);
        Assertions.assertEquals("kupo", value);
    }

    @Test
    void assertRandomMoogle() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Event) args[0];
            }
        });
        Event result = eventService.createEvent(validEventCreate.toBuilder().title("asdasdmeqweOgfdgfdorerGewqlwE").build());
        String value = result.title.substring(result.title.length()-4);
        Assertions.assertEquals("kupo", value);
    }

    @Test
    void assertMoogleEnd() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Event) args[0];
            }
        });
        Event result = eventService.createEvent(validEventCreate.toBuilder().title("asdmoogle").build());
        String value = result.title.substring(result.title.length()-4);
        Assertions.assertEquals("kupo", value);
    }

    @Test
    void assertNoMoogle() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Event) args[0];
            }
        });
        Event result = eventService.createEvent(validEventCreate.toBuilder().title("assdfsdfd").build());
        String value = result.title.substring(result.title.length()-4);
        Assertions.assertNotEquals("kupo", value);
    }

    @Test
    void assertEmptyString() throws Exception {
        when(eventRepository.findByTitleAndTimestampBetween(anyString(), anyLong(), anyLong())).thenReturn(List.of());
        when(eventRepository.save(any())).thenAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Event) args[0];
            }
        });
        Event result = eventService.createEvent(validEventCreate.toBuilder().title("assdfsdfd").build());
        String value = result.title.substring(result.title.length()-4);
        Assertions.assertNotEquals("kupo", value);
    }

    @Test
    void deleteWithNullUserReturnsBadRequest() throws Exception {
        assertThrows(ErrorController.BadRequest.class, () -> {
            eventService.deleteEventWithId(null, 1L);
        });
    }

    @Test
    void deleteWithNullIdReturnsBadRequest() throws Exception {
        assertThrows(ErrorController.BadRequest.class, () -> {
            eventService.deleteEventWithId(user, null);
        });
    }

    @Test
    void deletwWithAnotherUser() throws Exception {
        when(eventRepository.findById(any())).thenReturn(Optional.of(validEventCreate.toBuilder().id(1L).createdBy(user).build()));
        when(user.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        assertThrows(ErrorController.Forbidden.class, () -> {
            eventService.deleteEventWithId(user2, 1L);
        });
    }
}
