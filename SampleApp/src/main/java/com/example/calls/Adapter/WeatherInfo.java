/**************************************************************************************************
 * Copyright (C) 2016 WickerLabs. inc - All Rights Reserved.                                      *
 *                                                                                                *
 * NOTICE:  All information contained herein is, and remains the property of WickerLabs,          *
 * The intellectual and technical concepts contained herein are proprietary to WickerLabs.        *
 * Dissemination of this information or reproduction of this material                             *
 * is strictly forbidden unless prior permission is obtained from WickerLabs. inc                 *
 *                                                                                                *
 **************************************************************************************************/
package com.example.calls.Adapter;



import android.location.Location;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by littl on 2017/5/28.
 */
//使用新浪天气API
// http://php.weather.sina.com.cn/xml.php?city=%C4%CF%B2%FD&password=DJOYnieT8234jlsK&day=0
// city为汉字对应的GB2312编码String strCity = URLEncoder.encode("南昌", "GB2312")
// day为对应日期
public class WeatherInfo {

    String status; //correspond to status1
    String temperatureHigh; //temperature1
    String temperatureLow; //temperature2
    String pollution;//污染等级，用法pollution_l+"污染"
    String cy_tip;//chy_l穿衣说明，用法"适宜穿"+cy_tips
    String zwx_tip;//zwx_s紫外线
    String ssd_tip;//ssd_s舒适度
    String gm_tip; //gm_s感冒预警
    String yd_tip;//yd_s运动适宜度度
    String location;
    private static final String TAG = "WeatherInfo";
    public WeatherInfo(String Location){
        location = Location;
        status = null;
        temperatureHigh = null;
        temperatureLow = null;
        pollution = null;
        cy_tip = null;
        zwx_tip = null;
        //获取天气信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                getInfoFromNet();
            }
        }).start();

    }
    public String getWeather(){
        if (this.status == null || status.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getInfoFromNet();
                }
            }).start();
            return null;
        }
        String weather = this.status + "  "+ temperatureHigh +"-"+ temperatureLow + "°";
        return weather;
    }

    public String getWeatherMessage(){
        String weatherMessage = new String();
        if (this.status == null || status.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getInfoFromNet();
                }
            }).start();
            return null;
        }
        weatherMessage = location + "今天" + status + ",";
        weatherMessage += "气温:"+ temperatureHigh +"-"+ temperatureLow + "摄氏度,";
        weatherMessage += pollution+"污染,";
        weatherMessage += "适宜穿"+cy_tip;
        weatherMessage += zwx_tip + ",";
        weatherMessage += ssd_tip;
        weatherMessage += gm_tip ;
        weatherMessage += yd_tip;
        return weatherMessage;
    }

    private void getInfoFromNet(){
        if(location == null || location.equals(""))
            return;
        String link_head ="http://php.weather.sina.com.cn/xml.php?city=";
        String city=null;
        try {
            city = URLEncoder.encode(location, "GB2312");
            String link_tail ="&password=DJOYnieT8234jlsK&day=0";
            String link = link_head+city+link_tail;
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            InputStream in =urlConnection.getInputStream();
            Log.d(TAG, "getInfoFromNet: "+in);
            if(in.equals(""))
                return;
            XmlWeatherParser parser = new XmlWeatherParser(in);
            String[] nodes = {"status1","temperature1","temperature2","pollution_l","chy_l","zwx_s","ssd_s","gm_s","yd_s"};
            Map<String, String> map = parser.getValue(nodes);
            if(map.isEmpty())
                return;
            status = map.get(nodes[0]);
            temperatureHigh = map.get(nodes[1]);
            temperatureLow = map.get(nodes[2]);
            pollution = map.get(nodes[3]);
            cy_tip = map.get(nodes[4]);
            zwx_tip = map.get(nodes[5]);
            ssd_tip = map.get(nodes[6]);
            gm_tip = map.get(nodes[7]);
            yd_tip = map.get(nodes[8]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 解析xml文档，包括本地文档和url
     * @author cyxl
     * @version 1.0 2012-05-24
     * @since 1.0
     *
     */
    public class XmlWeatherParser {
        InputStream inStream;
        Element root;

        public InputStream getInStream() {
            return inStream;
        }

        public void setInStream(InputStream inStream) {
            this.inStream = inStream;
        }

        public Element getRoot() {
            return root;
        }

        public void setRoot(Element root) {
            this.root = root;
        }

        public XmlWeatherParser() {
        }

        public XmlWeatherParser(InputStream inStream) {
            if (inStream != null) {
                this.inStream = inStream;
                DocumentBuilderFactory domfac = DocumentBuilderFactory
                        .newInstance();
                try {
                    DocumentBuilder domBuilder = domfac.newDocumentBuilder();
                    Document doc = domBuilder.parse(inStream);
                    root = doc.getDocumentElement();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public XmlWeatherParser(URL url) {
            InputStream inStream = null;
            try {
                inStream = url.openStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (inStream != null) {
                this.inStream = inStream;
                DocumentBuilderFactory domfac = DocumentBuilderFactory
                        .newInstance();
                try {
                    DocumentBuilder domBuilder = domfac.newDocumentBuilder();
                    Document doc = domBuilder.parse(inStream);
                    root = doc.getDocumentElement();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         *
         * @param nodes
         * @return 单个节点多个值以分号分隔
         */
        public Map<String, String> getValue(String[] nodes) {
            if (inStream == null || root==null) {
                return null;
            }
            Map<String, String> map = new HashMap<String, String>();
            // 初始化每个节点的值为null
            for (int i = 0; i < nodes.length; i++) {
                map.put(nodes[i], null);
            }

            // 遍历第一节点
            NodeList topNodes = root.getChildNodes();
            if (topNodes != null) {
                for (int i = 0; i < topNodes.getLength(); i++) {
                    Node book = topNodes.item(i);
                    if (book.getNodeType() == Node.ELEMENT_NODE) {
                        for (int j = 0; j < nodes.length; j++) {
                            for (Node node = book.getFirstChild(); node != null; node = node
                                    .getNextSibling()) {
                                if (node.getNodeType() == Node.ELEMENT_NODE)
                                    if (node.getNodeName().equals(nodes[j])) {
                                        //String val=node.getFirstChild().getNodeValue();
                                        String val = node.getTextContent();
                                        //System.out.println(nodes[j] + ":" + val);
                                        // 如果原来已经有值则以分号分隔
                                        String temp = map.get(nodes[j]);
                                        if (temp != null && !temp.equals("")) {
                                            temp = temp + ";" + val;
                                        } else {
                                            temp = val;
                                        }
                                        map.put(nodes[j], temp);
                                    }
                            }
                        }
                    }
                }
            }
            return map;
        }

    }

}
