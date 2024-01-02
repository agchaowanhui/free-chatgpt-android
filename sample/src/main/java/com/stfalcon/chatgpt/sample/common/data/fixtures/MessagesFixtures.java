package com.stfalcon.chatgpt.sample.common.data.fixtures;

import com.stfalcon.chatgpt.sample.common.data.model.Message;
import com.stfalcon.chatgpt.sample.common.data.model.User;

/*
 * Created by troy379 on 12.12.16.
 */
public final class MessagesFixtures extends FixturesData {
    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static Message getTextMessage(String text, int user_id) {
        return new Message(String.valueOf(user_id), getUser(user_id), text);
    }

    private static User getUser(int user_id) {
        return new User(String.valueOf(user_id), names.get(user_id), avatars.get(user_id), false);
    }
}
