package me.leefeng.imageselector;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limxing on 2016/12/11.
 */

public class ImgSelConfig {
    public static final int REQUEST_CODE = 999;
    public static final int RESULT_CODE = 888;

    public static int titleColor = Color.parseColor("#0056ac");
    public static int bottomBarColor = Color.parseColor("#0056ac");
    public static int titleHeight;
    public static Drawable titleBackImage;
//    public static boolean needCamera = false;
    //    public static ArrayList<String> array;
    public static int maxNum = 9;
    public static int spanCount = 3;

    public static List<Image> checkedList;
    public static List<Image> currentList;

    public static ImageLoadMethod loadMethod;
    public static boolean isStateTran=true;
    public static boolean showImageName;

    public static boolean isLook=false;
    public static boolean lastIsNone=false;
}
