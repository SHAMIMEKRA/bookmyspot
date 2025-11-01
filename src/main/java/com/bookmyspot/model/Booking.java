package com.bookmyspot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_event_id", columnList = "event_id"),
        @Index(name = "idx_booking_time", columnList = "booking_time")
})
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Min(value = 1, message = "Number of tickets must be at least 1")
    @Column(nullable = false, name = "number_of_tickets")
    private int numberOfTickets;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @CreatedDate
    @Column(name = "booking_time", nullable = false, updatable = false)
    private LocalDateTime bookingTime;

    @Column(name = "confirmation_code", unique = true, length = 50)
    private String confirmationCode;

    @PrePersist
    protected void onCreate() {
        bookingTime = LocalDateTime.now();
        // Generate confirmation code
        confirmationCode = "BMS-" + System.currentTimeMillis();
    }

    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED
    }
}