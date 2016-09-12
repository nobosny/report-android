package com.vectoranalytica.trashwatchfree;

/**
 * Created by Nobosny on 1/6/2015.
 */
public class DataHolder {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {
        return holder;
    }
}
