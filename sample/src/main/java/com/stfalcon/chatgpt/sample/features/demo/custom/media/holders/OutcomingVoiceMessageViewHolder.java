package com.stfalcon.chatgpt.sample.features.demo.custom.media.holders;

import android.view.View;
import android.widget.TextView;

import com.stfalcon.chatgpt.messages.MessageHolders;
import com.stfalcon.chatgpt.sample.R;
import com.stfalcon.chatgpt.sample.common.data.model.Message;
import com.stfalcon.chatgpt.sample.utils.FormatUtils;
import com.stfalcon.chatgpt.utils.DateFormatter;

/*
 * Created by troy379 on 05.04.17.
 */
public class OutcomingVoiceMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView tvDuration;
    private TextView tvTime;

    public OutcomingVoiceMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvDuration = itemView.findViewById(R.id.duration);
        tvTime = itemView.findViewById(R.id.time);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        tvDuration.setText(
                FormatUtils.getDurationString(
                        message.getVoice().getDuration()));
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
    }
}
