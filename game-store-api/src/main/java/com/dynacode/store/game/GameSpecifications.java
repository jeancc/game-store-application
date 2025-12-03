package com.dynacode.store.game;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * Las Specifications es como una serie de filtros de todo tipo
 * para aplicar a nuestras entidades. Aquí unos ejemplos:
 */
public class GameSpecifications
{

    public static Specification<Game> byGameTitle(String gameTitle)
    {
        return (Root<Game> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.equal(root.get("title"), gameTitle);
    }

    public static Specification<Game> bySupportedPlatform(SupportedPlatforms platform){
        return (Root<Game> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.equal(root.get("platform"), platform);
    }


    public static Specification<Game> byCatName(String catName){
        return (Root<Game> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.equal(root.get("category").get("name"), catName);
    }
}
