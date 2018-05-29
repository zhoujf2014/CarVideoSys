package com.gtafe.carvideosys;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class OperationService {
    DatabaseHelper dbHelper;

    public OperationService(Context context, String name) {
        dbHelper = new DatabaseHelper(context, name, null, 1);
    }

    public List<String> queryShowName() {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("optionlist", null, null, null, null, null,
                null);
        int no = 1;
        while (cursor.moveToNext()) {
            String showname = no + "." + cursor.getString(cursor.getColumnIndex("name"));
            list.add(showname);
            no++;
        }
        return list;
    }

}
