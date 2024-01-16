package org.dbahrim.forum.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.models.Event;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.dtos.EventPost;
import org.dbahrim.forum.models.mappers.EventMapper;
import org.dbahrim.forum.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    @Operation(summary = "Get all events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class)) })})
    List<Event> getAll() {
        return eventService.getAll();
    }

    @PostMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Add new event")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            content = { @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Event.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorController.MessageClass.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorController.MessageClass.class)) }
                    ),
            }
    )
    Event postEvent(@AuthenticationPrincipal User user, @RequestBody @Valid EventPost requestEvent) throws ErrorController.MessagedException {
        Event newEvent = eventMapper.sourceToDestination(requestEvent);
        newEvent.setCreatedBy(user);
        newEvent.setTimestamp(requestEvent.getTimestamp());
        return eventService.createEvent(newEvent);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Delete an event")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable @NotNull Long id) throws ErrorController.NotFoundException, ErrorController.BadRequest, ErrorController.Forbidden {
        eventService.deleteEventWithId(user, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
