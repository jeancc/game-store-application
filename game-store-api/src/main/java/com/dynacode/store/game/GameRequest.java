package com.dynacode.store.game;

import com.dynacode.store.platform.Platform;

import java.util.List;
import java.util.Set;

public record GameRequest(
        String title, //no vamos a permitir duplicados
        String categoryId,//hay que comprobar que existe
        List<String> platforms //hay que comprobar que existen
)
{

}
