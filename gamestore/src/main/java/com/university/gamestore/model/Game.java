package com.university.gamestore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String genre;
    private String developer;
    private String imageUrl;
    private Double rating;
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer stock = 999;

    @ManyToMany(mappedBy = "library")
    private List<User> owners;

    public Game(String title, String description, BigDecimal price,
                String genre, String developer, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.genre = genre;
        this.developer = developer;
        this.imageUrl = imageUrl;
        this.rating = 0.0;
        this.releaseDate = LocalDate.now();
    }
}
