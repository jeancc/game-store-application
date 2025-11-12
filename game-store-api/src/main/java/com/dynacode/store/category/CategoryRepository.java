package com.dynacode.store.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String>
{
    /**
     * Este método por debajo hará
     *
     * select * from category where name like 'a%' order by name asc
     *
     * Es bastante largo el nombre del método, mirar en el siguiente método
     * cómo emplear anotacioens de jpa para evitar esto.
     */
    List<Category> findAllByNameStartingWithIgnoreCaseOrderByNameAsc(String name);


    // JPQL Syntax, al arrancar el programa el sistema lo comprueba y
    //saltarán errores si pasa algo
    @Query("""
          SELECT c FROM Category c
          WHERE c.name like lower(:catName)
          ORDER BY c.name ASC
          """)
    List<Category> findAllByName(@Param("catName") String categoryName);


    @Query(value = "select * from category where name like :catName order by name ASC", nativeQuery = true)
    List<Category> findAllByNameUsingNativeQuery(@Param("catName") String categoryName);


    //@Query(name="Category.findByName")
    @Query //buscará la query Category.namedQueryFindByName
    List<Category> namedQueryFindByName(@Param("catName") String categoryName);

}
