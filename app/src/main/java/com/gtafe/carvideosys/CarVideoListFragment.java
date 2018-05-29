package com.gtafe.carvideosys;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by jie.gao2 on 2016/10/17.
 */
public class CarVideoListFragment extends Fragment{
    public static View view;
    /*public static List<String> list=new ArrayList<String>();*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.carvideo_list,container,false);
        return view;
    }

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView=(ListView) view.findViewById(R.id.videolist);
//        list = new ArrayList<String>();
        File path = new File("data/data/com.example.jiegao2.carvideosys");
        path.mkdir();
        try {
            AssetManager assetmanager = getActivity().getApplicationContext().getAssets();
            InputStream inputstream = assetmanager.open("database.db");

            FileOutputStream fileoutputstream = new FileOutputStream("/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + "com.example.jiegao2.carvideosys" + "/" + "database.db");
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputstream.read(buffer)) > 0) {
                fileoutputstream.write(buffer, 0, count);
            }
            fileoutputstream.flush();
            fileoutputstream.close();
            inputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String DB_PATH = "/data/data/com.example.jiegao2.carvideosys/database.db";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        Cursor cursor = db.rawQuery("select * from optionlist", null);
        int no=1;
        while (cursor.moveToNext()) {
            String modulename = no+"."+cursor.getString(cursor.getColumnIndex("name"));
            list.add(modulename);
            no++;
        }
        *//*list=new OperationService(getActivity().getApplicationContext(),"database.db").queryShowName();*//*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CarVideoShow.timer.cancel();
                CarVideoShow.auto.setText("自动循环");
                CarVideoShow.isAuto=0;
                FragmentManager fm = getActivity().getFragmentManager();
                CarVideoShow carVideoShow = (CarVideoShow) fm.findFragmentById(R.id.fragment_show);
                carVideoShow.videoPlay(list.get(i),i);
            }
        });
    }*/
}
