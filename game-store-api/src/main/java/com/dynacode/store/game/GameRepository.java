package com.dynacode.store.game;

import com.dynacode.store.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository
        extends JpaRepository<Game, String>, JpaSpecificationExecutor<Game>
{

    //encontrar todos los juegos de una categoría v1
    List<Game> findAllByCategory(Category category);

    //encontrar todos los juegos de una categoría v2
    List<Game> findAllByCategoryId(String categoryId);

    //encontrar todos los juegos de la categoría con el nombre X
    //select g.* from category c inner join game g on c.id=g.category_id
    //where c.name = ''
    List<Game> findAllByCategoryName(String categoryName);


    //List<Game> findAllByCategoryName(String categoryName, Pageable pageable);
    Page<Game> findAllByCategoryName(String categoryName, Pageable pageable);


//    @Query("""
//            SELECT g from Game g
//            INNER JOIN Category c ON g.category.id = c.id
//            WHERE c.name LIKE :catName
//            """)
    @Query("""
            SELECT g from Game g
            INNER JOIN g.category c
            WHERE c.name LIKE :catName
            """)
    List<Game> findAllByCat(@Param("catName") String catName);



    @Query("""
        update Game set title = upper(title)
        """)
    @Modifying
    public void transformGamesTitleToUpperCase();
}
