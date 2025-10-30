package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MySequenceGenerator")
    @SequenceGenerator(allocationSize = 1, schema = "public", name = "MySequenceGenerator", sequenceName = "mysequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private String message;

    private String link;

    private boolean read = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
