package org.complete.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SocketHandler socketHandler; // WebSocket 핸들러

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 채팅방 번호를 포함한 경로로 웹소켓 핸들러 등록
        registry.addHandler(socketHandler, "/ws/chat/{roomNumber}")
                .setAllowedOrigins("http://localhost:8080", "http://your-frontend-domain.com") // CORS 설정
                .addInterceptors(new HttpSessionHandshakeInterceptor(), new CustomHandshakeInterceptor())
                .withSockJS(); // SockJS 사용하여 폴백 옵션 추가
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(500_000);   // 텍스트 메시지 최대 크기
        container.setMaxBinaryMessageBufferSize(500_000); // 바이너리 메시지 최대 크기
        return container;
    }
}
