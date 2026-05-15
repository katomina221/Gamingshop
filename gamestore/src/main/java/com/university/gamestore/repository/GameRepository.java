package com.university.gamestore.repository;

import com.university.gamestore.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByTitleContainingIgnoreCase(String title);

    List<Game> findByGenreIgnoreCase(String genre);

    List<Game> findByDeveloperIgnoreCase(String developer);

    @Query("SELECT DISTINCT g.genre FROM Game g WHERE g.genre IS NOT NULL")
    List<String> findAllGenres();

    List<Game> findTop8ByOrderByRatingDesc();

    @Query("SELECT g FROM Game g WHERE " +
           "LOWER(g.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(g.genre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(g.developer) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Game> searchGames(@Param("query") String query);
}
