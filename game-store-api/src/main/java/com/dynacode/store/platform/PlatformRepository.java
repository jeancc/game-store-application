package com.dynacode.store.platform;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PlatformRepository extends JpaRepository<Platform, String> {

    @Query("""
        select p from Platform p
        where p.console in :consoles
    """)
    List<Platform> findAllByConsoleIn(@Param("consoles") List<Console> selectedConsoles);
}
