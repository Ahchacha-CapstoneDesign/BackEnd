package ahchacha.ahchacha.service;


import ahchacha.ahchacha.aws.AmazonS3Manager;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.Uuid;
import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
import ahchacha.ahchacha.dto.UserDto;
import ahchacha.ahchacha.repository.UserRepository;
import ahchacha.ahchacha.repository.UuidRepository;
import ahchacha.ahchacha.service.common.ConnectionResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.jsoup.Connection;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User login(UserDto.LoginRequestDto loginRequestDto, HttpSession session) throws IOException {

        long stuId = Long.parseLong(loginRequestDto.getId());

        // 종정시 로그인
        ResponseEntity<String> jjsResponse = getJjsResponse(loginRequestDto);
        Document jjsDocument = Jsoup.parse(Objects.requireNonNull(jjsResponse.getBody()));
        // redirectUrl 추출
        String redirectUrl = Objects.requireNonNull(jjsDocument.body().select("a").first()).attr("href");
        if (redirectUrl.equals("null")) {
            throw new RuntimeException("error");
        }

        // 비교과 포인트 로그인
        ResponseEntity<String> hsportalResponse = getHsportalResponse(loginRequestDto);
        Document hsportalDocument = Jsoup.parse(Objects.requireNonNull(hsportalResponse.getBody()));


        // response의 "success" 값 추출
        Element body = hsportalDocument.body();
        String jsonString = body.getAllElements().text();
        JSONObject jsonObject = new JSONObject(jsonString);
        boolean success = jsonObject.getBoolean("success");

        // 세션에 쿠키 설정
        setCookieToSession(session, jjsResponse, hsportalResponse);

        Map<String, String> cookies = new HashMap<>();
        cookies.putAll(getCookies(jjsResponse));
        cookies.putAll(getCookies(hsportalResponse));
        session.setAttribute("cookies", cookies);

        // 학생정보 생성
        User user;

        // 학생 정보가 존재하면 해당 학생 정보를 가져옴
        Optional<User> optionalStudent = userRepository.findById(stuId);
        if (optionalStudent.isPresent()) {
            user = optionalStudent.get();
        } else {
            // 학번, 1트랙, 2트랙으로 학생 정보 만들기
            user = userRepository.save(getNewStudent(session, stuId));

        }

        log.info("Saving user info in session. User ID: {}", user.getId());

        session.setAttribute("user", user);

// 로그인 성공 로그 출력
        log.info("Login successful. User ID: {}", user.getId());

        if (redirectUrl.equals("http://info.hansung.ac.kr/h_dae/dae_main.html") && success) {
            return user;
        } else {
            throw new RuntimeException("error!");
        }

    }
    private static ResponseEntity<String> getJjsResponse(UserDto.LoginRequestDto loginRequestDto) {
        String url = "https://info.hansung.ac.kr/servlet/s_gong.gong_login_ssl"; // 종정시 로그인

        // Form Data 생성
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id", loginRequestDto.getId());
        map.add("passwd", loginRequestDto.getPasswd());

        return httpResponse(map, url);
    }

    private static ResponseEntity<String> getHsportalResponse(UserDto.LoginRequestDto loginRequestDto) {
        String url = "https://hsportal.hansung.ac.kr/ko/process/member/login";  // 비교과 포인트 로그인

        // Form Data 생성
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", loginRequestDto.getId());
        map.add("password", loginRequestDto.getPasswd());

        return httpResponse(map, url);
    }

    private static ResponseEntity<String> httpResponse(MultiValueMap<String, String> map, String url) {
        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // http 요청하기
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    private Map<String, String> getCookies(ResponseEntity<String> response) {
        // cookie 추출
        return Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).stream()
                .map(cookieStr -> {
                    String[] cookieArr = cookieStr.split(";\\s*");
                    String[] nameValue = cookieArr[0].split("=");
                    return new AbstractMap.SimpleEntry<>(nameValue[0], nameValue[1]);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    private void setCookieToSession(HttpSession session, ResponseEntity<String> jjsResponse, ResponseEntity<String> hsportalResponse) {
        Map<String, String> cookies = new HashMap<>();
        cookies.putAll(getCookies(jjsResponse));
        cookies.putAll(getCookies(hsportalResponse));
        session.setAttribute("cookies", cookies);
    }

    private static User getNewStudent(HttpSession session, long stuId) throws IOException {
        String jjsUrl = "https://info.hansung.ac.kr/jsp_21/student/hakjuk/collage_register_1_rwd.jsp";
        Connection.Response response = ConnectionResponse.getResponse(session, jjsUrl);

        String jjsUrl2 = "https://info.hansung.ac.kr/jsp_21/student/hakjuk/haksang_info2_h_rwd.jsp";
        Connection.Response response2 = ConnectionResponse.getResponse(session, jjsUrl2);

        String gradesUrl = "https://info.hansung.ac.kr/jsp_21/student/grade/total_grade.jsp";
        Connection.Response gradesResponse = ConnectionResponse.getResponse(session, gradesUrl);

        Document doc = response.parse();
        Document doc2 = response2.parse();
        Document gradeDoc = gradesResponse.parse();

        Element link = doc.select("a.d-block").first();
        String text = link.html(); // "모바일소프트웨어트랙<br> 웹공학트랙<br> 김동욱"
        String[] split = text.split("<br>");
        String track1 = split[0].trim(); // "모바일소프트웨어트랙"
        String track2 = split[1].trim(); // "웹공학트랙"
        String name = split[2].trim(); // 김동욱

        String phoneNumberSelector = "#menu183_obj498 > ul > li:nth-child(2) > table > tbody > tr > td > input[type=text]";
        Element inputElement = doc2.select(phoneNumberSelector).first();
        String phoneNumber = "";
        if (inputElement != null) {
            phoneNumber = inputElement.attr("value").trim();
        }

        String gradeText = Objects.requireNonNull(gradeDoc.select("strong.objHeading_h3").first()).text(); // 김동욱 (1971047) 컴퓨터공학부 4 학년 재학
        // 괄호를 제거하고, 공백을 기준으로 문자열 분리
        String[] parts = gradeText.replace("(", "").replace(")", "").split(" ");
        String department = parts[2]; //컴퓨터공학부
        String grade = parts[3] + parts[4]; //4학년
        String status = parts[5]; //복학


        return User.builder()
                .id(stuId)
                .name(name)
                .personOrOfficial(PersonOrOfficial.PERSON)
                .track1(track1)
                .track2(track2)
                .phoneNumber(phoneNumber)
                .grade(grade)
                .status(status)
                .build();
    }

    public String logout(HttpSession session) throws IOException {

        // 세션 유효성 검사
        if (session == null || session.getAttribute("cookies") == null) {
            throw new RuntimeException("error!");
        }

        String url = "https://info.hansung.ac.kr/sso_logout.jsp";

        @SuppressWarnings(value = "unchecked")
        Map<String, String> cookies = (Map<String, String>) session.getAttribute("cookies");

        Jsoup.connect(url)
                .cookies(Objects.requireNonNull(cookies))
                .method(Connection.Method.GET)
                .execute();

        // 세션 삭제
        session.invalidate();

        return "logout success";
    }

    public void setNickname(String nickname, HttpSession session) {
        // 세션에서 사용자 정보를 가져옴
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 사용자가 로그인하지 않았을 경우 에러 처리
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Optional<User> existingUser = userRepository.findByNickname(nickname);
        if(existingUser.isPresent() && !existingUser.get().getId().equals(user.getId()))
            throw new IllegalStateException("이미 사용중인 닉네임 입니다.");
        // 닉네임 변경
        user.setNickname(nickname);
        // 변경된 사용자 정보를 데이터베이스에 저장
        userRepository.save(user);
    }

    public void setUserType(PersonOrOfficial userType, HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        user.setPersonOrOfficial(userType);
        userRepository.save(user);
    }

    public boolean validateCurrentPassword(String password, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        // 로그인 요청 DTO 생성
        UserDto.LoginRequestDto loginRequestDto = new UserDto.LoginRequestDto();
        loginRequestDto.setId(user.getId().toString());
        loginRequestDto.setPasswd(password);
        try {
            ResponseEntity<String> hsportalResponse = getHsportalResponse(loginRequestDto);
            Document hsportalDocument = Jsoup.parse(Objects.requireNonNull(hsportalResponse.getBody()));
            // response의 "success" 값 추출
            Element body = hsportalDocument.body();
            String jsonString = body.getAllElements().text();
            JSONObject jsonObject = new JSONObject(jsonString);
            boolean success = jsonObject.getBoolean("success");
            // 비밀번호 검증 성공 여부 반환
            return success;
        } catch (Exception e) {
            throw new IllegalStateException("비밀번호가 올바르지 않습니다.");
        }
    }

    public String saveProfile(MultipartFile file, HttpSession session, String category) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 이미지 업로드
        String pictureUrl = null;
        if (file != null){
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());
            pictureUrl = s3Manager.uploadFile(s3Manager.generateProfileKeyName(savedUuid), file);
        }

        System.out.println("s3 url(클릭 시 브라우저에 사진 뜨는지 확인): " + pictureUrl);

        // 기존 이미지가 존재하면 S3에서 삭제 후 새 이미지 저장
        if (category.equals("defaultProfile")) {
            String oldProfileUrl = user.getDefaultProfile();
            if (oldProfileUrl != null) {
                s3Manager.deleteFile(oldProfileUrl);
            }
            user.setDefaultProfile(pictureUrl);
        }

        userRepository.save(user);

        return pictureUrl;
    }

    public String getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        return user.getDefaultProfile();
    }
}