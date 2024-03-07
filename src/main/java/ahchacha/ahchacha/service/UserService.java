package ahchacha.ahchacha.service;


import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.dto.UserDto;
import ahchacha.ahchacha.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
        String jjsUrl = "https://info.hansung.ac.kr/jsp_21/index.jsp";
        Connection.Response response = ConnectionResponse.getResponse(session, jjsUrl);

        Document doc = response.parse();
        Element link = doc.select("a.d-block").first();

        String text = link.html(); // "모바일소프트웨어트랙<br> 웹공학트랙<br> 황준현"
        String[] split = text.split("<br>");

        String track1 = split[0].trim(); // "모바일소프트웨어트랙"
        String track2 = split[1].trim(); // "웹공학트랙"

        return User.builder()
                .id(stuId)
                .track1(track1)
                .track2(track2)
                .build();
    }

}
