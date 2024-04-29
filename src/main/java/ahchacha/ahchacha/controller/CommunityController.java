package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Community;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.Category;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import ahchacha.ahchacha.dto.CommentDto;
import ahchacha.ahchacha.dto.CommunityDto;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.service.CommunityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community")
public class CommunityController {
    private final CommunityService communityService;

    @Autowired
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }


    @Operation(summary = "게시물 작성")
    @PostMapping(path ="/create",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommunityDto.CommunityResponseDto> create(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                    @RequestParam(name = "title") String title,
                                                                    @RequestParam(name = "content") String content,
                                                                    HttpSession session){
        CommunityDto.CommunityRequestDto communityRequestDto = CommunityDto.CommunityRequestDto.builder()
                .title(title)
                .content(content)
                .build();

        CommunityDto.CommunityResponseDto communityResponseDto = communityService.createBoard(communityRequestDto,files,session);
        return new ResponseEntity<>(communityResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "커뮤니티 상세 조회", description = "{communityId} 자리에 상세 조회할 커뮤니티 id를 전달해주세요.")
    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityDto.CommunityResponseDto> getCommunityId(@PathVariable Long communityId) {
        Optional<CommunityDto.CommunityResponseDto> optionalCommunityDto = communityService.getCommunityById(communityId);

        return optionalCommunityDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "게시물 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<Page<CommunityDto.CommunityResponseDto>> getAllCommunity(@RequestParam(value = "page", defaultValue = "1")int page) {
        Page<CommunityDto.CommunityResponseDto> communityDtoPage= communityService.getAllCommunity(page);
        return new ResponseEntity<>(communityDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "게시물 조회수 순 목록")
    @GetMapping("/list/view-counts")
    public ResponseEntity<Page<CommunityDto.CommunityResponseDto>> getAllCommunityByViewCounts(@RequestParam(value = "page", defaultValue = "1")int page) {
        Page<CommunityDto.CommunityResponseDto> communityDtoPage= communityService.getAllCommunityByViewCounts(page);
        return new ResponseEntity<>(communityDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "게시물 좋아요 순 목록")
    @GetMapping("/list/like-counts")
    public ResponseEntity<Page<CommunityDto.CommunityResponseDto>> getAllCommunityByLikeCounts(@RequestParam(value = "page", defaultValue = "1")int page) {
        Page<CommunityDto.CommunityResponseDto> communityDtoPage= communityService.getAllCommunityByLikeCounts(page);
        return new ResponseEntity<>(communityDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "게시물 제목으로 검색")
    @GetMapping("/list/search-title")
    public ResponseEntity<Page<CommunityDto.CommunityResponseDto>> searchCommunityByTitle(@RequestParam(value = "title") String title,
                                                                                          @RequestParam(value = "page", defaultValue = "1")int page) {
        Page<CommunityDto.CommunityResponseDto> communityDtoPage= communityService.searchCommunityByTitle(title,page);
        return new ResponseEntity<>(communityDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "게시물 내용으로 검색")
    @GetMapping("/list/search-content")
    public ResponseEntity<Page<CommunityDto.CommunityResponseDto>> searchCommunityByContent(@RequestParam(value = "content") String content,
                                                                                          @RequestParam(value = "page", defaultValue = "1")int page) {
        Page<CommunityDto.CommunityResponseDto> communityDtoPage= communityService.searchCommunityByContent(content,page);
        return new ResponseEntity<>(communityDtoPage, HttpStatus.OK);
    }

//    @Operation(summary = "게시물 수정", description = "id를 입력하세요, 로그인 한 사용자의 게시물이 아니면  수정이 되지않습니다.")
//    @PatchMapping("/{id}")
//    public ResponseEntity<?> updateCommunity(@PathVariable Long id, @RequestBody CommunityDto.CommunityRequestDto communityRequestDto, HttpServletRequest request) {
//        User user = (User) request.getSession().getAttribute("user");
//        CommunityDto.CommunityResponseDto updatedCommunity = communityService.updateCommunity(id, communityRequestDto, user);
//        return ResponseEntity.ok(updatedCommunity);
//    }

    @Operation(summary = "게시물 수정", description = "id와 로그인 한 사용자의 게시물이 아니면 수정이 되지 않습니다.")
    @PostMapping(path = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommunityDto.CommunityResponseDto> update(@PathVariable Long id,
                                                                    @RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                    @RequestParam(name = "title") String title,
                                                                    @RequestParam(name = "content") String content,
                                                                    HttpServletRequest request) {
        CommunityDto.CommunityRequestDto communityRequestDto = CommunityDto.CommunityRequestDto.builder()
                .title(title)
                .content(content)
                .build();

        User user = (User) request.getSession().getAttribute("user");
        CommunityDto.CommunityResponseDto updatedCommunity = communityService.updateCommunity(id, communityRequestDto, files, user);
        return new ResponseEntity<>(updatedCommunity, HttpStatus.OK);
    }

    @Operation(summary = "게시물 삭제", description = "id를 입력하세요, 로그인 한 사용자의 게시물이 아니면 삭제가 되지않습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommunity(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        communityService.deleteCommunity(id, user);
        return ResponseEntity.ok().build();
    }
}