package me.leefeng.imageselector.imagelook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.imageselector.Image;
import me.leefeng.imageselector.ImgSelConfig;
import me.leefeng.imageselector.R;
import me.leefeng.imageselector.StatusBarCompat;

/**
 * Created by limxing on 2016/12/12.
 */

public class ImageLookActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, CompoundButton.OnCheckedChangeListener {
    private List<Image> list;
    private ViewPagerAdapter adapter;
    private ViewPager vp;
    private TextView title_name;
    private TextView imagelook_name;
    private ImageView imageLookCheck;
//    private CheckBox imagelook_cb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ImgSelConfig.isStateTran)
            StatusBarCompat.translucentStatusBar(this);
        setContentView(R.layout.activity_imagelook);
        vp = (ViewPager) findViewById(R.id.imagelook_vp);

        title_name = (TextView) findViewById(R.id.selectimage_title_name);
        imagelook_name = (TextView) findViewById(R.id.imagelook_name);
        imageLookCheck = (ImageView) findViewById(R.id.imagelook_cb);

        if (!ImgSelConfig.isLook) {
            imageLookCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = vp.getCurrentItem();
                    if (ImgSelConfig.checkedList.contains(list.get(position))) {
                        ImgSelConfig.checkedList.remove(list.get(position));
                        imageLookCheck.setImageResource(R.drawable.imgsel_icon_unselected);
                    } else if (ImgSelConfig.checkedList.size() >= ImgSelConfig.maxNum) {
                        Toast.makeText(view.getContext(), "最多选择" + ImgSelConfig.maxNum + "张图片", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ImgSelConfig.checkedList.add(list.get(position));
                        imageLookCheck.setImageResource(R.drawable.imgsel_icon_selected);
                    }
                }
            });
        } else {
            imageLookCheck.setVisibility(View.GONE);
        }
        int position = getIntent().getIntExtra("position", 0);
        list = ImgSelConfig.currentList;
        adapter = new ViewPagerAdapter(list, this);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(this);
        vp.setCurrentItem(position);
        if (position == 0) {
            onPageSelected(0);
        }

        ImageView back = (ImageView) findViewById(R.id.imagelook_bac);
        if (ImgSelConfig.titleBackImage != null) {
            back.setImageDrawable(ImgSelConfig.titleBackImage);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vp.removeAllViews();
        vp.setAdapter(null);
        vp = null;
        adapter.onDestory();
        adapter = null;
        list = null;
    }

    /**
     * ViewPager监听事件
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Image image = list.get(position);
        title_name.setText(position + 1 + "/" + adapter.getCount());
        if (image.getName() != null && image.getName().length() > 0) {
            imagelook_name.setVisibility(View.VISIBLE);
            imagelook_name.setText(image.getName());
        } else if (imagelook_name.getVisibility() == View.VISIBLE) {
            imagelook_name.setVisibility(View.GONE);
        }
        if (ImgSelConfig.isLook) {
            return;
        }
        if (ImgSelConfig.checkedList.contains(list.get(position))) {
            imageLookCheck.setImageResource(R.drawable.imgsel_icon_selected);
        } else {
            imageLookCheck.setImageResource(R.drawable.imgsel_icon_unselected);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Checkbox状态改变
     *
     * @param compoundButton
     * @param b
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            if (ImgSelConfig.checkedList.size() >= ImgSelConfig.maxNum) {
                Toast.makeText(this, "最多选择" + ImgSelConfig.maxNum + "张图片", Toast.LENGTH_SHORT).show();
//                compoundButton.setChecked(false);
//                imagelook_cb.setChecked(false);
                return;
            }
            if (!ImgSelConfig.checkedList.contains(list.get(vp.getCurrentItem())))
                ImgSelConfig.checkedList.add(list.get(vp.getCurrentItem()));
        } else {
            ImgSelConfig.checkedList.remove(list.get(vp.getCurrentItem()));
        }

    }
}
