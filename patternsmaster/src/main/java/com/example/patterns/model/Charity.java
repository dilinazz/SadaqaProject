package com.example.patterns.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "charities")
@Getter
@Setter
@ToString
public class Charity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String charityName;
    private String category;
    private String city;
    private Date date;
    private String shortDescription;
    private String fullDescription;
    private int collected;
    private int goal;
    @ManyToOne
    private User user;

    private boolean isHandled;
}
