package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Community;
import ahchacha.ahchacha.domain.common.enums.Category;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import ahchacha.ahchacha.dto.CommentDto;
import ahchacha.ahchacha.dto.CommunityDto;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.service.CommunityService;
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

    @Operation(summary = "게시물 수정")
    @PatchMapping("/{id}")
    public CommunityDto.CommunityResponseDto updateCommunity(@PathVariable Long id, @RequestBody CommunityDto.CommunityRequestDto communityRequestDto){
        return communityService.updateCommunity(id, communityRequestDto);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long id){ communityService.deleteCommunity(id); }
}