package me.leefeng.imageselectordemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.leefeng.imageselector.ImageLoadMethod;
import me.leefeng.imageselector.ImageLoaderActivity;
import me.leefeng.imageselector.ImgSelConfig;

public class MainActivity extends AppCompatActivity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageview);
    }

    public void getPic(View view) {
        ImgSelConfig.maxNum=1;
        ImgSelConfig.titleHeight=44*3;
        ImgSelConfig.titleColor= Color.parseColor("#ff0099");
        ImgSelConfig.bottomBarColor= Color.parseColor("#ff0033");
//        ImgSelConfig.isStateTran=false;
        ImgSelConfig.loadMethod=new ImageLoadMethod() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).placeholder(me.leefeng.imageselector.R.drawable.ic_default_image).into(imageView);
            }
        };
        ImageLoaderActivity.startActivityForResult(this, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == me.leefeng.imageselector.ImgSelConfig.REQUEST_CODE && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra("array");
            String path = data.getStringExtra("path");
            if (list != null)
                Glide.with(this).load(list.get(0)).into(image);
            if (path != null) {
                Glide.with(this).load(path).into(image);
            }
        }
    }

}
