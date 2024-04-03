package ahchacha.ahchacha.service;

import ahchacha.ahchacha.aws.AmazonS3Manager;
import ahchacha.ahchacha.domain.*;
import ahchacha.ahchacha.dto.CommentDto;
import ahchacha.ahchacha.dto.CommunityDto;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.repository.CommunityRepository;
import ahchacha.ahchacha.repository.UuidRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Transactional
    public CommunityDto.CommunityResponseDto createBoard(CommunityDto.CommunityRequestDto communityDto,
                                                         List<MultipartFile> files,
                                                         HttpSession session) {
        User user = (User) session.getAttribute("user");

        List<String> pictureUrls = new ArrayList<>();
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

        Community community = Community.builder()
                .user(user)
                .title(communityDto.getTitle())
                .content(communityDto.getContent())
                .imageUrls(pictureUrls)
                .build();

        Community createdBoard = communityRepository.save(community);
        return CommunityDto.CommunityResponseDto.toDto(createdBoard);
    }

    public Page<CommunityDto.CommunityResponseDto> getAllCommunity(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); //최근 작성순

        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(sorts)); //한페이지에 10개
        Page<Community> communityPage = communityRepository.findAll(pageable);
        return CommunityDto.toDtoPage(communityPage);

    }

    public Page<CommunityDto.CommunityResponseDto> getAllCommunityByViewCounts(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("viewCount"));
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1,10,Sort.by(sorts));
        Page<Community> communityPage = communityRepository.findAll(pageable);
        return CommunityDto.toDtoPage(communityPage);
    }

    public Page<CommunityDto.CommunityResponseDto> getAllCommunityByLikeCounts(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("likeCount"));
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1,10,Sort.by(sorts));
        Page<Community> communityPage = communityRepository.findAll(pageable);
        return CommunityDto.toDtoPage(communityPage);
    }

    public Page<CommunityDto.CommunityResponseDto> searchCommunityByTitle(String title,int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1,10,Sort.by(sorts));
        Page<Community> communityPage = communityRepository.findByTitleContaining(title, pageable);
        return CommunityDto.toDtoPage(communityPage);
    }

    public Page<CommunityDto.CommunityResponseDto> searchCommunityByContent(String content,int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1,10,Sort.by(sorts));
        Page<Community> communityPage = communityRepository.findByContentContaining(content, pageable);
        return CommunityDto.toDtoPage(communityPage);
    }

    @Transactional
    public CommunityDto.CommunityResponseDto updateCommunity(Long id, CommunityDto.CommunityRequestDto communityDto){
        Community community = communityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        community.updateCommunity(communityDto.getTitle(), communityDto.getContent(), communityDto.getPictureUrls());
        return CommunityDto.CommunityResponseDto.toDto(community);
    }

    @Transactional
    public void deleteCommunity(Long id){
        Community community = communityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        communityRepository.delete(community);
    }

}
