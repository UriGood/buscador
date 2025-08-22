package com.unir.buscador.repository;

import com.unir.buscador.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByTitleContainingIgnoreCase(String title);

    // Busca coincidencias en descripción (ignora mayúsculas/minúsculas)
    List<Item> findByDescriptionContainingIgnoreCase(String keyword);

    // 🔹 Nuevo: búsqueda por ambos campos con un solo parámetro
    @Query("SELECT i FROM Item i " +
            "WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> searchByKeyword(@Param("keyword") String keyword);
}
