package com.market.api.service;

import com.market.api.dto.Page;
import com.market.api.dto.item.FilterItemRequestDto;
import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.entity.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {
    Item save(Item data);
    Item updateItem(String itemUuid, Item data);
    void delete(String itemUuid);
    Item findItemByUuid(String itemUuid);
    List<Item> findItemsByFilter(FilterItemRequestDto filter);
    List<Item> saveAll(Set<Item> items);
    Item changeItemStatus(String itemUuid, boolean status);
}
