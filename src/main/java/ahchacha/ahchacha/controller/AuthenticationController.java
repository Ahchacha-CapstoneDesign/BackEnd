package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.dto.AuthenticationDto;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "OFFICIAL 인증 사진 제출")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AuthenticationDto.AuthenticationResponseDto> create(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                    HttpSession session){
        AuthenticationDto.AuthenticationResponseDto authenticationResponseDto = authenticationService.createAuthentication(files, session);
        return new ResponseEntity<>(authenticationResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "제출된 인증사진 리스트")
    @GetMapping("/allAuthentication")
    public ResponseEntity<Page<AuthenticationDto.AuthenticationResponseDto>> getAllAuthentication(@RequestParam(value = "page", defaultValue = "1")int page, HttpSession session) throws IllegalAccessException {
        Page<AuthenticationDto.AuthenticationResponseDto> authenticationResponseDtoPage = authenticationService.getAllAuthentication(page, session);
        return new ResponseEntity<>(authenticationResponseDtoPage, HttpStatus.OK);
    }
}
