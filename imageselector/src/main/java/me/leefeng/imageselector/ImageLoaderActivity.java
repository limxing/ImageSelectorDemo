package me.leefeng.imageselector;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.leefeng.imageselector.imagelook.ImageLookActivity;

/**
 * Created by limxing on 2016/12/11.
 */

public class ImageLoaderActivity extends AppCompatActivity implements FolderListItemListener, ImageListItemListener {

    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private static final String TAG = "ImageLoaderActivity";
    private static int DEFAULT_TOP = 500;
    private RecyclerView selectimage_list;
    private List<Folder> folderList;
    private List<Image> imageList;
    private boolean hasFolderGened = false;
    private ImageListAdapter imageListAdapter;
    private RecyclerView selectimage_list_folder;
    private FolderListAdapter folderListAdapter;
    private RelativeLayout.LayoutParams folderParamas;
    private int bottomY;
    private TextView selectimage_folder_tv;
    private int toY;
    private View selectimage_list_folder_bac;
    private TextView selectimage_confirm_num;
    private TextView selectimage_title_right;
    private TextView finishButton;
    private ArrayList<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ImgSelConfig.isStateTran)
            StatusBarCompat.translucentStatusBar(this);
        setContentView(R.layout.activity_selectimage);
        array = getIntent().getStringArrayListExtra("array");
        View selectimage_bottom = findViewById(R.id.selectimage_bottom);
        selectimage_list_folder_bac = findViewById(R.id.selectimage_list_folder_bac);
        selectimage_list_folder_bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimY(selectimage_list_folder, DEFAULT_TOP, bottomY);
                selectimage_list_folder_bac.setVisibility(View.GONE);
            }
        });
        selectimage_bottom.measure(0, 0);
        bottomY = getResources().getDisplayMetrics().heightPixels - selectimage_bottom.getMeasuredHeight();
        DEFAULT_TOP = (int) (getResources().getDisplayMetrics().heightPixels - 400 * getResources().getDisplayMetrics().density);
        selectimage_list = (RecyclerView) findViewById(R.id.selectimage_list);
        selectimage_list_folder = (RecyclerView) findViewById(R.id.selectimage_list_folder);

        folderParamas = (RelativeLayout.LayoutParams) selectimage_list_folder.getLayoutParams();
        folderParamas.setMargins(0, bottomY, 0, 0);
        selectimage_list_folder.setLayoutParams(folderParamas);
        selectimage_confirm_num = (TextView) findViewById(R.id.selectimage_confirm_num);
        selectimage_title_right = (TextView) findViewById(R.id.selectimage_title_right);
        selectimage_title_right.setText("0/" + ImgSelConfig.maxNum);
        if (ImgSelConfig.maxNum <= 0) {//不设上限选择
            selectimage_title_right.setVisibility(View.GONE);
        }
        if (ImgSelConfig.maxNum == 0) {//单选，某一个返回
            findViewById(R.id.selectimage_confirm).setVisibility(View.GONE);
        }
        selectimage_folder_tv = (TextView) findViewById(R.id.selectimage_folder_tv);
        selectimage_folder_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toY == DEFAULT_TOP) {
                    setAnimY(selectimage_list_folder, DEFAULT_TOP, bottomY);
                    selectimage_list_folder_bac.setVisibility(View.GONE);

                } else {
                    setAnimY(selectimage_list_folder, bottomY, DEFAULT_TOP);
                    selectimage_list_folder_bac.setVisibility(View.VISIBLE);

                }
            }
        });
        if (ImgSelConfig.titleBackImage != null) {
            ((ImageView) findViewById(R.id.selectimage_title_bac)).setImageDrawable(ImgSelConfig.titleBackImage);
        }
        findViewById(R.id.selectimage_title_bac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.selectimage_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageListAdapter.getCheckList().size() > 0) {
                    Intent intent = getIntent();
                    intent.putExtra("array", imageListAdapter.getStringArray());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        finishButton = (TextView) findViewById(R.id.selectimage_confirm_text);
        folderList = new ArrayList<>();
        imageList = new ArrayList<>();
        ImgSelConfig.currentList = imageList;//指向
        imageListAdapter = new ImageListAdapter(imageList, this, this);
        folderListAdapter = new FolderListAdapter(folderList, this, this);

        selectimage_list.setLayoutManager(new GridLayoutManager(this, ImgSelConfig.spanCount));
        selectimage_list.addItemDecoration(new DividerGridItemDecoration(selectimage_list.getContext()));
        selectimage_list.setAdapter(imageListAdapter);

        selectimage_list_folder.setLayoutManager(new LinearLayoutManager(this));
        selectimage_list_folder.setAdapter(folderListAdapter);


        getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);


    }

    public static void startActivityForResult(Activity activity, ArrayList<String> paths) {
        Intent intent = new Intent(activity, ImageLoaderActivity.class);
        intent.putStringArrayListExtra("array", paths);
        activity.startActivityForResult(intent, ImgSelConfig.REQUEST_CODE);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//            if (id == LOADER_ALL) {
            CursorLoader cursorLoader = new CursorLoader(ImageLoaderActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
//            } else if (id == LOADER_CATEGORY) {
//                CursorLoader cursorLoader = new CursorLoader(ImageLoaderActivity.this,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
//                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
//                return cursorLoader;
//            }
//            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<Image> tempImageList = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
//                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, dateTime);
                        if (!image.getPath().endsWith("gif"))
                            tempImageList.add(image);
                        if (!hasFolderGened) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            if (!folderList.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                folderList.add(folder);
                            } else {
                                Folder f = folderList.get(folderList.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (data.moveToNext());

                    imageList.clear();
                    if (ImgSelConfig.needCamera)
                        imageList.add(new Image());
                    imageList.addAll(tempImageList);

                    Folder folder = new Folder();
                    List<Image> imageList = new ArrayList<>();
                    imageList.addAll(tempImageList);
                    folder.images = imageList;
                    folder.name = "全部照片";
                    folderList.add(0, folder);

//                    Log.i(TAG, "onLoadFinished: "+imageList.size());
//                    if (Constant.imageList != null && Constant.imageList.size() > 0) {
                    //imageListAdapter.setDefaultSelected(Constant.imageList);
//                    }

                    folderListAdapter.notifyDataSetChanged();

                    hasFolderGened = true;
                    data.close();
                    if (array != null && array.size() > 0) {
                        for (String s : array) {
                            for (Image image : imageList) {
                                if (s.equals(image.getPath())) {
                                    imageListAdapter.addCheckedImage(image);
                                    continue;
                                }
                            }
                        }
                    }
                    imageListAdapter.notifyDataSetChanged();

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * 控件的Y值的动画
     *
     * @param view
     * @param fromY
     * @param toYY
     */

    public void setAnimY(final View view, int fromY, int toYY) {
        this.toY = toYY;
        ValueAnimator animator = null;
        animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setTarget(view);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                folderParamas.setMargins(0, (int) f, 0, 0);
                view.setLayoutParams(folderParamas);
            }

        });
    }

    /**
     * 文件夹条目的点击
     *
     * @param position
     */
    @Override
    public void folderListItemClick(int position) {
        imageList.clear();
        imageList.addAll(folderList.get(position).images);
        imageListAdapter.notifyDataSetChanged();
        setAnimY(selectimage_list_folder, DEFAULT_TOP, bottomY);
        selectimage_list_folder_bac.setVisibility(View.GONE);
        imageListAdapter.clearCheckList();
        selectimage_folder_tv.setText(folderList.get(position).name);
        onItemChecked(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageListAdapter.destory();
        imageListAdapter = null;
        folderListAdapter.destory();
        imageList.clear();
        folderList.clear();
        ImgSelConfig.checkedList = null;
        ImgSelConfig.currentList = null;
        mLoaderCallback = null;
        System.gc();
    }

    /**
     * 图片的点击事件
     *
     * @param position
     */
    @Override
    public void onItemChecked(int position) {
        int sumCheck = imageListAdapter.getCheckList().size();
        if (sumCheck > 0) {
            selectimage_confirm_num.setVisibility(View.VISIBLE);
            finishButton.setTextColor(Color.WHITE);
        } else {
            selectimage_confirm_num.setVisibility(View.GONE);
            finishButton.setTextColor(Color.parseColor("#cccccc"));
        }
        selectimage_confirm_num.setText(sumCheck + "");
        selectimage_title_right.setText(sumCheck + "/" + ImgSelConfig.maxNum);

    }

    @Override
    public void onItemClick(int position) {
        if (ImgSelConfig.maxNum != 0) {
            Intent intent = new Intent(this, ImageLookActivity.class);
            intent.putExtra("position", position);
            startActivityForResult(intent, 0);
        } else {
            Intent intent = getIntent();
            intent.putExtra("path", imageList.get(position).getPath());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageListAdapter.notifyDataSetChanged();
        onItemChecked(0);
    }
}
