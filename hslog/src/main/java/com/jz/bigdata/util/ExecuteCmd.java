package com.jz.bigdata.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author jiyourui
 * java 方式执行系统命令
 */
public class ExecuteCmd {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String LINE_SEPARATO = "\\t";
	
	/**
	 * 执行命令行，返回执行结果
	 * @param cmd 命令行
	 * @return
	 */
	public static Map<String, String> execCmd(String cmd) {
		
		Map<String, String> result = new HashMap<>() ;
		BufferedReader bufrIn = null;
		//BufferedReader bufrError = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			
			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			//bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

			StringBuilder stringBuffer = new StringBuilder();
			// 读取输出
			String line = null;
			while ((line = bufrIn.readLine()) != null) {
				stringBuffer.append(line).append('\n');
				System.out.println(line);
			}
			/*while ((line = bufrError.readLine()) != null) {
				stringBuffer.append(line).append('\n');
			}*/
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			result.put(cmd, stringBuffer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
			result.put(cmd, cmd+"命令行为空");
		} finally {
			closeStream(bufrIn);
			//closeStream(bufrError);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;
		
	}
	
	/**
	 * 执行命令行，返回执行结果
	 * @param cmd 命令行
	 * @param dir 执行命令进程需要的工作目录，null表示与当前主进程工作目录相同
	 * @return
	 */
	public Map<String, String> execCmd(String cmd,File dir) {
		
		Map<String, String> result = new HashMap<>();
		BufferedReader bufrIn = null;
		BufferedReader bufrError = null;
		Process process = null;
		
		try {
			process = Runtime.getRuntime().exec(cmd, null, dir);
			
			
			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

			StringBuilder stringBuffer = new StringBuilder();
			// 读取输出
			String line = null;
			while ((line = bufrIn.readLine()) != null) {
				System.out.println(line.trim());
				if (getSubUtil(line.trim(),"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")!=null&&getSubUtil(line.trim(),"Discovered")!=null) {
                	stringBuffer.append(line.trim()).append(LINE_SEPARATOR);
                	break;
				}
				stringBuffer.append(line).append('\n');
			}
			while ((line = bufrError.readLine()) != null) {
				stringBuffer.append(line).append('\n');
			}
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			result.put(cmd, stringBuffer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeStream(bufrIn);
			closeStream(bufrError);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;
	}
	
	/**
	 * 执行命令行，返回与关键字匹配的执行结果
	 * @param cmd 命令行
	 * @param dir 执行命令进程需要的工作目录，null表示与当前主进程工作目录相同
	 * @param rgex 单个需要匹配的内容
	 * @return
	 */
	public Map<String, String> execCmd(String cmd,File dir,String rgex) {
		
		Map<String, String> result = new HashMap<>();
		BufferedReader bufrIn = null;
		BufferedReader bufrError = null;
		Process process = null;
		
		try {
			process = Runtime.getRuntime().exec(cmd, null, dir);
			
			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

			StringBuilder stringBuffer = new StringBuilder();
			// 读取输出
			String line = null;
			while ((line = bufrIn.readLine()) != null) {
				// 匹配需要的
				if (getSubUtil(line.trim(),rgex)!=null) {
                	stringBuffer.append(line.trim()).append(LINE_SEPARATOR);
                	break;
				}
			}
			while ((line = bufrError.readLine()) != null) {
				stringBuffer.append(line).append('\n');
			}
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			result.put(cmd, stringBuffer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeStream(bufrIn);
			closeStream(bufrError);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;
	}
	
	/**
	 * 执行命令行，返回与关键字匹配的执行结果
	 * @param cmd 命令行
	 * @param dir 执行命令进程需要的工作目录，null表示与当前主进程工作目录相同
	 * @param rgexs 多个需要匹配的内容
	 * @param isUnion 多个关键字是否全匹配，true全匹配，false包含一个即可
	 * @return
	 */
	public static Map<String, String> execCmd(String cmd,File dir,String [] rgexs,boolean isUnion) {
		
		Map<String, String> result = new HashMap<>();
		BufferedReader bufrIn = null;
		Process process = null;
		
		try {
			process = Runtime.getRuntime().exec(cmd, null, dir);
			
			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

			StringBuilder stringBuffer = new StringBuilder();
			// 读取输出
			String line = null;
			if (isUnion) {
				while ((line = bufrIn.readLine()) != null) {
					boolean union = true;
					for(String rgex : rgexs) {
						if (getSubUtil(line.trim(),rgex)==null) {
							union = false;
		                	break;
						}
					}
					if (union) {
						stringBuffer.append(line.trim()).append(LINE_SEPARATOR);
						break;
					}
				}
				
			}else {
				while ((line = bufrIn.readLine()) != null) {
					for(String rgex : rgexs) {
						if (getSubUtil(line.trim(),rgex)!=null) {
							stringBuffer.append(line.trim()).append(LINE_SEPARATOR);
		                	break;
						}
					}
				}
			}
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			result.put(cmd, stringBuffer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeStream(bufrIn);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;
		
	}
	
	/**
	 * 执行命令行，返回与关键字匹配的执行结果
	 * @param cmd 命令行
	 * @param dir 执行命令进程需要的工作目录，null表示与当前主进程工作目录相同
	 * @param rgexs 多个需要匹配的内容
	 * @return
	 */
	public static Map<String, Vector<String>> execCmd(String cmd,File dir,String [] rgexs) {
		
		Map<String, Vector<String>> result = new HashMap<>();
		Vector<String> vector = new Vector<String>();
		BufferedReader bufrIn = null;
		Process process = null;
		
		try {
			process = Runtime.getRuntime().exec(cmd, null, dir);
			
			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

			// 读取输出
			String line = null;
			while ((line = bufrIn.readLine()) != null) {
				//stringBuffer.append(line.trim()).append(LINE_SEPARATO);
				String ports = getSubUtilSimple(line.trim(), "port\\s+(.*?)[/]");
				String ip = getSubUtil(cmd,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				vector.add(ip+":"+ports);
			}
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			result.put("masscan"+getSubUtil(cmd,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"), vector);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeStream(bufrIn);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;
		
	}
	
	/**
	 * 执行命令行，返回与关键字匹配的执行结果
	 * @param cmd 命令行
	 * @param filepath 执行命令进程需要的工作目录，null表示与当前主进程工作目录相同
	 * @return
	 */
	public static Map<String, Set<String>> execCmd(String cmd,String filepath) {
		
		Map<String, Set<String>> result = new HashMap<>();
		Set<String> list = new HashSet<String>();
		BufferedReader bufrIn = null;
		Process process = null;
		File file = null;
		
		if(filepath!=null&&!filepath.equals("")) {
			file = new File(filepath);
		}
		
		try {
			process = Runtime.getRuntime().exec(cmd, null, file);
			
			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

			StringBuilder stringBuffer = new StringBuilder();
			// 读取输出
			String line = null;
			while ((line = bufrIn.readLine()) != null) {
				//stringBuffer.append(line.trim()).append(LINE_SEPARATO);
				String ports = getSubUtilSimple(line.trim(), "port\\s+(.*?)[/]");
				String ip = getSubUtil(cmd,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				list.add(ip+"_"+ports);
			}
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			//result.put("masscan"+getSubUtil(cmd,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"), stringBuffer.toString());
			result.put("masscan"+getSubUtil(cmd,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"), list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeStream(bufrIn);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;
		
	}

	/**
	 * 执行shell命令行，返回与关键字匹配的执行结果
	 * @param shell 命令行
	 * @param filepath 执行命令进程需要的工作目录，null表示与当前主进程工作目录相同
	 * @return
	 */
	public static Map<String, Set<String>> execShell(String shell,String filepath) {

		Map<String, Set<String>> result = new HashMap<>();
		Set<String> list = new HashSet<String>();
		BufferedReader bufrIn = null;
		Process process = null;
		File file = null;

		if(filepath!=null&&!filepath.equals("")) {
			file = new File(filepath);
		}

		try {
			process = Runtime.getRuntime().exec(shell, null, file);

			bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

			StringBuilder stringBuffer = new StringBuilder();
			// 读取输出
			String line = null;
			while ((line = bufrIn.readLine()) != null) {
				//stringBuffer.append(line.trim()).append(LINE_SEPARATO);
				String ports = getSubUtilSimple(line.trim(), "port\\s+(.*?)[/]");
				String ip = getSubUtil(shell,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				list.add(ip+"_"+ports);
			}
			//每个命令存储自己返回数据-用于后续对返回数据进行处理
			//result.put("masscan"+getSubUtil(cmd,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"), stringBuffer.toString());
			result.put("masscan"+getSubUtil(shell,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"), list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			closeStream(bufrIn);
			// 销毁进程
			if (process!=null) {
				process.destroy();
			}
		}
		return result;

	}
	
	/**
	 * 关闭流
	 * @param stream
	 */
	private static void closeStream(Closeable stream) {
		if (stream!=null) {
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 正则匹配
	 * @param soap
	 * @param rgex
	 * @return 返回匹配的内容
	 */
 	public static String getSubUtil(String soap,String rgex){  
         Pattern pattern = Pattern.compile(rgex);// 匹配的模式  
         Matcher m = pattern.matcher(soap);  
         while(m.find()){
             return m.group(0);
         }  
         return "";  
    }
 	
 	/**
	 * 正则匹配
	 * @param soap
	 * @param rgex
	 * @return 返回中括号的内容
	 */
 	public static String getSubUtilSimple(String soap, String rgex) {
 		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
 		Matcher m = pattern.matcher(soap);
 		while (m.find()) {
 			return m.group(1);
 		}
 		return "";
 	}
 	
 	public static void main(String [] args) {
 		
 		/*String startIP = "2.16.8.0";
 		String [] ips = startIP.split(".");
 		ArrayList<String> list = new ArrayList<String>();
 		for(int i = 0;i<=255;i++ ) {
 			//Integer.valueOf(ips[3]);
 			list.add(ips[0]+"."+ips[1]+"."+ips[2]+"."+i);
 		}
		String[] rgexs = { "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "Discovered" };
		Map<String, String> result = ExecuteCmd.execCmd("D:/Computer_Science/IP探测/masscan/masscan 192.168.0.12 -p10-1000", null, rgexs, true);
		System.out.println(result);*/
 		ExecuteCmd.execCmd("nmap -p 443 192.168.0.11");
	}
}
