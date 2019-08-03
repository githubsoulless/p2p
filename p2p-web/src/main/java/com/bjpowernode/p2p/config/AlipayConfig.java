package com.bjpowernode.p2p.config;

import java.io.FileWriter;
import java.io.IOException;

public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101000656803";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQClLM+d9OplXtsYvLCsJNP1LdgLAPGnpGvK8ZgEK33KpQAMhWkHFjiPUftbf/6jb6LYQtg+0SB3KyYRcFSGv5+sOv28XGwmc1KFMo/zdEVoJuWmgC1mfgm0a9nhwp1fDP/D3Lzg+E1bOncm6sL9M/toObIHeh96cgDMCnk7RfLEhv2YZMbml/WajBGfNJ2BJzzC/L/2zZ8O5RHfJjjH7f6L5Uai7Vx7oIElYK+U4MQDmdIElQ9kkB1i9KsIJk7Bd9F3nWQ26zJkkLnwEK9f4t/3+i2L4IpdaD9EqDtCOsXkOGTTjkV0SgkMGlyj/0icJhOgDuVjiM5pImgepgP4t5DNAgMBAAECggEAPKTtPXN0s+9VxglRXILRB43janbYQtLNeN+nDrDhKIvYLsC0xRNVhNl2zit6VItiYMB6IOYrY1WGTpTNlAO7HkMycfwFAUEKJTlhPOONsCXH34/kG2NfMM0AXDFWElX1efYLqxW/YSVgJfu/x7NUNPop8TDXad99h2y5Ahg2gAzvy2RwtJbN3bQUW7Mpv49ors0B3god83BIXKg5RSb5ubOKYHTvfTfGtPUiBo2PIasNGyJvyQ0ELYNLHqY+4HqSnLZk0MQS7Q80AmpeUjGjPOyRlr66LmofY+2Z6gxjwsNJnfPsqGWrisaVtpKmh5ZgxKmNljeBvWcWhprSNYRqwQKBgQDpd5T6Zftx68KqTPWPhuIZHzIHXJvaBjnokSFy+ZmEEsyD0utwiP4H8U5ghOG3acZMfTunfrJ98JbjemGyVkTVLWE0caIDCKbApx7y2v+TLXn7lIV2GnGMelfb4sT9j/XGFt1V1qxBOZ74LZT6yrkI+Lo+OGw0xqw1+6I0RXQQhQKBgQC1HeTieNQnCAddlABlS9w7+uM0lIjXQLwO+TJk7qYzA1md2h8+a80uD8sqML+D8CwClGXSl9RVGDG6AASvaaFjKD4rps5I0TLtCFxE8zIDwfhes1437ukwT5HeeOfhEEk2Wn8TBPuuwGTf7bgbcxxOxqCzAGpqYxCMw7zBFBLVqQKBgG5ZFxFDIpW12UylE2vDVRqKpBrWqtNiHfzOnAC7+NN0DzY/ewi1t1wJsgHszMLkO9XrW0bPm43rKWZbcdHY9K/NwyQGkYzdifOFChu/Zw0pcX53tJ0d4o2DRgOoK3Otl30JUZRA4ynxEjI7WS8wokSbS2wdPwey37KkPQZ3zWQBAoGAQFmXuhN+JUQIAeqU3Wi/nhv83/CqUfhxGMzXZ98W8+cLzzbxhOceZnA8Gghp80HBEUWSi+lgCr/m4xQglgZszeM9e5rvjhWuB2PCDMlvisA/M759Clh7R6vtwuKoWGHF8Wbj4WkS1zsKXDmFAVO7yg1uiUrrhFMgTTM6fEtaickCgYEAj9cfu17R8SY8SDHaPD7+Pg0FyQ4tt1N7UmUcIjgB6JKN8dONP9BG3TaG4SYvJVrLjTh3XOrSrlodwHTMy/TTfYqtMfkhoO7q8Zlcwio25HZCVJoWK8grVocNPZ6oW6trlBu9Vq3X63XKjEtACCeLsiT6mSOG5x4isdBFXXlsEvo=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxhR1Y+Dm35C2zLUmwRa6AtxxtUjmpaoxjtNw375kSP21gJ8qYIs1aetCKHMrl6wGxtECVVW7+W43TIYZqG83roxSjYtjf6tqv2oAfoGn72ymZc3zAHNwf2ZLFM8af9Qxe5znoPgYbKxCJLDfd1LZ5olXZqtRvyJuei8dnk2yr5kLp38vELp3BT+n7FU466mavbxMV3NolhknzyHSFk+C4YWgiHz0FgZH755qgxpLVZ+tqk+cyoduPY4fLZUz0dc+lJLJEad0EqSZ5h/f6H8SKhwncodwZw/r8t7KItD9uJVVbXc9TcUAZBOIagXeW6Pianm8ouc3y+eHPG19dWB8kwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/p2p/loan/alipayNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/p2p/loan/alipayBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
