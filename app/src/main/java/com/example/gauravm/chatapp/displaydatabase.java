package com.example.gauravm.chatapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class displaydatabase extends Fragment implements View.OnClickListener {
    TextView textView;
    Button button;


    // Define a projection that specifies which columns from the database
// you will actually use after this query.
    String[] projection = {
            BaseColumns._ID,
            FeedReaderContract.FeedEntry.Messages,
            FeedReaderContract.FeedEntry.isMine,
            FeedReaderContract.FeedEntry.Receiver
    };

    // Filter results WHERE "title" = 'My Title'
    String selection = null;
    String[] selectionArgs =null;
String data="";int temp=0;
String details="";
SQLiteDatabase db;
int i=0;
    // How you want the results sorted in the resulting Cursor


    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.database, container, false);
        Context context=view.getContext();
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
      db = mDbHelper.getReadableDatabase();
        button=(Button)view.findViewById(R.id.databasedata);
        textView=(TextView)view.findViewById(R.id.databasecontent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = db.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        selection,              // The columns for the WHERE clause
                        selectionArgs,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        null               // The sort order
                );
                cursor.moveToFirst();


               do {
                   data=cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Messages));
                   temp=cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.isMine));
                   details=cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.Receiver));
                   if(temp==0){

                         textView.append("you recieved msg from: "+details+"\n"+"MSG:"+ data + "\n");}
                   else{

                       textView.append("you sent msg to: "+details+"\n"+"MSG:"+ data + "\n");
                   }
               }while(cursor.moveToNext());




            }
        });

        return view;
    }
    @Override
    public void onClick(View view) {

    }
}
