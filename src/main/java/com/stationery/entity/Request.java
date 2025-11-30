package com.stationery.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many Requests belong to One User
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(name = "superior_email", nullable = false)
    private String superiorEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @CreationTimestamp // Automatically sets time when saved
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    // Total cost of this specific request (Calculated for Reports)
    @Column(name = "total_cost")
    private Double totalCost;

    // Relationship: One Request contains many RequestDetails (Line Items)
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestDetail> requestDetails;
}