package com.stfalcon.chatgpt.sample.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.stfalcon.chatgpt.sample.common.data.fixtures.MessagesFixtures;
import com.stfalcon.chatgpt.sample.common.data.manager.MessageManager;
import com.stfalcon.chatgpt.sample.common.data.model.Message;
import com.stfalcon.chatgpt.sample.features.demo.DemoMessagesActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import okio.BufferedSource;
import okio.Okio;

public class ChatRequestTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "ChatRequestTask";
    private static final String OPENAI_API_KEY = "sk-RpGW9F2sK5zRZFLYyRJ8xnQrepPmpUcnNl16qcIjQgZSkAkP";

//    private static final String API_ENDPOINT = "https://api.chatanywhere.com.cn/v1/chat/completions";
    private static final String API_ENDPOINT = "https://api.chatanywhere.tech/v1/chat/completions";

    public DemoMessagesActivity messagesActivity;

    private MessageManager messageManager = MessageManager.getmessageManagerInstance();


    public ChatRequestTask(DemoMessagesActivity demoMessagesActivity){
        messagesActivity = demoMessagesActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            if (params.length == 0) {
                return "No message content provided.";
            }

            String messageContent = params[0];
            URL url = new URL(API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            connection.setRequestProperty("User-Agent", "Apifox/1.0.0 (https://apifox.com)");

            // 将新的用户消息添加到历史消息列表
            messageManager.addChatgptMessage("user", messageContent);

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("model", "gpt-3.5-turbo");

            jsonRequest.put("messages", messageManager.buildJson());

            String jsonRequestString = jsonRequest.toString();
            Log.d(TAG, "doInBackground: jsonRequestString: " + jsonRequestString);

            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(jsonRequestString.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedSource source = Okio.buffer(Okio.source(connection.getInputStream()));
                String responseBody = source.readUtf8();
                source.close();

                return responseBody;
            } else {
                return "Error: " + responseCode;
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            Log.d(TAG, "onPostExecute: result: " + result);
            JSONObject jsonResult = new JSONObject(result);
            String content = jsonResult.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            messageManager.addChatgptMessage("assistant", content);
            Message message =  MessagesFixtures.getTextMessage(content, 1);
            messagesActivity.runOnUiThread(() -> {
                messagesActivity.messagesAdapter.addToStart(message, true);
            });
        } catch (Exception e) {
            Log.e(TAG, "onPostExecute: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// To execute the task with a message:
// new ChatRequestTask().execute("你好");

