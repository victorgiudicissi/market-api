package com.market.api.service;

import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto save(ItemRequestDto itemRequestDto);
    void delete(String itemUuid);
    ItemResponseDto findItem(String itemUuid);
    List<ItemResponseDto> findItems();
}
