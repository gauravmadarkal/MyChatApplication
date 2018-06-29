package com.example.gauravm.chatapp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


@SuppressLint("ValidFragment")
public class chats extends Fragment implements OnClickListener {

    private EditText msg_edittext;

    private Random random;
    public static ArrayList <ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    ViewPager viewPager;
    Firebase reference1, reference2;
    String sender="",receiver="";
    ClipboardManager cbm;
    ClipData data;
    int j=10;
    int count=0,n=0;
    ArrayList<String> arrayList;
    SQLiteDatabase db;
    String receiptRead="true",receiptNotRead="false";
   public static String user1="",user2="",keyvalue="";


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);
        Context context=view.getContext();
        boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        if(!connected){
            Toast.makeText(getContext(),"INTERNET CONNECTION NOT AVAILABLE",Toast.LENGTH_LONG).show();
        }
        random = new Random();

       int i= UserDetails.username.compareTo(UserDetails.chatWith);
        if(i>0){
            user1= UserDetails.chatWith;
            user2= UserDetails.username;
        }
        else{
            user1= UserDetails.username;
            user2= UserDetails.chatWith;
        }
        final String tablename=user1+"_"+user2;
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        db = mDbHelper.getWritableDatabase();

        reference1 = new Firebase("https://mychat-641b5.firebaseio.com/" + user1 + "_" + user2);

        reference2 = new Firebase("https://mychat-641b5.firebaseio.com/" + user1 + "_" + user2);
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton) view
                .findViewById(R.id.sendMessageButton);
        FloatingActionButton refreshbutton=(FloatingActionButton)view.findViewById(R.id.fab);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg_edittext.getEditableText().toString();

                if(!message.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", message);
                    map.put("user", UserDetails.username);
                    map.put("read",receiptNotRead);
                    reference1.push().setValue(map);



                }
            }
        });

        arrayList=new ArrayList<String>();

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                arrayList.add(dataSnapshot.getKey());
                count=arrayList.size();
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                if(userName.equals(UserDetails.username)) {
                    sender = UserDetails.username;
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.Messages, message);
                    values.put(FeedReaderContract.FeedEntry.isMine, 1);
                    values.put(FeedReaderContract.FeedEntry.Receiver,UserDetails.chatWith);
                    long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                }
                else {
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.Messages, message);
                    values.put(FeedReaderContract.FeedEntry.isMine, 0);
                    values.put(FeedReaderContract.FeedEntry.Receiver,UserDetails.chatWith);

                    long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        reference1.limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
              keyvalue=dataSnapshot.getKey();

                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String status = map.get("read").toString();
                if(userName.equals(UserDetails.username)){
                    sender=UserDetails.username;

                    final ChatMessage chatMessage = new ChatMessage(UserDetails.username, UserDetails.chatWith,
                            message, "" + random.nextInt(1000), true);
                    chatMessage.setMsgID();
                    chatMessage.body = message;
                    msg_edittext.setText("");
                    chatAdapter.add(chatMessage);
                    chatMessage.Date = CommonMethods.getCurrentDate();
                    chatMessage.Time = CommonMethods.getCurrentTime();
                    chatAdapter.notifyDataSetChanged();


                }
                else{

                    reference2.child(keyvalue).child("read").setValue(true);
                    final ChatMessage chatMessage = new ChatMessage( UserDetails.chatWith,sender,
                            message, "" + random.nextInt(1000), false);
                    chatMessage.setMsgID();
                    chatMessage.body = message;
                    msg_edittext.setText("");
                    chatAdapter.add(chatMessage);
                    chatMessage.Date = CommonMethods.getCurrentDate();
                    chatMessage.Time = CommonMethods.getCurrentTime();
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });



        refreshbutton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                chatAdapter.remove();
               if(j<count){
                j=j+2;

                   reference1.limitToLast(j).addChildEventListener(new ChildEventListener() {
                       @Override
                       public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                           Map map = dataSnapshot.getValue(Map.class);
                           String message = map.get("message").toString();
                           String userName = map.get("user").toString();

                           if(userName.equals(UserDetails.username)){
                               sender=UserDetails.username;

                               final ChatMessage chatMessage = new ChatMessage(UserDetails.username, UserDetails.chatWith,
                                       message, "" + random.nextInt(1000), true);
                               chatMessage.setMsgID();
                               chatMessage.body = message;
                               msg_edittext.setText("");
                               chatAdapter.add(chatMessage);
                               chatMessage.Date = CommonMethods.getCurrentDate();
                               chatMessage.Time = CommonMethods.getCurrentTime();
                               chatAdapter.notifyDataSetChanged();
                                n=n+1;

                           }
                           else{
                               final ChatMessage chatMessage = new ChatMessage( UserDetails.chatWith,sender,
                                       message, "" + random.nextInt(1000), false);
                               chatMessage.setMsgID();
                               chatMessage.body = message;
                               msg_edittext.setText("");
                               chatAdapter.add(chatMessage);
                               chatMessage.Date = CommonMethods.getCurrentDate();
                               chatMessage.Time = CommonMethods.getCurrentTime();
                               chatAdapter.notifyDataSetChanged();
                               n=n+1;
                           }
                       }

                       @Override
                       public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                       }
                       @Override
                       public void onChildRemoved(DataSnapshot dataSnapshot) {
                       }
                       @Override
                       public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                       }
                       @Override
                       public void onCancelled(FirebaseError firebaseError) {
                       }
                   });

               }
                else{
                   Toast.makeText(getContext(),"No More Messages",Toast.LENGTH_LONG).show();
               }
            }
        });





        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);


        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(getActivity(), chatlist);

        msgListView.setAdapter(chatAdapter);
        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,int pos,long l) {
                Context context=view.getContext();



                Firebase status = reference2.child(arrayList.get(pos)).child("read");

                status.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String readstatus = dataSnapshot.getValue(String.class);
                        if(readstatus.equals("true")) {
                            Toast.makeText(getContext(),"message seen", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(),"message not seen", Toast.LENGTH_SHORT).show();
                        }

                    }


                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }


                });

                return false;
            }
        });



        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onClick(View view) {

    }
}

