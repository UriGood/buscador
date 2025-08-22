package com.unir.buscador.service;

import com.unir.buscador.model.Item;
import com.unir.buscador.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> searchItems(String keyword) {
        return itemRepository.searchByKeyword(keyword);
    }
}