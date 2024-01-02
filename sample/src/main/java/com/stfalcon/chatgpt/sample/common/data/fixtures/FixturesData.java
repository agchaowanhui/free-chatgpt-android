package com.stfalcon.chatgpt.sample.common.data.fixtures;

import java.util.ArrayList;

/*
 * Created by Anton Bevza on 1/13/17.
 */
abstract class FixturesData {

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add(null);
            add(String.valueOf(com.stfalcon.chatgpt.R.drawable.ic_launcher));
        }
    };


    static final ArrayList<String> names = new ArrayList<String>() {
        {
            add("self");
            add("chatgpt");
        }
    };

}
