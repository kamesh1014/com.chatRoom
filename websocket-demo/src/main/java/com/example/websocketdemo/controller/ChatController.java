package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Controller
public class ChatController {
	@Autowired	    
	private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/{roomId}/chat.sendMessage")
   // @SendTo(format("/chat-room/%s", roomId))
    public ChatMessage sendMessage(@DestinationVariable String roomId,@Payload ChatMessage chatMessage,
    		SimpMessageHeaderAccessor headerAccessor) {
    	 System.out.println(roomId);
    	 
    	// messagingTemplate.convertAndSend(format("/chat-room/%s", roomId), chatMessage);
    	 String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
 		System.out.println("from chat:"+sessionId);
 		headerAccessor.setSessionId(sessionId);
    	 messagingTemplate.convertAndSend(format("/chat-room/%s", roomId), chatMessage);
    	 return chatMessage;
    }

   

	@MessageMapping("/chat/{roomId}/chat.addUser")
  //  @SendTo("/topic/public")
    public ChatMessage addUser(@DestinationVariable String roomId,@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        /*headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;*/
		String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
	//	String sessionId = headerAccessor.getSessionId();
		System.out.println("duringg add user:"+sessionId);
		headerAccessor.setSessionId(sessionId);
		System.out.println("duringg add user:"+chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("name", chatMessage.getSender());
		
		System.out.println("durinf add use name :"+headerAccessor.getSessionAttributes().get("name"));
		
		 String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
	        if (currentRoomId != null) {
	        	ChatMessage leaveMessage = new ChatMessage();
	            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
	            leaveMessage.setSender(chatMessage.getSender());
	            messagingTemplate.convertAndSend(format("/chat-room/%s", currentRoomId), leaveMessage);
	        }
	    //    headerAccessor.getSessionAttributes().put("name", chatMessage.getSender());
	        messagingTemplate.convertAndSend(format("/chat-room/%s", roomId), chatMessage);
			return chatMessage;
    }

}
