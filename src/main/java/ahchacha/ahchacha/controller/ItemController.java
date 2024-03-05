package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto.ItemResponseDto> create(@RequestBody ItemDto.ItemRequestDto itemRequestDto) {
        Item item = itemService.save(itemRequestDto);
        ItemDto.ItemResponseDto itemResponseDto = ItemDto.ItemResponseDto.toDto(item);
        return new ResponseEntity<>(itemResponseDto, HttpStatus.CREATED);
    }
}
