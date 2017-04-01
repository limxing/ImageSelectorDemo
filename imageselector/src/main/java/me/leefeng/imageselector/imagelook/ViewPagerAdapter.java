package me.leefeng.imageselector.imagelook;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.leefeng.imageselector.Image;
import me.leefeng.imageselector.ImgSelConfig;
import me.leefeng.imageselector.PhotoView.PhotoView;

/**
 *
 *
 * @author leefeng.me
 *
 * 2016/8/30 15:23
 *
 *
 */
public class ViewPagerAdapter extends PagerAdapter {
    private final int width;
    private final int height;
    private Context context;
    private List<Image> list;
    private List<WeakReference<PhotoView>> imageList;

    public ViewPagerAdapter(List list, Context context) {
        this.list = list;
        this.context = context;
        imageList = new ArrayList<>();
      width=  context.getResources().getDisplayMetrics().widthPixels;
      height=  context.getResources().getDisplayMetrics().heightPixels;
        Log.i("", "ViewPagerAdapter: "+width+"=="+height);
    }

    @Override
    public int getCount() {
        if (ImgSelConfig.lastIsNone) {
            return list.size() - 1;
        }
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Image att = list.get(position);
        PhotoView view;
        if (imageList.isEmpty()) {
            initView(4);
        }
        view = imageList.get(0).get();

        if (view == null) {
            clear();
            imageList.clear();
            initView(2);
            view = imageList.get(0).get();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
        } else {
            container.addView(view);
        }
        view.enable();
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (ImgSelConfig.loadMethod != null) {
            ImgSelConfig.loadMethod.displayImage(context, att.getPath(), view);
        } else {
            Glide.with(container.getContext())
                    .load(att.getPath())
                    .override(width,height)
                    .into(view);
        }
        imageList.remove(0);
        return view;
    }

    /**
     * @param num
     */
    public void initView(int num) {
        for (int i = 0; i < num; i++) {
            imageList.add(new WeakReference<>(new PhotoView(context)));
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (imageList != null) {
            imageList.add(new WeakReference<>((PhotoView) object));
        }
        container.removeView((View) object);
        Glide.clear((View) object);
    }


    public Image getCurItem(int i) {
        return list.get(i);
    }

    public void onDestory() {
        context = null;
        list = null;
        clear();
        imageList.clear();
        imageList = null;
    }

    public void clear() {
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).get() != null) {
                Glide.clear(imageList.get(i).get());
            }
        }
    }

    public void setList(List<Image> attachments) {
        list = attachments;
        notifyDataSetChanged();
    }


    /**
     *
     */
    private int mChildCount = 0;

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
