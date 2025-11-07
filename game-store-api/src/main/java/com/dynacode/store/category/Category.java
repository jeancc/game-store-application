package com.dynacode.store.category;

import com.dynacode.store.common.BaseEntity;
import com.dynacode.store.game.Game;
import jakarta.persistence.Entity;

import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category extends BaseEntity {


    private String name;
    private String description;

    @OneToMany(mappedBy = "category") //igual al nombre del campo en la entidad 'Game'
    private List<Game> games;


}
