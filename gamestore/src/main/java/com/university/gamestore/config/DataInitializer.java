package com.university.gamestore.config;

import com.university.gamestore.model.Game;
import com.university.gamestore.model.User;
import com.university.gamestore.repository.GameRepository;
import com.university.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (gameRepository.count() > 0) {
            return;
        }

        log.info("Инициализация тестовых данных...");

        User admin = new User("admin", "admin@gamestore.com",
            passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        User user = new User("player1", "player@gamestore.com",
            passwordEncoder.encode("player123"));
        userRepository.save(user);

        gameRepository.save(new Game(
            "Cyberpunk 2077",
            "Открытый мир в мегаполисе будущего. RPG с видом от первого лица в мире киберпанка.",
            new BigDecimal("29.99"), "RPG", "CD Projekt Red",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co4hna.jpg"
        ));

        gameRepository.save(new Game(
            "The Witcher 3: Wild Hunt",
            "Эпическое RPG с огромным открытым миром. Играйте за Геральта из Ривии.",
            new BigDecimal("9.99"), "RPG", "CD Projekt Red",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co1wyy.jpg"
        ));

        gameRepository.save(new Game(
            "Counter-Strike 2",
            "Легендарный тактический шутер. Команды террористов против спецназа.",
            new BigDecimal("0.00"), "Шутер", "Valve",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co6nnr.jpg"
        ));

        gameRepository.save(new Game(
            "Elden Ring",
            "Action RPG в тёмном фэнтезийном мире. Сложная, но невероятно глубокая игра.",
            new BigDecimal("59.99"), "Action RPG", "FromSoftware",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg"
        ));

        gameRepository.save(new Game(
            "Stardew Valley",
            "Расслабляющий симулятор фермы. Выращивай овощи, заводи друзей и исследуй пещеры.",
            new BigDecimal("14.99"), "Симулятор", "ConcernedApe",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co18j5.jpg"
        ));

        gameRepository.save(new Game(
            "Dota 2",
            "Командная стратегия в реальном времени. 5 на 5, разрушь трон врага.",
            new BigDecimal("0.00"), "MOBA", "Valve",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co5s5v.jpg"
        ));

        gameRepository.save(new Game(
            "Hollow Knight",
            "Метroidvania в подземном мире насекомых. Красивая и атмосферная игра.",
            new BigDecimal("14.99"), "Инди", "Team Cherry",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co1rgi.jpg"
        ));

        gameRepository.save(new Game(
            "Grand Theft Auto V",
            "Огромный открытый мир Лос-Сантоса. История трёх преступников.",
            new BigDecimal("19.99"), "Экшен", "Rockstar Games",
            "https://images.igdb.com/igdb/image/upload/t_cover_big/co2lbd.jpg"
        ));

        log.info("Тестовые данные загружены! Admin: admin/admin123, User: player1/player123");
    }
}
