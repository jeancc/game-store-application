package com.dynacode.store.game;

import com.dynacode.store.category.Category;
import org.springframework.stereotype.Service;

@Service
public class GameMapper
{
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
}
