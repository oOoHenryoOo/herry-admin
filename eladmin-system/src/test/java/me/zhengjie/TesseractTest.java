package me.zhengjie;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.net.URL;

public class TesseractTest {
    private ITesseract tesseract;

    public static void main(String[] args) {
        // 识别图片的路径（修改为自己的图片路径）
        String path = "D:\\testpic\\5.png";
        File file = new File(path);

        ITesseract instance = new Tesseract();
        //获得Tesseract的文字库，设置语言库位置
//        URL tessdataPath = ClassLoader.getSystemResource("tessdata");
        instance.setDatapath("D:\\Tesseract-OCR\\tessdata");
        //chi_sim ：简体中文， eng：英文    根据需求选择语言库
        instance.setLanguage("chi_sim");

        String result = null;
        try {
            long startTime = System.currentTimeMillis();
            result =  instance.doOCR(file);
            long endTime = System.currentTimeMillis();
            System.out.println("Time is：" + (endTime - startTime) + " 毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("result: ");
        System.out.println(result);

    }

}
