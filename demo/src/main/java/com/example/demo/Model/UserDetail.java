package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class UserDetail {
    @Id // Marks this field as a primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates values for this field
    private Long index;

    @Column(nullable = false)
    private String userId;

    @Column(length = 50, nullable = false)
    private String firstName;

    @Column(length = 50, nullable = false)
    private String lastName;

    @Column(length = 10)
    private String sex;

    @Column
    private String email;

    @Column(length = 40)
    private String phone;

    @Column
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String jobTitle;
}
