package com.market.api.service;

import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {
    ItemResponseDto save(ItemRequestDto itemRequestDto);
    ItemResponseDto updateItem(String itemUuid, ItemRequestDto itemRequestDto);
    void delete(String itemUuid);
    ItemResponseDto findItemByUuid(String itemUuid);
    List<ItemResponseDto> findItems();
    List<Item> saveAll(Set<Item> items);
    ItemResponseDto changeItemStatus(String itemUuid, boolean status);
}
