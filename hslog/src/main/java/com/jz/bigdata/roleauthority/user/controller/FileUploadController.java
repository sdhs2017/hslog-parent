package com.jz.bigdata.roleauthority.user.controller;
import java.io.File;  
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.license.LicenseExtra;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.DruidUtil;
import com.jz.bigdata.util.SpringUtils;
import de.schlichtherle.license.LicenseContent;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jz.bigdata.common.license.VerifyLicense;
import com.jz.bigdata.util.DescribeLog;  
  
/** 
 * 文件上传测试类 
 */
@Slf4j
@Controller  
@RequestMapping("/upload")  
public class FileUploadController {

    @Resource(name = "UserService")
    private IUserService userService;
    @Resource(name = "licenseService")
	private VerifyLicense verifyLicense;
	
    @ResponseBody  
    @RequestMapping(value="uploadFile")  
    @DescribeLog(describe="文件上传")
    public void testUpload(MultipartHttpServletRequest request) throws IOException {  
        /* 
         * MultipartHttpServletRequest: 继承于HttpServletRequest以及MultipartRequest. 
         * 其中MultipartRequest中定义了相关的访问操作. MultipartHttpServletRequest重写 
         * 了HttpServletRequest中的方法, 并进行了扩展. 如果以HttpServletRequest来接收参 
         * 数, 则需要先将其转为MultipartHttpServletRequest类型 
         * MultipartHttpServletRequest request = (MultipartHttpServletRequest) HttpServletRequest; 
         */  
          
        /* 
         * 再说回刚才的form, 假设我们在单个文件选框中上传了文件1, 多个文件选框中上传了文件2, 3, 4. 
         * 那么对于后台接收到的, 可以这么理解, 就是一个Map的形式(实际上它后台真的是以Map来存储的). 
         * 这个Map的Key是什么呢? 就是上面<input>标签中的name=""属性. Value则是我们刚才上传的 
         * 文件, 通过下面的示例可以看出每一个Value就是一个包含对应文件集合的List 
         *  
         * 传到后台接收到的Map就是这样: 
         * fileTest: 文件1 
         * fileList: 文件2, 文件3, 文件4 
         *  
         * 虽然从方法名的表面意义来看是得到文件名, 但实际上这个文件名跟上传的文件本身并没有什么关系. 
         * 刚才说了这个Map的Key就是<input>标签中的name=""属性, 所以得到的也就是这个属性的值 
         */  
        Iterator<String> fileNames = request.getFileNames();  
          
        while (fileNames.hasNext()) {  
              
            //把fileNames集合中的值打出来  
            String fileName=fileNames.next();  
            System.out.println("fileName: "+fileName);  
              
            /* 
             * request.getFiles(fileName)方法即通过fileName这个Key, 得到对应的文件 
             * 集合列表. 只是在这个Map中, 文件被包装成MultipartFile类型 
             */  
            List<MultipartFile> fileList=request.getFiles(fileName);  
              
            if (fileList.size()>0) {  
                  
                //遍历文件列表  
                Iterator<MultipartFile> fileIte=fileList.iterator();  
                  
                while (fileIte.hasNext()) {  
                      
                    //获得每一个文件  
                    MultipartFile multipartFile=fileIte.next();  
                      
                    //获得原文件名  
                    String originalFilename = multipartFile.getOriginalFilename();  
                    System.out.println("originalFilename: "+originalFilename);  
                      
                    //设置保存路径.   
                    String path ="D:/testUpload/";  
                      
                    //检查该路径对应的目录是否存在. 如果不存在则创建目录  
                    File dir=new File(path);  
                    if (!dir.exists()) {  
                        dir.mkdirs();  
                    }  
                      
                    String filePath = path + originalFilename;  
                    System.out.println("filePath: "+filePath);  
                      
                    //保存文件  
                    File dest = new File(filePath);  
                    if (!(dest.exists())) {  
                        /* 
                         * MultipartFile提供了void transferTo(File dest)方法, 
                         * 将获取到的文件以File形式传输至指定路径. 
                         */  
                        multipartFile.transferTo(dest);  
                          
                        /* 
                         * 如果需对文件进行其他操作, MultipartFile也提供了 
                         * InputStream getInputStream()方法获取文件的输入流 
                         *  
                         * 例如下面的语句即为通过 
                         * org.apache.commons.io.FileUtils提供的 
                         * void copyInputStreamToFile(InputStream source, File destination) 
                         * 方法, 获取输入流后将其保存至指定路径 
                         */  
                        //FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), dest);  
                    } else {
						if(dest.delete()) {
							multipartFile.transferTo(dest);
						}
					} 
                      
                    //MultipartFile也提供了其他一些方法, 用来获取文件的部分属性  
                      
                    //获取文件contentType  
                    String contentType=multipartFile.getContentType();  
                    System.out.println("contentType: "+contentType);  
                      
                    /* 
                     * 获取name 
                     * 其实这个name跟上面提到的getFileName值是一样的, 
                     * 就是Map中Key的值. 即前台页面<input>中name="" 
                     * 属性. 但是上面的getFileName只是得到这个Map的Key, 
                     * 而Spring在处理上传文件的时候会把这个值以name属性 
                     * 记录到对应的每一个文件. 如果需要从文件层面获取这个 
                     * 值, 则可以使用该方法  
                     */  
                    String name=multipartFile.getName();  
                    System.out.println("name: "+name);  
                      
                    //获取文件大小, 单位为字节  
                    long size=multipartFile.getSize();  
                    System.out.println("size: "+size);  
                      
                    System.out.println("---------------------------------------------------");  
                }  
            }  
        }  
    }  
    
