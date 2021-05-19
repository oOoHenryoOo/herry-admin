package me.zhengjie;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class test {
    public static void main(String[] args) throws Exception {

        /*Calendar rightNow=Calendar.getInstance();
        Date d1=rightNow.getTime();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime=(sdf1.format(d1)).toString();

        File dir=new File("D:/ljj");
        dir.mkdirs();
        String makeBoatsLogStr="D:/ljj/"+nowtime+"MakeBoatsLog.sgy";
        File makeBoatsLog=new File(makeBoatsLogStr);
        FileWriter fw;
        fw=new FileWriter(makeBoatsLog,true);
        FileOutputStream fos = new FileOutputStream(makeBoatsLog);
        String lineEnd="\r\n";
        // 组装数据
        String lhl= "2,lhl asdf FileOutputStream(makeBoatsLog)";
        byte[] buf = new byte[3200];
        buf = lhl.getBytes();
        byte[] buff = CodeUtil.ASCIIToEBCDIC(buf);
//        fw.write(String.valueOf(buff));
//        fw.flush();
        fos.write(buff);
        fos.close();
        System.out.println(nowtime+" 完成！");*/
    }
}
