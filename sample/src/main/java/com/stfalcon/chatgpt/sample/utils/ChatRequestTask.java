package com.stfalcon.chatgpt.sample.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.stfalcon.chatgpt.sample.common.data.fixtures.MessagesFixtures;
import com.stfalcon.chatgpt.sample.common.data.manager.MessageManager;
import com.stfalcon.chatgpt.sample.common.data.model.Message;
import com.stfalcon.chatgpt.sample.features.demo.DemoMessagesActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import okio.BufferedSource;
import okio.Okio;

public class ChatRequestTask extends AsyncTask<String, String, String> {

    private static final String TAG = "ChatRequestTask";
    private static final String OPENAI_API_KEY = "sk-xxxxxxxx";

//    private static final String API_ENDPOINT = "https://api.chatanywhere.com.cn/v1/chat/completions";
    private static final String API_ENDPOINT = "https://api.chatanywhere.tech/v1/chat/completions";

    public DemoMessagesActivity messagesActivity;

    private MessageManager messageManager = MessageManager.getmessageManagerInstance();

    private int pre_len = "data: ".length();

    public volatile static boolean isRunning = false;

    public Message currentMessage;


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
            messageManager.clear();
            jsonRequest.put("stream", true);

            String jsonRequestString = jsonRequest.toString();
            Log.d(TAG, "doInBackground: jsonRequestString: " + jsonRequestString);

            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(jsonRequestString.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // 给流式输出用的
                this.currentMessage =  MessagesFixtures.getTextMessage("", 1);

                messagesActivity.runOnUiThread(() -> messagesActivity.messagesAdapter.addToStart(this.currentMessage, true));

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // 发布进度以更新 UI
                    publishProgress(line);
                }
                reader.close();
            } else {
                publishProgress("Error: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: " + e.getMessage(), e);
            publishProgress("Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        try {
            if (values != null && values.length > 0) {
                String line = values[0];
                line = line.trim();
                if(line.isEmpty() || (line.length() < pre_len)){
                    return;
                }
                line = line.substring(pre_len);
                if("[DONE]".equals(line)){
                    return;
                }
                JSONObject jsonResult = new JSONObject(line);
                JSONObject delta = jsonResult.getJSONArray("choices").getJSONObject(0).getJSONObject("delta");
                Log.d(TAG, "onProgressUpdate: delta: " + delta);
                if(delta.length() == 0){
                    return;
                }
                String content = delta.getString("content").trim();
                if(content.isEmpty()){
                    return;
                }
                Thread.sleep(1);
//                messagesActivity.runOnUiThread(() -> messagesActivity.messagesAdapter.streamPrint(content, true));
                content = this.currentMessage.getText() + content;
                this.currentMessage.setText(content);

                messagesActivity.runOnUiThread(() -> messagesActivity.messagesAdapter.update(this.currentMessage));
            }
        } catch (Exception e) {
            Log.e(TAG, "onPostExecute: " + e.getMessage());
            e.printStackTrace();
        }

            // 在这里处理每一行数据，例如将其显示在 UI 上
            // 你可以添加适当的逻辑来处理每一行数据

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}

// To execute the task with a message:
// new ChatRequestTask().execute("你好");

