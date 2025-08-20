package com.unir.buscador.controller;

import com.unir.buscador.model.Item;
import com.unir.buscador.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemRepository repo;

    public ItemController(ItemRepository repo){
        this.repo = repo;
    }

    @GetMapping
    public List<Item> all(@RequestParam(required = false) String nombre){
        if(nombre == null){
            return repo.findAll();
        }
        return repo.findByNombreContainingIgnoreCase(nombre);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody Item item){
        return repo.save(item);
    }

    @GetMapping("/{id}")
    public Item getOne(@PathVariable Long id){
        return repo.findById(id).orElseThrow(()->new RuntimeException("Item no encontrado: " + id));
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item nuevo){
        return repo.findById(id).map(item -> {
            item.setNombre(nuevo.getNombre());
            item.setDescripcion(nuevo.getDescripcion());
            item.setPrecio(nuevo.getPrecio());
            return repo.save(item);
        }).orElseThrow(()-> new RuntimeException("Item no encontrado" + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        repo.deleteById(id);
    }

}
