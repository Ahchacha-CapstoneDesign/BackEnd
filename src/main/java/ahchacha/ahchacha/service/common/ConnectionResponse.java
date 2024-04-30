package ahchacha.ahchacha.service.common;

import jakarta.servlet.http.HttpSession;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class ConnectionResponse {

    public static Connection.Response getResponse(HttpSession session, String url) throws IOException {

        @SuppressWarnings(value = "unchecked")
        Map<String, String> cookies = (Map<String, String>) session.getAttribute("cookies");

        return Jsoup.connect(url)
                .cookies(cookies)
                .method(Connection.Method.GET)
                .execute();

    }

}
