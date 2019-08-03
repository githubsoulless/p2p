package com.bjpowernode;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:QRCodeTest
 * Package:com.bjpowernode
 * Description:
 *
 * @date:2019/6/22 15:54
 * @author:guoxin
 */
public class QRCodeTest {


    @Test
    public void generateQRCode() throws WriterException, IOException {
        Map<EncodeHintType,Object> hintTypeObjectMap = new HashMap<EncodeHintType, Object>();
        hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //使用fastjson来拼接json格式的字符串
        //{"country":"CHINA","province":"HeBei","city":"国际庄","detail":{"area":"开发区","address":"幸福社区"}}
        //创建一个JSON对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("country","CHINA");
        jsonObject.put("province","HeBei");
        jsonObject.put("city","国际庄");
        JSONObject detailJson = new JSONObject();
        detailJson.put("area","开发区");
        detailJson.put("address","幸福社区");
        jsonObject.put("detail",detailJson);
        //将json对象转换为json格式的字符串
        String jsonString = jsonObject.toString();


        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("weixin://wxpay/bizpayurl?pr=3HP3Dvb", BarcodeFormat.QR_CODE,200,200,hintTypeObjectMap);

        String filePath = "D://";
        String fileName = "qrCode.jpg";
        Path path = FileSystems.getDefault().getPath(filePath,fileName);

        //将矩阵对象生成二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);

        System.out.println("生成图片");

        System.out.println("aaa");

    }
}
