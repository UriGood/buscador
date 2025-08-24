package com.unir.buscador.service;

import com.unir.buscador.model.Item;
import com.unir.buscador.repository.ItemRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repo;
    private final ElasticsearchOperations elasticsearchOperations;


    public ItemService(ItemRepository repo, ElasticsearchOperations elasticsearchOperations){
        this.repo = repo;
        this.elasticsearchOperations = elasticsearchOperations;
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
}