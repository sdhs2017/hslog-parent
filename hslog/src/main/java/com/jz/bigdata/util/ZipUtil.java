package com.jz.bigdata.util;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.lang.StringUtils;

import java.io.*;

/**
 * @Author: yiyang
 * @Date: 2021/10/14 9:41
 * @Description: 将文件压缩成zip文件，并设置密码
 */
@Slf4j
public class ZipUtil {
    /**
     * 将一个文件夹进行压缩，并设置密码
     * @param srcFilePath 要压缩的文件夹路径
     * @param zipFilePathName 压缩后的文件路径及名称 eg /home/test.zip
     * @param password 压缩包的密码
     * @throws Exception
     */
    public static boolean zipFilesAndEncrypt(String srcFilePath,String zipFilePathName,String password) {
        try{
            // 生成的压缩文件
            ZipFile zipFile = new ZipFile(zipFilePathName);
            ZipParameters parameters = new ZipParameters();
            // 压缩方式
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            // 压缩级别
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            //设置密码
            parameters.setEncryptFiles( true );
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password);
            // 要打包的文件夹
            File currentFile = new File(srcFilePath);
            File[] fs = currentFile.listFiles();
            // 遍历文件夹下所有的文件、文件夹
            for (File f : fs) {
                if (f.isDirectory()) {
                    zipFile.addFolder(f.getPath(), parameters);
                } else {
                    zipFile.addFile(f, parameters);
                }
            }
            return true;
        }catch (Exception e){
            log.error("生成压缩包失败:"+e.getMessage());
            return false;
        }

    }
}
