package com.dynacode.store.game;

import com.dynacode.store.category.Category;
import com.dynacode.store.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;


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


    public PageResponse<Game> pagedResult(final int page, final int size)
    {
        Pageable pageable = PageRequest.of(page,
                size,
                Sort.by( Sort.Direction.DESC, "title","createTime")
        );

        Page<Game> pagedResult = gameRepository.findAllByCategoryName("anyCat", pageable);

        //gameRepository.findAll(pageable);

        return PageResponse.<Game>builder()
                .content(pagedResult.getContent())
                .totalPages(pagedResult.getTotalPages())
                .totalElements(pagedResult.getTotalElements())
                .isFirst(pagedResult.isFirst())
                .isLast(pagedResult.isLast())
                .build();
    }

    /**
     * buscar juego por título, que es único en bd
     */
    public void queryByExampleCaseSensitive()
    {
        Game game = new Game();
        game.setTitle("The witcher III");
        game.setSupportedPlatforms(SupportedPlatforms.PS);

        Example<Game> example = Example.of(game);

        Optional<Game> myGame = gameRepository.findOne(example);
    }



    /**
     * buscar juego por título, que es único en bd
     */
    public void queryByExampleCaseInsensitive()
    {
        //Búsqueda por 'example'
        Game game = new Game();
        game.setTitle("The witcher III");
        game.setSupportedPlatforms(SupportedPlatforms.PS);

        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase();

        Example<Game> example = Example.of(game, matcher);

        Optional<Game> myGame = gameRepository.findOne(example);




    }


    /**
     * buscar juego por título, que es único en bd
     */
    public void queryByExampleCustomMatching()
    {
        Game game = new Game();
        game.setTitle("witcher");
        game.setSupportedPlatforms(SupportedPlatforms.PS);

        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("supportedPlatforms", ExampleMatcher.GenericPropertyMatchers.exact());
        //select * from game where lower(title) like '%witcher%' and supportedPlatforms like 'PS'


        Example<Game> example = Example.of(game, matcher);

        List<Game> myGame = gameRepository.findAll(example);
    }


    /**
     */
    public void queryByExampleIgnoringProperties()
    {
        Game game = new Game();
        game.setTitle("witcher");

        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
                .withIgnorePaths("supportedPlatforms", "coverPicture");


        Example<Game> example = Example.of(game, matcher);
        List<Game> myGame = gameRepository.findAll(example);
    }


    public void specificationExample1()
    {
        Specification<Game> spec = buildSpecificationWithAndOperator("witcher", SupportedPlatforms.PC);

        List<Game> games = gameRepository.findAll(spec);
    }

    public void specificationExample2()
    {
        Specification<Game> spec = buildSpecificationWithOrOperator("witcher", SupportedPlatforms.PC);

        List<Game> games = gameRepository.findAll(spec);
    }

    private Specification<Game> buildSpecificationWithAndOperator(String title, SupportedPlatforms platform)
    {
        Specification<Game> spec = Specification.where(null);

        if (StringUtils.hasLength(title))
            spec = spec.and(GameSpecifications.byGameTitle(title));

        if (platform != null)
            spec = spec.and(GameSpecifications.bySupportedPlatform(platform));


        return spec;
    }

    private Specification<Game> buildSpecificationWithOrOperator(String title, SupportedPlatforms platform)
    {
        Specification<Game> spec = Specification.where(null);

        if (StringUtils.hasLength(title))
            spec = spec.and(GameSpecifications.byGameTitle(title));

        if (platform != null)
            spec = spec.or(GameSpecifications.bySupportedPlatform(platform));


        return spec;
    }
}
