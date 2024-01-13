package org.dbahrim.forum.services;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.controllers.EventController;
import org.dbahrim.forum.data.EventRepository;
import org.dbahrim.forum.models.Event;
import org.dbahrim.forum.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Calendar;
import java.util.Date;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EventService {
    final private EventRepository eventRepository;

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event createEvent(Event newEvent) throws ErrorController.MessagedException {
        if (newEvent.id != null) {
            throw new ErrorController.MessagedException("ID should be null when creating", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newEvent.timestamp);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long dateStartTimestamp = calendar.getTimeInMillis();
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long dateEndTimestamp = calendar.getTimeInMillis();

        if (eventRepository.findByTitleAndTimestampBetween(newEvent.title, dateStartTimestamp, dateEndTimestamp).size() != 0) {
            throw new ErrorController.MessagedException("Can't create event with same name in the same day", HttpStatus.BAD_REQUEST);
        }

        int currentIndex = 0;
        for (Character c: newEvent.title.toLowerCase().toCharArray()) {
            String moogle = "moogle";
            if (c == moogle.charAt(currentIndex)) {
                currentIndex += 1;
            }
            if (currentIndex == moogle.length()) {
                newEvent.title = newEvent.title + " kupo";
                break;
            }
        }
        return eventRepository.save(newEvent);
    }

    public void deleteEventWithId(User user, Long id) throws ErrorController.NotFoundException, ErrorController.BadRequest, ErrorController.Forbidden {
        if (user == null || id == null) {
            throw new ErrorController.BadRequest();
        }
        Event event = eventRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        if (!Objects.equals(event.createdBy.getId(), user.getId())) {
            throw new ErrorController.Forbidden();
        }
        eventRepository.deleteById(id);
    }
}
