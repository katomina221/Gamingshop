package com.university.gamestore.service;

import com.university.gamestore.model.Game;
import com.university.gamestore.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final GameRepository gameRepository;

    private static final Set<String> STOP_WORDS = Set.of(
            "и", "или", "а", "но", "что", "как", "так", "для", "от", "до",
            "в", "на", "по", "с", "со", "о", "об", "за", "из", "к", "у",
            "это", "то", "тот", "та", "те", "мне", "мной", "меня",
            "хочу", "хотел", "ищу", "нужно", "нужна", "нужен", "нравится",
            "игра", "игры", "игру", "играть", "что-то", "какой-то", "какую-то",
            "the", "a", "an", "is", "are", "to", "for", "of", "with"
    );

    private static final Map<String, List<String>> SYNONYMS = Map.ofEntries(

            Map.entry("рпг", List.of("rpg", "ролевая")),
            Map.entry("ролевая", List.of("rpg", "ролевая")),
            Map.entry("шутер", List.of("шутер", "shooter", "fps")),
            Map.entry("стрелялка", List.of("шутер", "shooter")),
            Map.entry("экшен", List.of("экшен", "action")),
            Map.entry("приключение", List.of("приключение", "adventure")),
            Map.entry("стратегия", List.of("стратегия", "strategy", "rts")),
            Map.entry("симулятор", List.of("симулятор", "simulator", "sim")),
            Map.entry("ферма", List.of("симулятор", "ферм")),
            Map.entry("инди", List.of("инди", "indie")),
            Map.entry("moba", List.of("moba")),
            Map.entry("мультиплеер", List.of("moba", "shooter", "онлайн")),
            Map.entry("онлайн", List.of("moba", "shooter", "онлайн")),

            Map.entry("расслабляющая", List.of("симулятор", "инди", "ферм")),
            Map.entry("спокойная", List.of("симулятор", "инди")),
            Map.entry("сложная", List.of("souls", "elden", "hollow", "fromsoftware")),
            Map.entry("хардкор", List.of("souls", "elden", "fromsoftware")),
            Map.entry("открытый", List.of("открытый мир", "rpg", "witcher", "gta")),
            Map.entry("мир", List.of("открытый мир", "rpg")),
            Map.entry("атмосферная", List.of("инди", "hollow")),
            Map.entry("бесплатная", List.of("free", "бесплат")),
            Map.entry("бесплатно", List.of("free", "бесплат")),
            Map.entry("даркфэнтези", List.of("souls", "elden")),
            Map.entry("фэнтези", List.of("rpg", "witcher", "elden")),
            Map.entry("киберпанк", List.of("киберпанк", "cyberpunk"))
    );

    public List<Game> findSimilarGames(String query, int topK) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        List<String> tokens = tokenize(query);
        if (tokens.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> searchTerms = new HashSet<>();
        for (String token : tokens) {
            searchTerms.add(token);
            if (SYNONYMS.containsKey(token)) {
                searchTerms.addAll(SYNONYMS.get(token));
            }
        }

        boolean wantsFree = tokens.stream().anyMatch(
                t -> t.contains("бесплат") || t.equals("free"));

        List<Game> allGames = gameRepository.findAll();

        List<ScoredGame> scored = new ArrayList<>();
        for (Game g : allGames) {
            int score = scoreGame(g, searchTerms, wantsFree);
            if (score > 0) {
                scored.add(new ScoredGame(g, score));
            }
        }

        scored.sort(Comparator.comparingInt(ScoredGame::score).reversed());
        return scored.stream()
                .limit(topK)
                .map(ScoredGame::game)
                .collect(Collectors.toList());
    }

    private int scoreGame(Game game, Set<String> searchTerms, boolean wantsFree) {
        int score = 0;
        String title = lower(game.getTitle());
        String genre = lower(game.getGenre());
        String developer = lower(game.getDeveloper());
        String description = lower(game.getDescription());

        for (String term : searchTerms) {
            if (term.length() < 2) continue;
            if (title.contains(term)) score += 5;
            if (genre.contains(term)) score += 4;
            if (developer.contains(term)) score += 3;
            if (description.contains(term)) score += 2;
        }

        if (wantsFree && game.getPrice() != null
                && game.getPrice().doubleValue() == 0.0) {
            score += 10;
        }

        if (wantsFree && game.getPrice() != null
                && game.getPrice().doubleValue() > 0.0) {
            score -= 5;
        }

        return score;
    }

    public String getRecommendation(String userRequest, List<Game> similarGames) {
        if (similarGames == null || similarGames.isEmpty()) {
            return "К сожалению, ничего подходящего под ваш запрос не нашлось. " +
                   "Попробуйте описать свои предпочтения иначе или загляните в полный каталог.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("По вашему запросу подобрано ").append(similarGames.size())
          .append(similarGames.size() == 1 ? " игра:" : " игр(ы):").append("\n\n");

        for (Game g : similarGames) {
            sb.append("• ").append(g.getTitle())
              .append(" (").append(g.getGenre()).append(") — ");
            if (g.getPrice() != null && g.getPrice().doubleValue() == 0.0) {
                sb.append("бесплатно");
            } else {
                sb.append("$").append(g.getPrice());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private List<String> tokenize(String query) {
        return Arrays.stream(query.toLowerCase()
                        .replaceAll("[^a-zа-яё0-9\\s-]", " ")
                        .split("\\s+"))
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .filter(t -> !STOP_WORDS.contains(t))
                .collect(Collectors.toList());
    }

    private String lower(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private record ScoredGame(Game game, int score) {}
}
