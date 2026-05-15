package com.university.gamestore.controller;

import com.university.gamestore.model.Order;
import com.university.gamestore.model.User;
import com.university.gamestore.service.GameService;
import com.university.gamestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final GameService gameService;
    private final UserService userService;

    @GetMapping("/cart")
    public String cartPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        BigDecimal total = user.getCart().stream()
            .map(g -> g.getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cart", user.getCart());
        model.addAttribute("total", total);
        return "cart/cart";
    }

    @PostMapping("/cart/add/{gameId}")
    public String addToCart(
            @PathVariable Long gameId,
            @AuthenticationPrincipal UserDetails userDetails) {
        gameService.addToCart(userDetails.getUsername(), gameId);
        return "redirect:/games/" + gameId;
    }

    @PostMapping("/cart/remove/{gameId}")
    public String removeFromCart(
            @PathVariable Long gameId,
            @AuthenticationPrincipal UserDetails userDetails) {
        gameService.removeFromCart(userDetails.getUsername(), gameId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            Order order = gameService.purchase(userDetails.getUsername());
            model.addAttribute("order", order);
            return "cart/success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "cart/cart";
        }
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/profile";
    }

    @GetMapping("/library")
    public String libraryPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("library", user.getLibrary());
        return "profile/library";
    }
}
