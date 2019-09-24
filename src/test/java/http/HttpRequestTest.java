package http;

import http.exception.NotFoundMethodException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.IOUtils.convertStringToInputStream;

public class HttpRequestTest {
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String GET_REQUEST =
            "GET /index.html HTTP/1.1\n" +
                    "Host: localhost:8080\n" +
                    "Connection: keep-alive\n" +
                    "Cache-Control: max-age=0\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36\n" +
                    "Sec-Fetch-Mode: navigate\n" +
                    "Sec-Fetch-User: ?1\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,/;q=0.8,application/signed-exchange;v=b3\n" +
                    "Sec-Fetch-Site: none\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                    "Cookie: Idea-c5a8acf3=c2c6d2e2-54d7-47a9-8e2a-b0ff8e06a759; JSESSIONID=55F183C31FC99489F6D47D0794FD685F\n";

    public static final String POST_REQUEST =
            "POST /user/create HTTP/1.1\n" +
                    "Host: localhost:8080\n" +
                    "Connection: keep-alive\n" +
                    "Content-Length: 61\n" +
                    "Pragma: no-cache\n" +
                    "Cache-Control: no-cache\n" +
                    "Origin: http://localhost:8080\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "Content-Type: application/x-www-form-urlencoded\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36\n" +
                    "Sec-Fetch-Mode: navigate\n" +
                    "Sec-Fetch-User: ?1\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                    "Sec-Fetch-Site: same-origin\n" +
                    "Referer: http://localhost:8080/user/form.html\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                    "Cookie: Idea-c5a8acf3=c2c6d2e2-54d7-47a9-8e2a-b0ff8e06a759; JSESSIONID=55F183C31FC99489F6D47D0794FD685F\n" +
                    "\n" +
                    "userId=park&password=1234&name=sungbum&email=park%40naver.com";

    public static final String LOGIN_REQUEST =
            "POST /user/login HTTP/1.1\n" +
                    "Host: localhost:8080\n" +
                    "Connection: keep-alive\n" +
                    "Content-Length: 27\n" +
                    "Pragma: no-cache\n" +
                    "Cache-Control: no-cache\n" +
                    "Origin: http://localhost:8080\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "Content-Type: application/x-www-form-urlencoded\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36\n" +
                    "Sec-Fetch-Mode: navigate\n" +
                    "Sec-Fetch-User: ?1\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                    "Sec-Fetch-Site: same-origin\n" +
                    "Referer: http://localhost:8080/user/login.html\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                    "Cookie: JSESSIONID=9958D573F2F86368E8D937C1439CB39C; Idea-daed433e=7b28f4e9-9efc-49f6-b857-2e1fd083330a\n" +
                    "\n" +
                    "userId=%s&password=%s\n";

    public static final String USER_LIST_REQUEST =
            "GET /user/list HTTP/1.1\n" +
                    "Host: localhost:8080\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:69.0) Gecko/20100101 Firefox/69.0\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                    "Accept-Language: ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3\n" +
                    "Accept-Encoding: gzip, deflate\n" +
                    "Connection: keep-alive\n" +
                    "Cookie: logined=%s\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "Pragma: no-cache\n" +
                    "Cache-Control: no-cache\n" +
                    "\n";

    HttpRequest request;

    @BeforeEach
    void setUp() throws IOException {
        request = HttpRequestParser.parse(new ByteArrayInputStream(GET_REQUEST.getBytes(UTF_8)));
    }

    @Test
    void 존재하지_않는_Method_생성_오류() throws IOException {
        String undefinedHeader = "NONE /index.html HTTP/1.1";

        assertThrows(NotFoundMethodException.class,
                () -> HttpRequestParser.parse(convertStringToInputStream(undefinedHeader)));
    }

    @Test
    void 헤더_URI_가져오기() {
        assertThat(request.getUri()).isEqualTo("/index.html");
    }

    @Test
    void 헤더_Request_Parameter_가져오기() throws IOException {
        String request = "GET /index.html?a=b&c=d HTTP/1.1";

        HttpRequest httpRequest = HttpRequestParser.parse(convertStringToInputStream(request));

        assertThat(httpRequest.getParameter("a")).isEqualTo("b");
        assertThat(httpRequest.getParameter("c")).isEqualTo("d");
    }

    @Test
    void 헤더_Method_가져오기() {
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 헤더_필드_가져오기() {
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getHeader("Cache-Control")).isEqualTo("max-age=0");
        assertThat(request.getHeader("Upgrade-Insecure-Requests")).isEqualTo("1");
        assertThat(request.getHeader("User-Agent")).isEqualTo("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
        assertThat(request.getHeader("Sec-Fetch-Mode")).isEqualTo("navigate");
        assertThat(request.getHeader("Sec-Fetch-User")).isEqualTo("?1");
        assertThat(request.getHeader("Accept")).isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,/;q=0.8,application/signed-exchange;v=b3");
        assertThat(request.getHeader("Sec-Fetch-Site")).isEqualTo("none");
        assertThat(request.getHeader("Accept-Encoding")).isEqualTo("gzip, deflate, br");
        assertThat(request.getHeader("Accept-Language")).isEqualTo("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        assertThat(request.getHeader("Cookie")).isEqualTo("Idea-c5a8acf3=c2c6d2e2-54d7-47a9-8e2a-b0ff8e06a759; JSESSIONID=55F183C31FC99489F6D47D0794FD685F");
    }

    @Test
    void 비어있는_바디_가져오기() {
        assertThat(request.getBody()).isEqualTo(null);
    }

    @Test
    void POST_요청_body_가져오기() throws IOException {
        HttpRequest postRequest = HttpRequestParser.parse(convertStringToInputStream(POST_REQUEST));

        assertThat(postRequest.getRequestBody("userId")).isEqualTo("park");
        assertThat(postRequest.getRequestBody("password")).isEqualTo("1234");
        assertThat(postRequest.getRequestBody("name")).isEqualTo("sungbum");
        assertThat(postRequest.getRequestBody("email")).isEqualTo("park@naver.com");
    }
}
