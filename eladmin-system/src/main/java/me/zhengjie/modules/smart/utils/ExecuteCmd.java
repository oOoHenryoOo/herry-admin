package me.zhengjie.modules.smart.utils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yster@foxmail.com 2018年4月19日 下午1:50:06
 */
public class ExecuteCmd {
    /** 执行外部程序,并获取标准输出 */
    public static String execute(String[] cmd, String... encoding) {
        BufferedReader bReader = null;
        InputStreamReader sReader = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            /* 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞 */
            Thread t = new Thread(new InputStreamRunnable(p.getErrorStream(), "ErrorStream"));
            t.start();

            /* "标准输出流"就在当前方法中读取 */
            BufferedInputStream bis = new BufferedInputStream(p.getInputStream());

            if (encoding != null && encoding.length != 0) {
                sReader = new InputStreamReader(bis, encoding[0]);// 设置编码方式
            } else {
                sReader = new InputStreamReader(bis, "utf-8");
            }
            bReader = new BufferedReader(sReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            bReader.close();
            p.destroy();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

class InputStreamRunnable implements Runnable {
    BufferedReader bReader = null;

    public InputStreamRunnable(InputStream is, String _type) {
        try {
            bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void run() {
        String line;
        int num = 0;
        try {
            while ((line = bReader.readLine()) != null) {
                System.out.println("---->"+String.format("%02d",num++)+" "+line);
            }
            bReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
