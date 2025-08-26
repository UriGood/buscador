package com.unir.buscador.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.unir.buscador.model.Item;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ElasticsearchClient esClient;
    private static final String INDEX = "items";

    public ItemService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    // Obtener todos los items
    public List<Item> getAll() throws IOException {
        SearchResponse<Item> response = esClient.search(s -> s
                        .index(INDEX)
                        .size(100)
                        .query(q -> q.matchAll(m -> m)),
                Item.class
        );

        return response.hits().hits().stream()
                .map(hit -> {
                    Item item = hit.source();
                    if (item != null) {
                        item.setId(hit.id()); // asignar _id de ES
                    }
                    return item;
                })
                .toList();
    }

    //  Guardar o actualizar un item
    public Item save(Item item) throws IOException {
        IndexResponse response = esClient.index(i -> i
                .index(INDEX)
                .id(item.getId()) // si viene null, ES asigna uno
                .document(item)
        );
        if (item.getId() == null) {
            item.setId(response.id());
        }
        return item;
    }

    //  Buscar por id
    public Item getById(String id) throws IOException {
        GetResponse<Item> response = esClient.get(g -> g
                        .index("items")
                        .id(id),
                Item.class
        );
        if (response.found()) {
            Item item = response.source();
            item.setId(response.id());
            return item;
        }
        return null;
    }

    //  Eliminar por id
    public void delete(String id) throws IOException {
        esClient.delete(d -> d.index(INDEX).id(id));
    }

    // Autocompletado (ejemplo: títulos que comiencen con prefijo)
    public List<Item> autocomplete(String query) throws IOException {
        SearchResponse<Item> response = esClient.search(s -> s
                        .index(INDEX)
                        .size(5)
                        .query(q -> q
                                .multiMatch(m -> m
                                        .query(query)
                                        .fields("title")
                                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BoolPrefix)
                                )
                        ),
                Item.class
        );

        return response.hits().hits().stream()
                .map(hit -> {
                    Item item = hit.source();
                    if (item != null) {
                        item.setId(hit.id());
                    }
                    return item;
                })
                .toList();
    }

    //  Facetas (categorías y cantidad de docs)
    public Map<String, Long> getFacets() throws IOException {
        var response = esClient.search(s -> s
                        .index(INDEX)
                        .size(0)
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

    //  Búsqueda en título + descripción
    public List<Item> searchFullText(String query) throws IOException {
        SearchResponse<Item> response = esClient.search(s -> s
                        .index(INDEX)
                        .query(q -> q
                                .multiMatch(m -> m
                                        .query(query)
                                        .fields("title", "description")
                                )
                        ),
                Item.class
        );

        return response.hits().hits().stream()
                .map(hit -> {
                    Item item = hit.source();
                    if (item != null) {
                        item.setId(hit.id());
                    }
                    return item;
                })
                .toList();
    }
}
