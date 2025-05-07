package org.complete.websocket;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CustomHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            HttpSession session = req.getSession(false); // 기존 세션만 가져옴, 없으면 null

            if (session != null) {
                attributes.put("sessionID", session.getId());
                logger.info("세션 ID: {}", session.getId());
            } else {
                logger.warn("세션이 존재하지 않습니다.");
            }

            String userName = req.getParameter("userName");
            if (userName != null && !userName.isBlank()) {
                attributes.put("userName", userName);
                logger.info("사용자 이름: {}", userName);
            } else {
                // 기본값 설정
                attributes.put("userName", "anonymous");
                logger.info("사용자 이름이 비어있어 'anonymous'로 설정됨.");
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception ex
    ) {
        // 연결 후 후처리 로직
    }
}
