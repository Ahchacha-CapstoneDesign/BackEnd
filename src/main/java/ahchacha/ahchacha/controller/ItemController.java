package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.Category;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "아이템 등록", description = "canBorrowDateTime/returnDateTime 예시 : 2024-03-17T10:26:08")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ItemDto.ItemResponseDto> create(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                          @RequestParam(name = "title") String title,
                                                          @RequestParam(name = "pricePerHour") int pricePerHour,
                                                          @RequestParam(name = "canBorrowDateTime") LocalDateTime canBorrowDateTime,
                                                          @RequestParam(name = "returnDateTime") LocalDateTime returnDateTime,
                                                          @RequestParam(name = "borrowPlace") String borrowPlace,
                                                          @RequestParam(name = "returnPlace") String returnPlace,
                                                          @RequestParam(name = "introduction") String introduction,
//                                                          @RequestParam(name = "reservation") Reservation reservation,
//                                                          @RequestParam(name = "rentingstatus") RentingStatus rentingStatus,
                                                          @RequestParam(name = "category") Category category,
                                                          HttpSession session){

        ItemDto.ItemRequestDto itemRequestDto = ItemDto.ItemRequestDto.builder()
                .title(title)
                .pricePerHour(pricePerHour)
                .canBorrowDateTime(canBorrowDateTime)
                .returnDateTime(returnDateTime)
                .borrowPlace(borrowPlace)
                .returnPlace(returnPlace)
                .introduction(introduction)
//                .reservation(reservation)
//                .rentingStatus(rentingStatus)
                .category(category)
                .build();

        ItemDto.ItemResponseDto itemResponseDto = itemService.createItem(itemRequestDto, files, session);
        return new ResponseEntity<>(itemResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "아이템 상세 조회", description = "{itemId} 자리에 상세 조회할 아이템 id를 전달해주세요.")
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto.ItemResponseDto> getTalkById(@PathVariable Long itemId) {
        Optional<ItemDto.ItemResponseDto> optionalItemDto = itemService.getItemById(itemId);

        return optionalItemDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "viewCount가 높은 순으로 카테고리 10개를 추출")
    @GetMapping("/categories/top")
    public ResponseEntity<List<ItemDto.CategoryCountDto>> getTopCategoriesByViewCount() {
        List<ItemDto.CategoryCountDto> topCategories = itemService.getTopCategoriesByViewCount(10);
        return ResponseEntity.ok(topCategories);
    }

    @Operation(summary = "조회수 많은 순으로 아이템 목록 조회")
    @GetMapping("/view-counts")
    public ResponseEntity<Page<ItemDto.ItemResponseDto>> getAllTalksByViewCounts(@RequestParam(value = "page", defaultValue = "1") int page) {
        Page<ItemDto.ItemResponseDto> itemsDtoPage = itemService.getAllItemsByViewCount(page);
        return ResponseEntity.ok(itemsDtoPage);
    }

    @Operation(summary = "등록된 아이템 최신순 조회")
    @GetMapping("/latest")
    public ResponseEntity<Page<ItemDto.ItemResponseDto>> getAllItems(@RequestParam(value = "page", defaultValue = "1")int page) {
        Page<ItemDto.ItemResponseDto> itemsDtoPage = itemService.getAllItems(page);
        return new ResponseEntity<>(itemsDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "등록된 아이템 예약가능 순 조회", description = "YES / NO 순 정렬")
    @GetMapping("/reservation")
    public ResponseEntity<Page<ItemDto.ItemResponseDto>> getAllItemsByReservation(@RequestParam(value = "page", defaultValue = "1")int page) {
        Page<ItemDto.ItemResponseDto> itemsDtoPage = itemService.getAllItemsByReservation(page);
        return new ResponseEntity<>(itemsDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "등록된 아이템 학교 or 개인 조회", description = "OFFICIAL / PERSON 순 정렬")
    @GetMapping("/personOrOfiicial")
    public ResponseEntity<Page<ItemDto.ItemResponseDto>> getAllItemsByPersonOrOfficial(@RequestParam(value = "page", defaultValue = "1")int page) {
        Page<ItemDto.ItemResponseDto> itemsDtoPage = itemService.getAllItemsByPersonOrOfficial(page);
        return new ResponseEntity<>(itemsDtoPage, HttpStatus.OK);
    }


    @Operation(summary = "아이템 검색", description = "제목으로 검색")
    @GetMapping("/search-title")
    public ResponseEntity<Page<ItemDto.ItemResponseDto>> searchItemByTitle(@RequestParam(value = "title") String title,
                                                                           @RequestParam(value = "page", defaultValue = "1") int page) {

        Page<ItemDto.ItemResponseDto> itemPages = itemService.searchItemByTitle(title, page);
        return ResponseEntity.ok(itemPages);
    }

    @Operation(summary = "아이템 검색", description = "카테고리로 검색")
    @GetMapping("/search-category")
    public ResponseEntity<Page<ItemDto.ItemResponseDto>> searchItemByCategory(@RequestParam(value = "category") String category,
                                                                              @RequestParam(value = "page", defaultValue = "1") int page) {

        Page<ItemDto.ItemResponseDto> itemPages = itemService.searchItemByCategory(category, page);
        return ResponseEntity.ok(itemPages);
    }

    @Operation(summary = "등록한 아이템 삭제", description = "item의 id를 입력하세요, 로그인 한 사용자의 물품이 아니면 삭제가 되지않습니다.")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        itemService.deleteItem(itemId, user);
        return ResponseEntity.ok().build();
    }
}