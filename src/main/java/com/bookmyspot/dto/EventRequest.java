package com.bookmyspot.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL; // For @URL validation

import java.time.LocalDateTime;

@Data
public class EventRequest {

    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Event date cannot be null")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotNull(message = "Total tickets cannot be null")
    @Min(value = 1, message = "Event must have at least 1 ticket")
    private int totalTickets;

    @NotNull(message = "Ticket price cannot be null")
    @Min(value = 0, message = "Ticket price can be 0 (free) but not negative")
    private Double ticketPrice;

    // This is optional, so no @NotBlank, but if it is provided,
    // it must be a valid URL format.
    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;
}