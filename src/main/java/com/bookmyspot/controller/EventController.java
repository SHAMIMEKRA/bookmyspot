package com.bookmyspot.controller;


import com.bookmyspot.dto.EventRequest;
import com.bookmyspot.model.Event;
import com.bookmyspot.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {

    @Autowired
    EventService eventService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity <?> createEvent(@Valid @RequestBody EventRequest eventRequest, Principal principal){

        Event createdEvent=eventService.createEvent(eventRequest,principal);
        return ResponseEntity.ok(createdEvent);

    }


}
