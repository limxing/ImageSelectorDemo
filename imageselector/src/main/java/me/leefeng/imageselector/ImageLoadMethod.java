package me.leefeng.imageselector;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by limxing on 2016/12/13.
 */

public interface ImageLoadMethod {
     void displayImage(Context context, String path, ImageView imageView);
}
