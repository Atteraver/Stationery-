package com.stationery.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "request_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the Request
    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnore // Prevents circular reference in JSON response
    private Request request;

    // Link to the Item
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;
}