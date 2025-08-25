package com.unir.buscador.controller;

import com.unir.buscador.exception.ItemNotFoundException;
import com.unir.buscador.model.Item;
import com.unir.buscador.repository.ItemRepository;
import com.unir.buscador.service.ItemService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;


    public ItemController(ItemService service){
        this.service = service;
    }


    @GetMapping
    public Iterable<Item> all(){
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody Item item){
        return service.save(item);
    }

    // GET /items/{id} → obtener uno por ID
    @GetMapping("/{id}")
    public Item getOne(@PathVariable String id){
        Item item = service.getById(id);
        if (item == null){
            throw new ItemNotFoundException("Producto no encontrado con id: " + id);
        }
        return item;
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable String id, @RequestBody Item nuevo){
        Item existente = service.getById(id);
        if (existente == null) {
            throwNotFound(id);
        }
        return service.save(updateItemFields(existente, nuevo));
    }

    // Método auxiliar para copiar los campos
    private Item updateItemFields(Item item, Item nuevo) {
        if (nuevo.getTitle() != null && !nuevo.getTitle().isBlank()) {
            item.setTitle(nuevo.getTitle());
        }
        if (nuevo.getDescription() != null && !nuevo.getDescription().isBlank()) {
            item.setDescription(nuevo.getDescription());
        }
        if (nuevo.getPrice() != null) { // si price es objeto (ej: BigDecimal, Double, Integer)
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

    // Lanzamos excepción clara
    private Item throwNotFound(String id) {
        throw new ItemNotFoundException("Producto no encontrado con id: " + id);
    }


    // DELETE /items/{id} → eliminar item
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id){
        service.delete(id);
    }


    @GetMapping("/search")
    public List<Item> search(@RequestParam("q") String keyword){
        return service.searchItems(keyword);
    }

    // 🔹 Full-text avanzado (multiMatch)
    @GetMapping("/search-full-text")
    public List<Item> searchFullText(@RequestParam("q") String query) throws IOException {
        return service.searchFullText(query);
    }

    // 🔹 Autocomplete
    @GetMapping("/autocomplete")
    public List<Item> autocomplete(@RequestParam("q") String query) throws IOException {
        return service.autocomplete(query);
    }

    // 🔹 Facets
    @GetMapping("/facets")
    public Map<String, Long> getFacets() throws IOException {
        return service.getFacets();
    }

}
