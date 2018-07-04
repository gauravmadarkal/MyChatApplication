package com.example.gauravm.chatapp;

import android.provider.BaseColumns;
import android.widget.Toast;

public final class FeedReaderContract extends chats {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    int i=0;

    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {


        public static final String TABLE_NAME = "MyMessages";
        public static final String Messages = "messages";
        public static final String isMine  = "isMine";
        public static final String Receiver = "receiver";

    }
}