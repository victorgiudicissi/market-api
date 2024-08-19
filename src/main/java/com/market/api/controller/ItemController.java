package com.market.api.controller;

import com.market.api.dto.Page;
import com.market.api.dto.item.FilterItemRequestDto;
import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.service.ItemService;
import com.market.api.entity.Item;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ItemRequestDto data) {
        Item result = itemService.save(data.toItem());
        return ResponseEntity.status(HttpStatus.CREATED).body(result.toItemResponseDto());
    }

    @PutMapping(value = "/{itemUuid}")
    public ResponseEntity<ItemResponseDto> updateItem(@Valid @RequestBody Item data,
                                                      @PathVariable("itemUuid") String uuid) {
        Item result = itemService.updateItem(uuid, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.toItemResponseDto());
    }

    @DeleteMapping(value = "/{itemUuid}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemUuid") String uuid) {
        itemService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{itemUuid}")
    public ResponseEntity<ItemResponseDto> findItem(@PathVariable("itemUuid") String uuid) {
        return ResponseEntity.ok(itemService.findItemByUuid(uuid).toItemResponseDto());
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponseDto>> findItems(@Valid FilterItemRequestDto filter) {
        List<Item> result = itemService.findItemsByFilter(filter);

        List<ItemResponseDto> itemsDTO = result.stream()
                .map(Item::toItemResponseDto)
                .collect(Collectors.toList());

        Page<ItemResponseDto> pagination = new Page<>();
        pagination.setPage(filter.getPage());
        pagination.setSize(filter.getSize());
        pagination.setContent(itemsDTO);

        return ResponseEntity.ok(pagination);
    }

    @PatchMapping("/{itemUuid}/status/{isEnabled}")
    public ResponseEntity<ItemResponseDto> enableOrDisableItem(@PathVariable("itemUuid") String itemUuid,
                                                               @PathVariable("isEnabled") boolean isEnabled) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.changeItemStatus(itemUuid, isEnabled).toItemResponseDto());
    }
}
