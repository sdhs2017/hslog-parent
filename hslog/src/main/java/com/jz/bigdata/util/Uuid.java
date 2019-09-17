package com.jz.bigdata.util;

import java.util.UUID;

/**
 * @author shichengyu
 * @date 2017年8月1日 下午2:44:30
 * @description
 */
public class Uuid {
	  /**
	   * 随机生成UUID
	   * @return
	   */
	  public static synchronized String getUUID(){
	    UUID uuid=UUID.randomUUID();
	    String str = uuid.toString(); 
	    String uuidStr=str.replace("-", "");
	    return uuidStr;
	  }


}
