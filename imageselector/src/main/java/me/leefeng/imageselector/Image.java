package me.leefeng.imageselector;

/**
 * Image bean
 * Created by Yancy on 2015/12/2.
 */
public class Image<T> {

    private String path;
    private String name;
    private long time;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Image(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }


    public Image(String s, long s1) {
        this.path = s;
        this.time = s1;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}