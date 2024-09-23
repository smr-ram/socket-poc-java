package smaniks.dev.websocketdemo;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper;

    MyWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Convert JSON message to DTO
        MessageDTO receivedMessage = objectMapper.readValue(message.getPayload(), MessageDTO.class);
        System.out.println("Received: " + receivedMessage.getContent());

        // Send a response message back
        MessageDTO responseMessage = new MessageDTO("Message received: " + receivedMessage.getContent());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseMessage)));

        Thread.sleep(1000);
        responseMessage = new MessageDTO("Message received: 2 " + receivedMessage.getContent());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseMessage)));

        Thread.sleep(1000);
        responseMessage = new MessageDTO("Message received: 3 " + receivedMessage.getContent());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseMessage)));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred in session: " + session.getId() + " Error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }
}
