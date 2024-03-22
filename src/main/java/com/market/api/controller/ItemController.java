package com.market.api.controller;

import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.model.Item;
import com.market.api.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.save(itemRequestDto));
    }

    @PutMapping(value = "/{itemUuid}")
    public ResponseEntity<ItemResponseDto> updateItem(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                      @PathVariable("itemUuid") String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.updateItem(uuid, itemRequestDto));
    }

    @DeleteMapping(value = "/{itemUuid}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemUuid") String uuid) {
        itemService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{itemUuid}")
    public ResponseEntity<ItemResponseDto> findItem(@PathVariable("itemUuid") String uuid) {
        return ResponseEntity.ok(itemService.findItemByUuid(uuid));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> findItems() {
        return ResponseEntity.ok(itemService.findItems());
    }

    @PatchMapping("/{itemUuid}/status/{isEnabled}")
    public ResponseEntity<ItemResponseDto> enableOrDisableItem(@PathVariable("itemUuid") String itemUuid,
                                                               @PathVariable("isEnabled") boolean isEnabled) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.changeItemStatus(itemUuid, isEnabled));
    }
}
