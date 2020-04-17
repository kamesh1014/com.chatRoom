package com.example.websocketdemo.controller;

import com.example.websocketdemo.config.SubscribeEventListener;
import com.example.websocketdemo.model.ChatMessage;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Created by rajeevkumarsingh on 25/07/17.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    	System.out.println("Received a new web socket connection");

    	StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
       // System.out.println(headerAccessor.getSessionAttributes().get("sessionId").toString());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        
        System.out.println("handleWebSocketDisconnectListener");
		System.out.println(headerAccessor.getSessionAttributes().get("sessionId").toString());
        
        String username = (String) headerAccessor.getSessionAttributes().get("name");
        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
        
        System.out.println(username+roomId);
        
        
        
        if(username != null) {
            logger.info("User Disconnected : " + username);
            System.out.println("User Disconnected : " + username);
            
            
            		
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

          //  messagingTemplate.convertAndSend("/topic/public", chatMessage);
            messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
        }
        
    }
}
