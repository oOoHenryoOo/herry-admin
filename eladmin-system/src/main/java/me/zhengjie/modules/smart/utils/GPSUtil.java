package me.zhengjie.modules.smart.utils;

import cn.hutool.core.util.StrUtil;

public class GPSUtil {
    public static String wd (String wd) {
         Double  f = Double.valueOf(StrUtil.subSuf(wd, 2)) / 60 ;
         Integer d = Integer.valueOf(StrUtil.sub(wd, 0, 2));
         return String.valueOf(d + f);
    }

    public static String jd (String jd) {
        Double  f = Double.valueOf(StrUtil.subSuf(jd, 3)) / 60 ;
        Integer d = Integer.valueOf(StrUtil.sub(jd, 0, 3));
        return String.valueOf(d + f);
    }
}
