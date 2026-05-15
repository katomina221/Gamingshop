package com.university.gamestore.controller;

import com.university.gamestore.model.Game;
import com.university.gamestore.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final GameService gameService;

    @GetMapping
    public String adminPanel(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        return "admin/panel";
    }

    @GetMapping("/games/new")
    public String newGameForm(Model model) {
        model.addAttribute("game", new Game());
        return "admin/game-form";
    }

    @PostMapping("/games/save")
    public String saveGame(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam String genre,
            @RequestParam String developer,
            @RequestParam(required = false) String imageUrl) {

        Game game = new Game(title, description, price, genre, developer, imageUrl);
        gameService.saveGame(game);

        return "redirect:/admin";
    }

    @PostMapping("/games/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/admin";
    }
}
