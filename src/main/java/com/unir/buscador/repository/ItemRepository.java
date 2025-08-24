package com.unir.buscador.repository;

import com.unir.buscador.model.Item;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;



import java.util.List;

public interface ItemRepository extends ElasticsearchRepository<Item, String> {
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"description\"]}}")
    List<Item> searchByTitleOrDescription(String title, String description);

}
