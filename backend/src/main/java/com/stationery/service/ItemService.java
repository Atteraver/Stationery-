package com.stationery.service;

import com.stationery.entity.Item;
import com.stationery.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // Module 2: Returns the full catalog and current stock
    public List<Item> findAll() {
        return itemRepository.findAll();
    }
}