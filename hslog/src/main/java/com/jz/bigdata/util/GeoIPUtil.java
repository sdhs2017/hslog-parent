package com.jz.bigdata.util;

import com.jz.bigdata.business.logAnalysis.log.entity.IPEntity;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @program: hsgit
 * @description: 服务于将IP地址转化为具体地址信息，包括国家、城市、经纬度等
 * @author: jiyourui
 * @create: 2019-12-23 14:07
 **/
public class GeoIPUtil {

    private static Logger logger = LoggerFactory.getLogger(GeoIPUtil.class);

    /**
     * 全局静态变量，DatabaseReader，保证类加载时加载一次
     */
    private static DatabaseReader reader;

    /**
     * 静态代码块，保证项目启动只获取一次文件
     */
    static {

        File database = null;

        try {
            //绝对路径读取文件方式
            database = new File("D:\\Computer_Science\\maxmind-geo\\GeoLite2-City_20191217\\GeoLite2-City.mmdb");

            // 通过 InputStream 流式读取文件，目的解决无法通过File方式读取jar包内的文件的问题·1
            //database = getFile("/GeoLite2-City.mmdb","geolite2.mmdb");
            logger.info("-------加载文件");
            reader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取classpath下的文件
     * @param fileName 原文件全名
     * @param newFileName  缓存的新文件的名称
     * @return
     * @throws IOException
     */
    public static File getFile(String fileName, String newFileName) throws IOException {
        //读取 ClassPath 路径下指定资源的输入流
        /*ClassPathResource resource = new ClassPathResource(fileName);
        InputStream inputStream = resource.getInputStream();*/
        InputStream inputStream = GeoIPUtil.class.getResourceAsStream(fileName);

        File file = new File(newFileName);

        inputstreamToFile(inputStream, file);

        return file;
    }

    /**
     * InputStream -> File
     * @param inputStream
     * @param file
     */
    private static void inputstreamToFile(InputStream inputStream,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String country;
    private String province;
    private String city_name;
    private String locations;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public GeoIPUtil(String ip) throws IOException, GeoIp2Exception {
        //GeoIP2-City 数据库文件D
        //File database = new File("D:\\Computer_Science\\maxmind-geo\\GeoLite2-City_20191217\\GeoLite2-City.mmdb");
        File database = new File("/home/elsearch/tmp/GeoLite2-City.mmdb");
        // 创建 DatabaseReader对象
        DatabaseReader reader = null;
        try {
            reader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // 获取查询结果
        CityResponse response = reader.city(ipAddress);

        // 获取国家信息
        Country country = response.getCountry();
        /*System.out.println("国家code:"+country.getIsoCode());
        System.out.println("国家:"+country.getNames().get("zh-CN"));*/

        this.country = country.getNames().get("zh-CN");


        // 获取省份
        Subdivision subdivision = response.getMostSpecificSubdivision();
        /*System.out.println("省份code:"+subdivision.getIsoCode());
        System.out.println("省份:"+subdivision.getNames().get("zh-CN"));*/

        this.province = subdivision.getNames().get("zh-CN");

        //城市
        City city = response.getCity();
        /*System.out.println("城市code:"+city.getGeoNameId());
        System.out.println("城市:"+city.getName());*/

        // 当省份为直辖市时，city.getName()会触发空指针，将city设为省份名称
        try {
            this.city_name = city.getName();
        }catch (Exception e){
            this.city_name = subdivision.getNames().get("zh-CN");
        }

        // 获取城市
        Location location = response.getLocation();
        /*System.out.println("经度:"+location.getLatitude());
        System.out.println("维度:"+location.getLongitude());*/

        this.locations = location.getLatitude()+","+location.getLongitude();
    }

    /**
     * 解析IP
     * @param ip
     * @return
     */
    public static IPEntity getIPMsg(String ip) throws IOException, GeoIp2Exception {

        IPEntity msg = new IPEntity();

        InetAddress ipAddress = InetAddress.getByName(ip);
        // 获取查询结果
        CityResponse response = reader.city(ipAddress);
        // 获取国家信息
        Country country = response.getCountry();
        // 获取省份
        Subdivision subdivision = response.getMostSpecificSubdivision();
        // 获取城市
        City city = response.getCity();
        // 获取邮政编码
        Postal postal = response.getPostal();
        // 获取经纬度，纬度在前，经度在后
        Location location = response.getLocation();

        msg.setCountryName(country.getNames().get("zh-CN"));
        msg.setCountryCode(country.getIsoCode());
        msg.setProvinceName(subdivision.getNames().get("zh-CN"));
        msg.setProvinceCode(subdivision.getIsoCode());
        msg.setCityName(city.getNames().get("zh-CN"));
        msg.setPostalCode(postal.getCode());
        //经度
        msg.setLongitude(location.getLongitude());
        //纬度
        msg.setLatitude(location.getLatitude());
        if (location.getLatitude()!=null&&location.getLongitude()!=null){
            msg.setLocations(location.getLatitude()+","+location.getLongitude());
        }



        return msg;
    }


    public static void main(String[] args) {

        /*try {
            IPEntity ipEntity = GeoIPUtil.getIPMsg("58.56.34.74");
            System.out.println(ipEntity.getCountryName()+","+ipEntity.getProvinceName()+","+ipEntity.getCityName());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*GeoIPUtil util = null;
        try {
            util = new GeoIPUtil("58.56.34.74");
        } catch (Exception e) {
            //e.printStackTrace();
        }

        System.out.println("国家:"+util.getCountry()+"  省份:"+util.getProvince()+"  城市："+util.getCity_name()+"  经纬度："+util.getLocations());*/

    }
}
