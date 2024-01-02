package com.stfalcon.chatgpt.sample.features.demo.custom.media;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.stfalcon.chatgpt.messages.MessageHolders;
import com.stfalcon.chatgpt.messages.MessageInput;
import com.stfalcon.chatgpt.messages.MessagesList;
import com.stfalcon.chatgpt.messages.MessagesListAdapter;
import com.stfalcon.chatgpt.sample.R;
import com.stfalcon.chatgpt.sample.common.data.fixtures.MessagesFixtures;
import com.stfalcon.chatgpt.sample.common.data.model.Message;
import com.stfalcon.chatgpt.sample.features.demo.DemoMessagesActivity;
import com.stfalcon.chatgpt.sample.features.demo.custom.media.holders.IncomingVoiceMessageViewHolder;
import com.stfalcon.chatgpt.sample.features.demo.custom.media.holders.OutcomingVoiceMessageViewHolder;
import com.stfalcon.chatgpt.sample.utils.ChatRequestTask;

public class CustomMediaMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageHolders.ContentChecker<Message>,
        DialogInterface.OnClickListener {

    private static final byte CONTENT_TYPE_VOICE = 1;

    private static final String TAG = "SQ_CustomMediaMessages";

    public static void open(Context context) {
        context.startActivity(new Intent(context, CustomMediaMessagesActivity.class));
    }

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_media_messages);

        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        // 获取到输入的字符串
        final String input_text = input.toString();
        Log.d(TAG, "onSubmit: " + input_text);

        Message message =  MessagesFixtures.getTextMessage(input_text, 0);

        super.messagesAdapter.addToStart(
                message, true
        );
        // 发送给chatgpt
        new ChatRequestTask(this).execute(input_text);

        return true;
    }

    @Override
    public void onAddAttachments() {
    }

    @Override
    public boolean hasContentFor(Message message, byte type) {
        return false;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    private void initAdapter() {
        MessageHolders holders = new MessageHolders()
                .registerContentType(
                        CONTENT_TYPE_VOICE,
                        IncomingVoiceMessageViewHolder.class,
                        R.layout.item_custom_incoming_voice_message,
                        OutcomingVoiceMessageViewHolder.class,
                        R.layout.item_custom_outcoming_voice_message,
                        this);


        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holders, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        this.messagesList.setAdapter(super.messagesAdapter);
    }
}
