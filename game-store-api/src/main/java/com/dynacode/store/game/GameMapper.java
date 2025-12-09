package com.dynacode.store.game;

import com.dynacode.store.category.Category;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class GameMapper
{
    private final StringHttpMessageConverter stringHttpMessageConverter;

    public GameMapper(StringHttpMessageConverter stringHttpMessageConverter) {
        this.stringHttpMessageConverter = stringHttpMessageConverter;
    }

    public Game toGame(GameRequest gameRequest)
    {
        return Game.builder()
                .title(gameRequest.title())
                .category(
                        Category.builder()
                                .id(gameRequest.categoryId())
                                .build()
                )
                //.platforms()//TODO
                .build();

    }

    public GameResponse toGameResponse(Game game) {

        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .imageUrl("") //TODO set the cdn url
                .platforms(
                        game.getPlatforms()
                                .stream()
                                .map(p -> p.getConsole().name())
                                .collect(Collectors.toSet())
                )
                .build();

    }
}
