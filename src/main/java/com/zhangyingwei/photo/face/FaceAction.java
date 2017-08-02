package com.zhangyingwei.photo.face;

import com.baidu.aip.face.AipFace;
import com.zhangyingwei.photo.utils.ImageUtils;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by zhangyw on 2017/8/2.
 */
public class FaceAction {
    private static final String APP_ID = "9959389";
    private static final String APP_KEY = "BW5QMG9yv88RMxpwSYaNGGix";
    private static final String SECRET_KEY = "H5C5s4ODIRNNQOkT6OMwQwhQ6cQKU9f0";

    public static void main(String[] args) throws IOException {
        AipFace client = new AipFace(APP_ID, APP_KEY, SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        String path = "src/main/resources/static/img/i2.jpg";
        JSONObject res = client.detect(path, new HashMap<String, String>());
        JSONObject result = (JSONObject) res.getJSONArray("result").get(0);
        JSONObject location = result.getJSONObject("location");
        System.out.println(location.toString(2));
        int top = location.getInt("top");
        int left = location.getInt("left");
        int width = location.getInt("width");
        int height = location.getInt("height");
        String cutPath = "src/main/resources/static/img/i2-cut.jpg";
        ImageUtils.cut(left,top,width,height,path,cutPath);
        System.out.println("img cut");
        String resizePath = "src/main/resources/static/img/i2-resize.jpg";
        ImageUtils.resize(new File(cutPath), new File(resizePath), 165, 1f);
        System.out.println("img resize");
        String mainPath = "src/main/resources/static/img/main.png";
        BufferedImage resultImage = ImageUtils.watermark(new File(mainPath), new File(resizePath), 168, 210, 1f);
        String resultPath = "src/main/resources/static/img/result2.png";
        ImageUtils.generateWaterFile(resultImage,resultPath);
    }
}
