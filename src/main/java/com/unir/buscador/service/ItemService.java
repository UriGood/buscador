package com.unir.buscador.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.unir.buscador.model.Item;
import com.unir.buscador.repository.ItemRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.core.search.Hit;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository repo;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient esClient;



    public ItemService(ItemRepository repo, ElasticsearchOperations elasticsearchOperations, ElasticsearchClient esClient){
        this.repo = repo;
        this.elasticsearchOperations = elasticsearchOperations;
        this.esClient = esClient;
    }

    public Iterable<Item> getAll(){
        return repo.findAll();
    }

    public Item save(Item item){
        return repo.save(item);
    }

    public Item getById(String id){
        return repo.findById(id).orElse(null);
    }

    public void delete(String id){
        repo.deleteById(id);
    }

    public List<Item> searchItems(String keyword) {
        Criteria criteria = new Criteria("title").matches(keyword)
                .or(new Criteria("description").matches(keyword));
        Query query = new CriteriaQuery(criteria);
        return elasticsearchOperations.search(query, Item.class)
                .map(SearchHit::getContent)
                .toList();
    }

    public List<Item> autocomplete(String query) throws IOException {
        SearchResponse<Item> response = esClient.search(s -> s
                        .index("items")
                        .query(q -> q
                                .multiMatch(m -> m
                                        .query(query)
                                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BoolPrefix)
                                        .fields("title", "description", "category")
                                )
                        )
                        .size(5),
                Item.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }


    public Map<String, Long> getFacets() throws IOException {
        var response = esClient.search(s -> s
                        .index("items")
                        .size(0) // no queremos docs, solo agregaciones
                        .aggregations("categorias", a -> a
                                .terms(t -> t.field("category"))
                        ),
                Item.class
        );

        return response.aggregations()
                .get("categorias")
                .sterms()
                .buckets()
                .array()
                .stream()
                .collect(Collectors.toMap(
                        b -> b.key().stringValue(),
                        b -> b.docCount()
                ));
    }

    public List<Item> searchFullText(String query) throws IOException {
        SearchResponse<Item> response = esClient.search(s -> s
                        .index("items")
                        .query(q -> q
                                .multiMatch(m -> m
                                        .query(query)
                                        .fields("title", "description") // buscamos en ambos campos
                                )
                        ),
                Item.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}