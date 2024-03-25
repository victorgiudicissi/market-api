package com.market.api.service;

import com.market.api.dto.Page;
import com.market.api.dto.item.FilterItemRequestDto;
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
    Page<ItemResponseDto> findItemsByFilter(FilterItemRequestDto filter);
    List<Item> saveAll(Set<Item> items);
    ItemResponseDto changeItemStatus(String itemUuid, boolean status);
}
