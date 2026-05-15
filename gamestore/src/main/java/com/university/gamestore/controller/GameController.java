package com.university.gamestore.controller;

import com.university.gamestore.model.Game;
import com.university.gamestore.model.User;
import com.university.gamestore.service.AiService;
import com.university.gamestore.service.GameService;
import com.university.gamestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final UserService userService;
    private final AiService aiService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("topGames", gameService.getTopGames());
        return "index";
    }

    @GetMapping("/games")
    public String catalog(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String search,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Game> games;

        if (search != null && !search.isBlank()) {
            games = gameService.searchGames(search);
            model.addAttribute("searchQuery", search);
        } else if (genre != null && !genre.isBlank()) {
            games = gameService.getGamesByGenre(genre);
            model.addAttribute("selectedGenre", genre);
        } else {
            games = gameService.getAllGames();
        }

        model.addAttribute("games", games);
        model.addAttribute("genres", gameService.getAllGenres());

        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            List<Long> libraryIds = user.getLibrary().stream()
                .map(Game::getId).toList();
            List<Long> cartIds = user.getCart().stream()
                .map(Game::getId).toList();
            model.addAttribute("libraryIds", libraryIds);
            model.addAttribute("cartIds", cartIds);
        }

        return "games/catalog";
    }

    @GetMapping("/games/{id}")
    public String gameDetail(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Game game = gameService.getGameById(id);
        model.addAttribute("game", game);

        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            boolean owned = user.getLibrary().stream()
                .anyMatch(g -> g.getId().equals(id));
            boolean inCart = user.getCart().stream()
                .anyMatch(g -> g.getId().equals(id));
            model.addAttribute("owned", owned);
            model.addAttribute("inCart", inCart);
        }

        return "games/detail";
    }

    @GetMapping("/ai-search")
    public String aiSearchPage(Model model) {
        return "games/ai-search";
    }

    @PostMapping("/ai-search")
    public String aiSearch(
            @RequestParam String query,
            Model model) {

        List<Game> similarGames = aiService.findSimilarGames(query, 6);
        String aiResponse = aiService.getRecommendation(query, similarGames);

        model.addAttribute("query", query);
        model.addAttribute("aiResponse", aiResponse);
        model.addAttribute("similarGames", similarGames);

        return "games/ai-search";
    }
}
