package com.theta360.sample.v2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ImageList2D_Activity extends Activity {

    // 表示する画像の名前（拡張子無し）
    private String[] members = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list2d);

        Log.d("debug","*** Start ImageList2D_Activity ***");

        // File名をTextViewに表示

        Intent intent = getIntent();
        final String fileId = intent.getStringExtra("OBJECT_ID");

        Log.d("debug",fileId);

        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/image/image2d/" + getfilename(fileId);
        storagePath = storagePath.substring(0,storagePath.length()-4);
        Log.d("debug","storagePath = "+storagePath);

        final File[] _files = new File(storagePath).listFiles();
        ArrayList<File> files = new ArrayList<File>();

        Log.d("debug","2D image数"+Integer.toString(_files.length));
        for (int i=0; i<_files.length; i++) {
            Log.d("debug",_files[i].getName());
            if(_files[i].isFile()) {
                files.add(_files[i]);
                members[i] = _files[i].getName();
                Log.d("debug",_files[i].getName());
            }
        }

        // GridViewのインスタンスを生成
        GridView gridview = (GridView) findViewById(R.id.gridview);

        //グリッド4列表示
        gridview.setNumColumns(2);
        //表示する画像を取得
        ArrayList<Bitmap> lstBitmap = new ArrayList<Bitmap>();

        for(File file: files) {
            byte[] byteArray = convertFile(file);
            lstBitmap.add(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        }

        //アダプター作成
        BitmapAdapter adapter = new BitmapAdapter(getApplicationContext(), lstBitmap);
        //グリッドにアダプタを設定
        gridview.setAdapter(adapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(), Image2D_Activity.class);
                intent.putExtra("filepath",_files[position].getAbsolutePath());
                Log.d("debug",_files[position].getAbsolutePath());
                startActivity(intent);
            }
        });


    }
    public String getfilename(String fileId){

        String[] list = fileId.split("/",0);
        String fname = list[list.length-1];
        return fname;
    }
    public byte[] convertFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file);) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while(true) {
                int len = inputStream.read(buffer);
                if(len < 0) {
                    break;
                }
                bout.write(buffer, 0, len);
            }
            return bout.toByteArray();
        } catch (Exception e) {
            Log.d("debug","CANNOT OPEN FILE:"+file.getAbsolutePath());
            e.printStackTrace();
        }
        return null;
    }
}