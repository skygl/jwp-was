package webserver;

import http.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static Map<String, HttpSession> sessionMap = new HashMap<>();

    public static HttpSession createEmptySession() {
        return new HttpSession();
    }

    public static String addSession(HttpSession session) {
        sessionMap.put(session.getId(), session);
        return session.getId();
    }

    public static HttpSession findSession(String key) {
        return sessionMap.get(key);
    }

    public static void deleteSession(String key) {
        sessionMap.remove(key);
    }
}
