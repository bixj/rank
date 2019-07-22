package com.pandatv.tools;

/**
 * Created by likaiqing on 2017/4/19.
 */
public class TimeHelper {
    /**
     * 时间工具类，预设一个超时时间timeout
     * 提供方法来检查当前时间间隔是否足够
     */
    private int timeout = 0;
    private long last = 0;

    public TimeHelper(int timeout) {
        this.timeout = timeout;
        last = System.currentTimeMillis();
    }

    /**
     * @return 如果时间间隔足够，返回true，并更新记录时间
     */
    public boolean checkTimeout() {
        long now = System.currentTimeMillis();

        boolean isTimeout = (now - last) > timeout;
        if (isTimeout) last = now;
        return isTimeout;
    }
}
