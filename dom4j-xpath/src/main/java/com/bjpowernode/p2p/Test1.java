package com.bjpowernode.p2p;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.junit.Test;

import java.util.List;

public class Test1 {
    public static void main(String[] args) throws DocumentException {
        String xmlStr = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><bookstore><book><title lang=\"eng\">Harry Potter</title><price>29.99</price></book><book><title lang=\"eng\">Learning XML</title><price>39.95</price></book></bookstore>";

//        将字符串类型的数据转换为document对象
        Document document = DocumentHelper.parseText(xmlStr);

//        获取第二个book下title的文本内容
//        先获得节点
        Node node = document.selectSingleNode("//book[2]//title");

//        获取结点中的内容
        String text = node.getText();
       // System.out.println(text);

//
        List<Node> nodes = document.selectNodes("//book[2]");
        for(Node node1: nodes){
//            String text2 = node1.getText();
//            System.out.println(text2);
        }

        Node node2 = document.selectSingleNode("//title[1]");
        System.out.println(node2.getText());
    }

    @Test
    public void test2() throws DocumentException {
String xmlStr="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><web-app><servlet><servlet-name>loginServlet</servlet-name><servlet-class>com.bjpowernode.servlet.LoginServlet</servlet-class></servlet><servlet-mapping><servlet-name>loginServlet</servlet-name><url-pattern>/login</url-pattern></servlet-mapping><servlet><servlet-name>registerServlet</servlet-name><servlet-class>com.bjpowernode.servlet.RegisterServlet</servlet-class></servlet><servlet-mapping><servlet-name>registerServlet</servlet-name><url-pattern>/register</url-pattern></servlet-mapping></web-app>";
        Document document = DocumentHelper.parseText(xmlStr);
//        for(int i =1;i<)
        Node node = document.selectSingleNode("/servlet[1]//servlet-name");
        String text = node.getText();
        System.out.println(text);
    }
}

