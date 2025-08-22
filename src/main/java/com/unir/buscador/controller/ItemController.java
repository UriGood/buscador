package com.unir.buscador.controller;

import com.unir.buscador.model.Item;
import com.unir.buscador.repository.ItemRepository;
import com.unir.buscador.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemRepository repo;
    private final ItemService itemService;


    public ItemController(ItemRepository repo, ItemService itemService) {
        this.repo = repo;
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> all(@RequestParam(required = false) String title){
        if(title == null){
            return repo.findAll();
        }
        return repo.findByTitleContainingIgnoreCase(title);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody Item item){
        return repo.save(item);
    }

    @GetMapping("/{id}")
    public Item getOne(@PathVariable Long id){
        return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado: " + id));
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item nuevo){
        return repo.findById(id).map(item -> {
            item.setTitle(nuevo.getTitle());
            item.setDescription(nuevo.getDescription());
            item.setPrice(nuevo.getPrice());
            item.setThumbnail(nuevo.getThumbnail());
            item.setRating(nuevo.getRating());
            item.setStock(nuevo.getStock());
            return repo.save(item);
        }).orElseThrow(()-> new RuntimeException("Item no encontrado" + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        repo.deleteById(id);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam("q") String keyword) {
        return itemService.searchItems(keyword);
    }

}
