package ahchacha.ahchacha.service;

import ahchacha.ahchacha.aws.AmazonS3Manager;
import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.Uuid;
import ahchacha.ahchacha.domain.common.enums.Category;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.repository.ItemRepository;
import ahchacha.ahchacha.repository.ReservationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import ahchacha.ahchacha.repository.UuidRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ItemDto.ItemResponseDto createItem(ItemDto.ItemRequestDto itemDto,
                                              List<MultipartFile> files,
                                              HttpSession session) {
        User user = (User) session.getAttribute("user");

        //이미지 업로드
        List<String> pictureUrls = new ArrayList<>(); // 이미지 URL들을 저장할 리스트
        if (files != null && !files.isEmpty()){
            for (MultipartFile file : files) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder()
                        .uuid(uuid).build());
                String pictureUrl = s3Manager.uploadFile(s3Manager.generateItemKeyName(savedUuid), file);
                pictureUrls.add(pictureUrl); // 리스트에 이미지 URL 추가

                System.out.println("s3 url(클릭 시 브라우저에 사진 뜨는지 확인): " + pictureUrl);
            }
        }

        Item item = Item.builder()
                .user(user)
                .title(itemDto.getTitle())
                .pricePerHour(itemDto.getPricePerHour())
                .canBorrowDateTime(itemDto.getCanBorrowDateTime())
                .returnDateTime(itemDto.getReturnDateTime())
                .borrowPlace(itemDto.getBorrowPlace())
                .returnPlace(itemDto.getReturnPlace())
                .introduction((itemDto.getIntroduction()))
                .reservation(Reservation.YES)
                .rentingStatus(RentingStatus.NONE)
                .itemStatus(itemDto.getItemStatus())
                .imageUrls(pictureUrls)
                .category(itemDto.getCategory())
                .personOrOfficial(user.getPersonOrOfficial())
                .build();

        Item createdItem = itemRepository.save(item);
        return ItemDto.ItemResponseDto.toDto(createdItem);
    }

    public Optional<ItemDto.ItemResponseDto> getItemById(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            item.setViewCount(item.getViewCount()+1);
            itemRepository.save(item);
        }

        return optionalItem.map(ItemDto.ItemResponseDto::toDto);
    }

    @Transactional
    public ItemDto.ItemResponseDto updateItem(Long itemId, ItemDto.ItemRequestDto itemDto, List<MultipartFile> files, List<String> files2,HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<String> pictureUrls = new ArrayList<>();

        // 아이템 ID로 아이템 조회
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new IllegalArgumentException("Invalid item Id: " + itemId));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have permission to update this item.");
        }

        if(files2!=null&&!files2.isEmpty()){
            for(String file:files2){
                pictureUrls.add(file);
            }}

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder()
                        .uuid(uuid).build());
                String pictureUrl = s3Manager.uploadFile(s3Manager.generateItemKeyName(savedUuid), file);
                pictureUrls.add(pictureUrl);

                System.out.println("s3 url(클릭 시 브라우저에 사진 뜨는지 확인): " + pictureUrl);
            }
        }



        item.setTitle(itemDto.getTitle());
        item.setPricePerHour(itemDto.getPricePerHour());
        item.setCanBorrowDateTime(itemDto.getCanBorrowDateTime());
        item.setReturnDateTime(itemDto.getReturnDateTime());
        item.setBorrowPlace(itemDto.getBorrowPlace());
        item.setReturnPlace(itemDto.getReturnPlace());
        item.setIntroduction(itemDto.getIntroduction());
        item.setItemStatus(itemDto.getItemStatus());
        item.setCategory(itemDto.getCategory());
        item.setPersonOrOfficial(user.getPersonOrOfficial());
        item.setImageUrls(pictureUrls);


        Item updatedItem = itemRepository.save(item);
        return ItemDto.ItemResponseDto.toDto(updatedItem);
    }

    @Transactional
    public Page<ItemDto.ItemResponseDto> getAllMyRegisteredItems(int page, User user) { //내가 등록한 아이템들 보여주기
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); // 최근 작성순

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        return itemRepository.findByUser(user, pageable)
                .map(ItemDto.ItemResponseDto::toDto);
    }

    @Transactional
    public Page<ItemDto.ItemResponseDto> getAllItemsByReservationYes(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Item> itemPage = itemRepository.findByUserAndReservation(user, Reservation.YES, pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ItemDto.ItemResponseDto> getAllItemsByReserved(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Item> itemPage = itemRepository.findByUserAndRentingStatus(user, RentingStatus.RESERVED, pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ItemDto.ItemResponseDto> getAllItemsByRenting(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Item> itemPage = itemRepository.findByUserAndRentingStatus(user, RentingStatus.RENTING, pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ItemDto.ItemResponseDto> getAllItemsByReturned(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Item> itemPage = itemRepository.findByUserAndRentingStatus(user, RentingStatus.RETURNED, pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    public Page<ItemDto.ItemResponseDto> getAllItems(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); //최근 작성순

        Pageable pageable = PageRequest.of(page-1, 1000, Sort.by(sorts)); //한페이지에 6개
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    public Page<ItemDto.ItemResponseDto> getAllItemsByViewCount(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("viewCount"));
        sorts.add(Sort.Order.desc("createdAt"));  // 조회수가 동일하면 최신순으로 정렬

        Pageable pageable = PageRequest.of(page-1, 1000, Sort.by(sorts)); //한페이지에 6개
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    public Page<ItemDto.ItemResponseDto> getAllItemsByReservation(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("reservation")); //예약가능 여부 정렬 asc : 영문 Y가 N보다 뒤에있어서 오름차순
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1, 1000, Sort.by(sorts));
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    public Page<ItemDto.ItemResponseDto> getAllItemsByPersonOrOfficial(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("personOrOfficial"));
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1, 1000, Sort.by(sorts));
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return ItemDto.toDtoPage(itemPage);
    }

    public Page<ItemDto.ItemResponseDto> searchItemByKeyword(String keyword,int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        Category category;
        try {
            category = Category.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e){
            category = null;
        }

        Page<Item> itemPage;

        itemPage = itemRepository.findByTitleContainingOrCategory(keyword, category, pageable);

        return ItemDto.toDtoPage(itemPage);
    }

    public Page<ItemDto.ItemResponseDto> searchItemByCategory(String categoryStr, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        // String to Category enum
        Category category = Category.valueOf(categoryStr.toUpperCase());

        Page<Item> itemPage = itemRepository.findByCategory(category, pageable);

        return ItemDto.toDtoPage(itemPage);
    }

    public List<ItemDto.CategoryCountDto> getTopCategoriesByViewCount(int count) {
        List<Object[]> categoryCounts = itemRepository.findTopCategoriesByViewCount(PageRequest.of(0, count));

        List<ItemDto.CategoryCountDto> categoryCountDtos = new ArrayList<>();
        for (Object[] result : categoryCounts) {
            Category category = (Category) result[0];

            if (category.equals(Category.기타)) {
                continue; // '기타' 카테고리인 경우, 리스트에 추가하지 않고 다음 반복으로 넘어감
            }
            Long viewCount = (Long) result[1];
            categoryCountDtos.add(new ItemDto.CategoryCountDto(category, viewCount.intValue())); // viewCount를 int로 변환하여 저장
        }

        return categoryCountDtos;
    }

    public void deleteItem(Long itemId, User currentUser) {
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id: " + itemId));

        // 아이템의 userId와 현재 세션의 userId가 같은지 확인
        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this item.");
        }

        itemRepository.deleteById(itemId);
    }
    public List<ItemDto.ItemResponseDto> getTopItemsByTopCategories() {
        List<Object[]> topCategories = reservationRepository.findTopCategoriesByReservationCount();
        List<Category> categories = topCategories.stream()
                .limit(10)
                .map(result -> (Category) result[0])
                .collect(Collectors.toList());

        List<Item> topItems = itemRepository.findTopItemsByCategories(categories);

        // Item 객체들을 ItemResponseDto 객체로 변환
        List<ItemDto.ItemResponseDto> topItemsResponseDtos = topItems.stream()
                .filter(item -> item.getRentingStatus() == RentingStatus.NONE) // 대여 가능한 상태만 필터링
                .collect(Collectors.groupingBy(Item::getCategory))
                .values()
                .stream()
                .map(items -> items.get(0)) // 각 카테고리에서 top item만 선택
                .map(ItemDto.ItemResponseDto::toDto) // Item 객체를 ItemResponseDto 객체로 변환
                .collect(Collectors.toList());

        return topItemsResponseDtos;
    }
//
//    public List<ItemDto.ItemResponseDto> getTopItemsByTopCategories(Long userId) {
//        Pageable top6 = PageRequest.of(0, 6);
//        List<Object[]> topCategories = reservationRepository.findTopCategoriesByUser(userId, top6);
//
//        List<ItemDto.ItemResponseDto> topItems = new ArrayList<>();
//
//        for (Object[] category : topCategories) {
//            String categoryName = (String) category[0];
//            Pageable top1 = PageRequest.of(0, 1);
//            List<Item> items = itemRepository.findTopItemsByCategory(categoryName, top1);
//            if (!items.isEmpty()) {
//                Item item = items.get(0);
//                topItems.add(new ItemDto.ItemResponseDto(item.getId(), item.getName(), item.getDescription()));
//            }
//        }
//        return topItems;
//    }

    public List<ItemDto.ItemResponseDto> getMyTopItemsByTopCategories(User user) {
        List<Object[]> topCategories = reservationRepository.findMyTopCategoriesByUser(user.getId());
        List<Category> categories = topCategories.stream()
                .limit(6)
                .map(result -> (Category) result[0])
                .collect(Collectors.toList());

        List<Item> topItems = itemRepository.findTopItemsByCategories(categories);

        // Item 객체들을 ItemResponseDto 객체로 변환
        List<ItemDto.ItemResponseDto> MytopItemsResponseDtos = topItems.stream()
                .filter(item -> item.getRentingStatus() == RentingStatus.NONE) // 대여 가능한 상태만 필터링
                .collect(Collectors.groupingBy(Item::getCategory))
                .values()
                .stream()
                .map(items -> items.get(0)) // 각 카테고리에서 top item만 선택
                .map(ItemDto.ItemResponseDto::toDto) // Item 객체를 ItemResponseDto 객체로 변환
                .collect(Collectors.toList());

        return MytopItemsResponseDtos;
    }
}
