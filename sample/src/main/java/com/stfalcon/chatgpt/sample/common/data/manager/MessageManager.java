package com.stfalcon.chatgpt.sample.common.data.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private static MessageManager messageManager;
    public final static List<ChatgptMessage> messageHistory = new ArrayList<>();

    private MessageManager(){}

    public static MessageManager getmessageManagerInstance(){
        if(messageManager == null){
            messageManager = new MessageManager();
        }
        return messageManager;
    }

    public void addChatgptMessage(String user, String content){
        messageHistory.add(new ChatgptMessage("assistant", content));
    }

    public JSONArray buildJson() throws JSONException {
        JSONArray messagesArray = new JSONArray();
        for (ChatgptMessage msg : messageHistory) {
            JSONObject message = new JSONObject();
            message.put("role", msg.getRole());
            message.put("content", msg.getContent());
            messagesArray.put(message);
        }
        return messagesArray;
    }

    public void clear(){
        messageHistory.clear();
    }

    // 内部类定义消息结构
    public static class ChatgptMessage {
        private String role;
        private String content;

        public ChatgptMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
