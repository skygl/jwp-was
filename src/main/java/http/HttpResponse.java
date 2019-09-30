package http;

import http.exception.EmptyStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.AbstractHttpException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String HTTP_NEW_LINE = "\r\n";

    private HttpStatus status;
    private HttpHeader header = new HttpHeader();
    private byte[] body;

    public HttpResponse() {
    }

    public void addHeader(String key, String value) {
        header.addHeader(key, value);
    }

    private String makeHeaderLines() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : header.getHeaders()) {
            sb.append(header.getKey() + HEADER_DELIMITER + header.getValue() + HTTP_NEW_LINE);
        }
        sb.append(HTTP_NEW_LINE);
        return sb.toString();
    }

    public void send(DataOutputStream dos) {
        if (status == null) {
            throw new EmptyStatusException();
        }
        try {
            dos.writeBytes(HTTP_VERSION + status.getStatusCode() + " " + status.getStatus() + HTTP_NEW_LINE);
            dos.writeBytes(makeHeaderLines());
            if (body != null) {
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendError(AbstractHttpException e) {
        status = e.getStatus();
    }

    public void addCookie(Cookie cookie) {
        header.addCookie(cookie);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeader(String key) {
        return header.getValue(key);
    }

    public byte[] getBody() {
        return body;
    }

    public void setStatus(int statusCode) {
        status = HttpStatus.of(statusCode);
    }

    public void setBody(byte[] body) {
        this.body = body;
        header.addHeader(CONTENT_LENGTH, String.valueOf(body.length));
    }
}