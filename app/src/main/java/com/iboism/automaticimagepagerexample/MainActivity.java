package com.iboism.automaticimagepagerexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iboism.easyimagepager.EasyImagePager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EasyImagePager pager = (EasyImagePager) findViewById(R.id.placeholder);
        ArrayList<String> arr = new ArrayList<>();
        arr.add("http://i.imgur.com/v60CSTt.jpg");
        arr.add("http://i.imgur.com/5OQ7Pug.jpg");
        arr.add("http://i.imgur.com/8cBYgMI.jpg");
        pager.setImages(arr);
    }
}
