package com.zhangyingwei.photo.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by zhangyw on 2017/8/2.
 */
public class ImageUtils {
    public static void cut(int x1, int y1, int width, int height,String sourcePath, String descpath) {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            is = new FileInputStream(sourcePath);
            String fileSuffix = sourcePath.substring(sourcePath.lastIndexOf(".") + 1);
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(fileSuffix);
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x1, y1, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, fileSuffix, new File(descpath));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iis = null;
            }
        }
    }

    public static void resize(File originalFile, File resizedFile,int newWidth, float quality) throws IOException {

        if (quality > 1) {
            throw new IllegalArgumentException("Quality has to be between 0 and 1");
        }

        ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());
        Image i = ii.getImage();
        Image resizedImage = null;
        int iWidth = i.getWidth(null);
        int iHeight = i.getHeight(null);
        if (iWidth > iHeight) {
            resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);
        } else {
            resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight,newWidth, Image.SCALE_SMOOTH);
        }

        // This code ensures that all the pixels in the image are loaded.
        Image temp = new ImageIcon(resizedImage).getImage();

        // Create the buffered image.
        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null),temp.getHeight(null), BufferedImage.TYPE_INT_RGB);

        // Copy image to buffered image.
        Graphics g = bufferedImage.createGraphics();

        // Clear background and paint the image.
        g.setColor(Color.white);
        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
        g.drawImage(temp, 0, 0, null);
        g.dispose();

        // Soften.
        float softenFactor = 0.05f;
        float[] softenArray = { 0, softenFactor, 0, softenFactor,1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };
        Kernel kernel = new Kernel(3, 3, softenArray);
        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        bufferedImage = cOp.filter(bufferedImage, null);

        // Write the jpeg to a file.
        FileOutputStream out = new FileOutputStream(resizedFile);

        // Encodes image as a JPEG data stream
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);

        param.setQuality(quality, true);

        encoder.setJPEGEncodeParam(param);
        encoder.encode(bufferedImage);
    }

    /**
     *
     * @Title: 构造图片
     * @Description: 生成水印并返回java.awt.image.BufferedImage
     * @param file
     *            源文件(图片)
     * @param waterFile
     *            水印文件(图片)
     * @param x
     *            距离右下角的X偏移量
     * @param y
     *            距离右下角的Y偏移量
     * @param alpha
     *            透明度, 选择值从0.0~1.0: 完全透明~完全不透明
     * @return BufferedImage
     * @throws IOException
     */
    public static BufferedImage watermark(File file, File waterFile, int x, int y, float alpha) throws IOException {
        // 获取底图
        BufferedImage buffImg = ImageIO.read(file);
        BufferedImage backImage = new BufferedImage(buffImg.getWidth(), buffImg.getHeight(), BufferedImage.TYPE_INT_RGB);
        // 获取层图
        BufferedImage waterImg = ImageIO.read(waterFile);
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = backImage.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 绘制
        g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);


        int waterImgWidth2 = buffImg.getWidth();// 获取层图的宽度
        int waterImgHeight2 = buffImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 绘制
        g2d.drawImage(buffImg, 0, 0, waterImgWidth2, waterImgHeight2, null);

        g2d.dispose();// 释放图形上下文使用的系统资源
        return backImage;
    }

    /**
     * 输出水印图片
     *
     * @param buffImg
     *            图像加水印之后的BufferedImage对象
     * @param savePath
     *            图像加水印之后的保存路径
     */
    public static void generateWaterFile(BufferedImage buffImg, String savePath) {
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