    @ResponseBody  
    @RequestMapping(value="licenseUpload")  
    @DescribeLog(describe="证书上传")
    public String licenseUpload(MultipartHttpServletRequest request) {
        
        Iterator<String> fileNames = request.getFileNames();  
          
        while (fileNames.hasNext()) {  
              
            //把fileNames集合中的值打出来  
            String fileName=fileNames.next();  
//            System.out.println("fileName: "+fileName);
              
            /* 
             * request.getFiles(fileName)方法即通过fileName这个Key, 得到对应的文件 
             * 集合列表. 只是在这个Map中, 文件被包装成MultipartFile类型 
             */  
            List<MultipartFile> fileList=request.getFiles(fileName);  
              
            if (fileList.size()>0) {  
                  
                //遍历文件列表  
                Iterator<MultipartFile> fileIte=fileList.iterator();  
                  
                while (fileIte.hasNext()) {  
                      
                    //获得每一个文件  
                    MultipartFile multipartFile=fileIte.next();  
                      
                    //获得原文件名  
                    String originalFilename = multipartFile.getOriginalFilename();  
//                    System.out.println("originalFilename: "+originalFilename);
                      
                    //设置保存路径.   
                    String path =getClass().getClassLoader().getResource("").getFile();
//                    System.out.println(path);
                      
                    //检查该路径对应的目录是否存在. 如果不存在则创建目录  
                    File dir=new File(path);  
                    if (!dir.exists()) {  
                        dir.mkdirs();  
                    }  
                      
                    String filePath = path + originalFilename;  
//                    System.out.println("filePath: "+filePath);
                      
                    //保存文件  
                    File dest = new File(filePath);  
                    if (!(dest.exists())) {  
                        /* 
                         * MultipartFile提供了void transferTo(File dest)方法, 
                         * 将获取到的文件以File形式传输至指定路径. 
                         */
                    	try {
                    		multipartFile.transferTo(dest);  
						} catch (Exception e) {
							// TODO: handle exception
							return "证书上传失败！";
						}
                        
                          
                        /* 
                         * 如果需对文件进行其他操作, MultipartFile也提供了 
                         * InputStream getInputStream()方法获取文件的输入流 
                         *  
                         * 例如下面的语句即为通过 
                         * org.apache.commons.io.FileUtils提供的 
                         * void copyInputStreamToFile(InputStream source, File destination) 
                         * 方法, 获取输入流后将其保存至指定路径 
                         */  
                        //FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), dest);  
                    } else {
						if(dest.delete()) {
							try {
								multipartFile.transferTo(dest);
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "证书上传失败！";
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "证书上传失败！";
							}
						}
					} 
                      
                    //MultipartFile也提供了其他一些方法, 用来获取文件的部分属性  
                      
                    //获取文件contentType  
                    String contentType=multipartFile.getContentType();  
//                    System.out.println("contentType: "+contentType);
                      
                    /* 
                     * 获取name 
                     * 其实这个name跟上面提到的getFileName值是一样的, 
                     * 就是Map中Key的值. 即前台页面<input>中name="" 
                     * 属性. 但是上面的getFileName只是得到这个Map的Key, 
                     * 而Spring在处理上传文件的时候会把这个值以name属性 
                     * 记录到对应的每一个文件. 如果需要从文件层面获取这个 
                     * 值, 则可以使用该方法  
                     */  
                    String name=multipartFile.getName();  
//                    System.out.println("name: "+name);
                      
                    //获取文件大小, 单位为字节  
                    long size=multipartFile.getSize();  
//                    System.out.println("size: "+size);
                      
                }  
            }  
        }
        //证书上传后，根据证书信息初始化用户及菜单信息
//        try{
//            userService.updateProduct();
//        }catch(Exception e){
//            log.error("产品激活失败:"+e.getMessage());
//            return "产品激活失败！";
//        }
        return "产品激活成功";

    } 
    
    @ResponseBody  
    @RequestMapping(value="licenseVerify")  
    @DescribeLog(describe="证书上传")
    public boolean license(MultipartHttpServletRequest request) throws IOException {
    	verifyLicense.setParam("/verifyparam.properties");
		//验证证书
		Boolean vresult = verifyLicense.verify();
		return vresult;
    }
}  