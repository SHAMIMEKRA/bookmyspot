package com.bookmyspot.service;

import com.bookmyspot.dto.EventRequest;
import com.bookmyspot.exception.GlobalExceptionHandler.*; // <-- Import our new exception
//import com.bookmyspot.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.bookmyspot.exception.ResourceNotFoundException;
import com.bookmyspot.model.Event;
import com.bookmyspot.model.User;
import com.bookmyspot.repository.EventRepository;
import com.bookmyspot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new event. This is the "recipe":
     * 1. Find the Admin User who is making this request.
     * 2. Create a new Event entity.
     * 3. Map the data from the DTO to the new Event entity.
     * 4. Set the fields that aren't in the DTO (creator, status, availableTickets).
     * 5. Save the new entity to the database.
     *
     * @param eventRequest The DTO with all the event details.
     * @param principal The logged-in user (our Admin).
     * @return The newly saved Event.
     */
    public Event createEvent(EventRequest eventRequest, Principal principal) {

        // --- This is the implementation of the logic ---

        // 1. Find the Admin User who is making this request.
        String username = principal.getName();
        User adminUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 2. Create a new Event entity.
        Event event = new Event();

        // 3. Map the data from the DTO to the new Event entity.
        event.setName(eventRequest.getName());
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setLocation(eventRequest.getLocation());
        event.setTotalTickets(eventRequest.getTotalTickets());
        event.setTicketPrice(eventRequest.getTicketPrice());
        event.setImageUrl(eventRequest.getImageUrl()); // This can be null, which is fine

        // 4. Set the fields that aren't in the DTO
        event.setCreator(adminUser);
        event.setAvailableTickets(eventRequest.getTotalTickets()); // On creation, available = total

        // We set the default status from the enum.
        // We can use the default from the entity, but being explicit here is safer.
        event.setStatus(Event.EventStatus.ACTIVE);

        // 5. Save the new entity to the database and return it.
        // The .save() method returns the saved entity (with its new ID!)
        return eventRepository.save(event);
    }
}