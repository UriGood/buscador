package com.unir.buscador.controller;

import com.unir.buscador.exception.ItemNotFoundException;
import com.unir.buscador.model.Item;
import com.unir.buscador.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<Item> all() throws IOException {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody Item item) throws IOException {
        return service.save(item);
    }

    @GetMapping("/{id}")
    public Item getOne(@PathVariable String id) throws IOException {
        Item item = service.getById(id);
        if (item == null) {
            throw new ItemNotFoundException("Producto no encontrado con id: " + id);
        }
        return item;
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable String id, @RequestBody Item nuevo) throws IOException {
        Item existente = service.getById(id);
        if (existente == null) {
            throwNotFound(id);
        }
        return service.save(updateItemFields(existente, nuevo));
    }

    private Item updateItemFields(Item item, Item nuevo) {
        if (nuevo.getTitle() != null && !nuevo.getTitle().isBlank()) {
            item.setTitle(nuevo.getTitle());
        }
        if (nuevo.getDescription() != null && !nuevo.getDescription().isBlank()) {
            item.setDescription(nuevo.getDescription());
        }
        if (nuevo.getPrice() != null) {
            item.setPrice(nuevo.getPrice());
        }
        if (nuevo.getThumbnail() != null && !nuevo.getThumbnail().isBlank()) {
            item.setThumbnail(nuevo.getThumbnail());
        }
        if (nuevo.getRating() != null) {
            item.setRating(nuevo.getRating());
        }
        if (nuevo.getStock() != null) {
            item.setStock(nuevo.getStock());
        }
        if (nuevo.getCategory() != null && !nuevo.getCategory().isBlank()) {
            item.setCategory(nuevo.getCategory());
        }
        return item;
    }

    private Item throwNotFound(String id) {
        throw new ItemNotFoundException("Producto no encontrado con id: " + id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) throws IOException {
        service.delete(id);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam("q") String keyword) throws IOException {
        return service.searchFullText(keyword);
    }

    @GetMapping("/search-full-text")
    public List<Item> searchFullText(@RequestParam("q") String query) throws IOException {
        return service.searchFullText(query);
    }

    @GetMapping("/autocomplete")
    public List<Item> autocomplete(@RequestParam("q") String query) throws IOException {
        return service.autocomplete(query);
    }

    @GetMapping("/facets")
    public Map<String, Long> getFacets() throws IOException {
        return service.getFacets();
    }
}
