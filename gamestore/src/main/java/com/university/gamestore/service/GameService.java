package com.university.gamestore.service;

import com.university.gamestore.model.Game;
import com.university.gamestore.model.Order;
import com.university.gamestore.model.User;
import com.university.gamestore.repository.GameRepository;
import com.university.gamestore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Игра не найдена: " + id));
    }

    public List<Game> searchGames(String query) {
        return gameRepository.searchGames(query);
    }

    public List<Game> getGamesByGenre(String genre) {
        return gameRepository.findByGenreIgnoreCase(genre);
    }

    public List<Game> getTopGames() {
        return gameRepository.findTop8ByOrderByRatingDesc();
    }

    public List<String> getAllGenres() {
        return gameRepository.findAllGenres();
    }

    @Transactional
    public void addToCart(String username, Long gameId) {
        User user = userService.findByUsername(username);
        Game game = getGameById(gameId);

        boolean alreadyInCart = user.getCart().stream()
            .anyMatch(g -> g.getId().equals(gameId));
        boolean alreadyOwned = user.getLibrary().stream()
            .anyMatch(g -> g.getId().equals(gameId));

        if (!alreadyInCart && !alreadyOwned) {
            user.getCart().add(game);
            userService.save(user);
        }
    }

    @Transactional
    public void removeFromCart(String username, Long gameId) {
        User user = userService.findByUsername(username);
        user.getCart().removeIf(g -> g.getId().equals(gameId));
        userService.save(user);
    }

    @Transactional
    public Order purchase(String username) {
        User user = userService.findByUsername(username);
        List<Game> cartGames = user.getCart();

        if (cartGames.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        BigDecimal total = cartGames.stream()
            .map(Game::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        user.getLibrary().addAll(cartGames);
        user.getCart().clear();

        Order order = new Order(user, cartGames, total);
        orderRepository.save(order);
        userService.save(user);

        return order;
    }

    @Transactional
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    @Transactional
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}
