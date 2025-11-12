package com.dynacode.store.game;

import com.dynacode.store.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //con esto, crea constructor con los campos final
public class GameService {

    private final GameRepository gameRepository;

    public void something(String categoryId)
    {
        Category cat = new Category();
        cat.setId(categoryId);

        //this will be ignored on the database level
        cat.setName("dfsdafa");
        cat.setDescription("gdfsgsdf");

        var games = gameRepository.findAllByCategory(cat);
    }
}
