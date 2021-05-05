package com.market.api.service.impl;

import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.model.Item;
import com.market.api.repository.ItemRepository;
import com.market.api.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemResponseDto save(ItemRequestDto itemRequestDto) {
        return itemRepository.save(itemRequestDto.toItem())
                .toItemResponseDto();
    }

    @Override
    public void delete(String itemUuid) {
        Item item = itemRepository.findByUuid(itemUuid);

        if (Objects.isNull(item)) {
            log.warn("Não foi possível encontrar o item passado. itemUuid: {}", itemUuid);
            throw new DataNotFoundException("Não foi possível encontrar o item informado.");
        }

        log.info("Item encontrado com sucesso, iniciando a deleção. item: {}", item);

        itemRepository.deleteById(item.getId());
    }

    @Override
    public ItemResponseDto findItem(String itemUuid) {
        Item item = itemRepository.findByUuid(itemUuid);

        if (Objects.isNull(item)) {
            log.warn("Não foi possível encontrar o item passado. itemUuid: {}", itemUuid);
            throw new DataNotFoundException("Não foi possível encontrar o item informado.");
        }

        log.info("Item encontrado com sucesso. item: {}", item);
        return item.toItemResponseDto();
    }

    @Override
    public List<ItemResponseDto> findItems() {
        List<Item> items = itemRepository.findAll();

        if (items.isEmpty()) {
            log.warn("Não foi possível encontrar nenhum item na base.");
            throw new DataNotFoundException("Não foi possível encontrar nenhum item na base.");
        }

        log.info("Itens encontrados com sucesso. itens: {}", items);
        return items.stream()
                .map(Item::toItemResponseDto)
                .collect(Collectors.toList());
    }
}
