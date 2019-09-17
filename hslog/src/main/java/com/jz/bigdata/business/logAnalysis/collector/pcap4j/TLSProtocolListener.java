package com.jz.bigdata.business.logAnalysis.collector.pcap4j;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;


public class TLSProtocolListener extends ProtocolListener {
	public class TLSRecord
	{
		public String content_type;
		public String version;
		public int length;
		public ArrayList<Handshake> handshakes;
		public Alert alert;
		public ChangeCipherSpec ccs;
		public Application application;
	}
	public class Handshake
	{
		public String Message_types;
		public int Message_types_int;
		public int length;
		public byte[] message_data;
		public ClientHello clientHello;
		public ServerHello serverHello;
	}
	public class Alert
	{
		public String Message_types;
		public int Message_types_int;
		public String Level_types;
		public int Level_types_int;
		public String Description_types;
		public int Description_types_int;
	}
	public class ChangeCipherSpec 
	{
		public int type;
	}
	
	public class Application 
	{
		public byte[] data;
	}
	
	public class ProtocolVersion
	{
		public int major;
	    public int minor;
	}
	public class Random
	{
		public long gmt_unix_time;
		public byte[] random_bytes = new byte[28];
		public String hex;
	}
	public class SessionID
	{
		public int length;
		public byte[] bytes;
		public String hex;
	}
	public class CipherSuite
	{
		public int cipher_suite;
		public String cipher_suite_string;
		
		public void value()
		{
			cipher_suite_string = Cases.CipherSuite(cipher_suite);
			
		}
	
	}
	public class CompressionMethod
	{
		public int method;
	
	}
	public class Extension
	{
		public int extension_type;
		public int length;
		public byte[] extension_data;
		public String extension_type_string;
		
		public void typeValue()
		{
			
			extension_type_string = Cases.Extension(extension_type);
		}
	}
	
	public class ClientHello
	{
		public ProtocolVersion client_version;
		public Random random;
		public SessionID session_id;
		public List<CipherSuite> cipher_suites;
		public List<CompressionMethod> compression_methods;
		public List<Extension> extensions;
	}
	
	public class ServerHello
	{
		public ProtocolVersion server_version;
		public Random random;
		public SessionID session_id;
		public CipherSuite cipher_suite;
		public CompressionMethod compression_methods;
		public List<Extension> extensions;
	}
	
	@Override
	public String onSend(String seq, byte[] data) {
		super.onSend(seq, data);
		List<TLSRecord> tls = handleTlsList(data);
//		String out = JSON.toJSONString(tls);
//		System.out.println("on send "+data.length+" tls:"+out);
		return tls.get(0).version;
	}

	@Override
	public String onRecv(String seq, byte[] data) {
		super.onRecv(seq, data);
		List<TLSRecord> tls = handleTlsList(data);
//		String out = JSON.toJSONString(tls);
//		System.out.println("onrecv "+data.length+" tls:"+out);
		return tls.get(0).version;
	}
	
	public List<TLSRecord> handleTlsList(byte[] data)
	{
		 int offset = 0;
		 int ysize = data.length;
		 List<TLSRecord> tlslist = new ArrayList<TLSRecord>();
		 while(ysize>5)
		 {
			 try{
			 TLSRecord tls = handleTls(data,offset);
			 if(tls!=null)
			 {
				 offset += tls.length+5;
				 ysize =ysize - tls.length-5;
				 
				 tlslist.add(tls);
			 }
			 else
			 {
				 break;
			 }
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				 break;
			 }
		 }
		 return tlslist;
	}
	public TLSRecord handleTls(byte[] data,int offset)
	{
		TLSRecord tls = new TLSRecord();
		int init = 0 + offset; 
		int content_type = bytesToIntTlsRecord1(data,init); //data[0+offset];
		init +=1;
		int major = bytesToIntTlsRecord1(data,1+offset); //data[1+offset];
		init +=1;
		int minor = bytesToIntTlsRecord1(data,2+offset);// data[2+offset];
		init +=1;
		int length=bytesToIntTlsRecord2(data,init);
		init +=2;
		tls.content_type = Cases.TLSContentType(content_type);
		
		tls.version = Cases.TLSVersion(minor);
		
		tls.length = length;
		
		if(content_type == 22)
		{
			tls.handshakes = new ArrayList<Handshake>();
			int count = 0;
			while(count<tls.length)
			{
			Handshake hs = new Handshake();
			hs.Message_types_int = bytesToIntTlsRecord1(data,init);// data[init];
			init +=1;
			hs.length = bytesToIntTlsRecord3(data,init);
			init +=3;
			hs.message_data = new byte[hs.length];
			if(hs.length ==0 ||hs.length>data.length-(init+count))
			{
				return tls;
			}
			System.arraycopy(data,init+count,hs.message_data,0,hs.length);
			count +=(4+ hs.length);
//			String out = new String(hs.message_data);
//			System.out.println("message:"+out);
			switch(hs.Message_types_int)
			{
				case 0:
				{
					hs.Message_types = "HelloRequest";
					break;
				}
				case 1:
				{
					int clientInit = 0;
					hs.Message_types = "ClientHello";
					hs.clientHello = new ClientHello();
					hs.clientHello.client_version = new ProtocolVersion();
					hs.clientHello.client_version.major = bytesToIntTlsRecord1(hs.message_data,0);// hs.message_data[0];
					hs.clientHello.client_version.minor = bytesToIntTlsRecord1(hs.message_data,1);// hs.message_data[1];
					clientInit =2;
					hs.clientHello.random = new Random();
					hs.clientHello.random.gmt_unix_time = bytesTolong(hs.message_data,2);
					hs.clientHello.random.random_bytes = new byte[28];
					byte[] byte32 = new byte[32];
					System.arraycopy(hs.message_data,clientInit,byte32,0,32);
					hs.clientHello.random.hex = byteToHexString(byte32);
					clientInit +=4;
					System.arraycopy(hs.message_data,clientInit,hs.clientHello.random.random_bytes,0,28);
					clientInit += 28;
					hs.clientHello.session_id = new SessionID();
					hs.clientHello.session_id.length=bytesToIntTlsRecord1(hs.message_data,clientInit);// hs.message_data[clientInit];
					clientInit += 1;
					if(hs.clientHello.session_id.length>0)
					{
						hs.clientHello.session_id.bytes = new byte[hs.clientHello.session_id.length];
						
						System.arraycopy(hs.message_data,clientInit,hs.clientHello.session_id.bytes,0,hs.clientHello.session_id.length);
						hs.clientHello.session_id.hex = byteToHexString(hs.clientHello.session_id.bytes);
					}
					clientInit += hs.clientHello.session_id.length;
					int cipher_suites_length =bytesToIntTlsRecord2(hs.message_data,clientInit);
					clientInit +=2;
					hs.clientHello.cipher_suites = new ArrayList<CipherSuite>();
					if(cipher_suites_length%2==0)
					{
						int max = cipher_suites_length/2;
						for(int i=0;i<max;i+=2)
						{
							CipherSuite cs = new CipherSuite();
							cs.cipher_suite = bytesToIntTlsRecord2(hs.message_data,clientInit+i);
							cs.value();
							hs.clientHello.cipher_suites.add(cs);
						}
					}
					clientInit +=cipher_suites_length;
					int compression_methods_length= bytesToIntTlsRecord1(hs.message_data,clientInit);// hs.message_data[clientInit];
					clientInit +=1;
					if(compression_methods_length>0)
					{
						hs.clientHello.compression_methods = new ArrayList<CompressionMethod>();
						for(int i=0;i<compression_methods_length;i++)
						{
							CompressionMethod cm = new CompressionMethod();
							cm.method = bytesToIntTlsRecord1(hs.message_data,clientInit+i);// hs.message_data[clientInit+i];
							hs.clientHello.compression_methods.add(cm);
						}
					}
					clientInit +=compression_methods_length;
					int extensions_length= bytesToIntTlsRecord2(hs.message_data,clientInit);
					clientInit +=2;
					int extensionCount = 0;
					if(extensions_length>0)
					{
						hs.clientHello.extensions = new ArrayList<Extension>();
						while(extensionCount<extensions_length)
						{
							Extension ext = new Extension();
							ext.extension_type = bytesToIntTlsRecord2(hs.message_data,clientInit+extensionCount);
							ext.length = bytesToIntTlsRecord2(hs.message_data,clientInit+2+extensionCount);
							if(ext.length>0)
							{
								ext.extension_data = new byte[ext.length];
								System.arraycopy(hs.message_data,clientInit+4+extensionCount,ext.extension_data,0,ext.length);
							}
							ext.typeValue();
							hs.clientHello.extensions.add(ext);
							extensionCount += 2+2+ext.length;
						}
					}
					break;
				}
				case 2:
				{
					hs.Message_types = "ServerHello";
					
					int serverInit = 0;
					hs.serverHello = new ServerHello();
					hs.serverHello.server_version = new ProtocolVersion();
					hs.serverHello.server_version.major = bytesToIntTlsRecord1(hs.message_data,0);// hs.message_data[0];
					hs.serverHello.server_version.minor = bytesToIntTlsRecord1(hs.message_data,1);// hs.message_data[1];
					serverInit =2;
					hs.serverHello.random = new Random();
					hs.serverHello.random.gmt_unix_time = bytesTolong(hs.message_data,2);
					hs.serverHello.random.random_bytes = new byte[28];
					byte[] byte32 = new byte[32];
					System.arraycopy(hs.message_data,serverInit,byte32,0,32);
					hs.serverHello.random.hex = byteToHexString(byte32);
					serverInit +=4;
					System.arraycopy(hs.message_data,serverInit,hs.serverHello.random.random_bytes,0,28);
					hs.serverHello.session_id = new SessionID();
					serverInit += 28;
					hs.serverHello.session_id.length=bytesToIntTlsRecord1(hs.message_data,serverInit);// hs.message_data[serverInit];
					serverInit += 1;
					if(hs.serverHello.session_id.length>0)
					{
						hs.serverHello.session_id.bytes = new byte[hs.serverHello.session_id.length];
						 
						System.arraycopy(hs.message_data,serverInit,hs.serverHello.session_id.bytes,0,hs.serverHello.session_id.length);
						hs.serverHello.session_id.hex = byteToHexString(hs.serverHello.session_id.bytes);
					}
					hs.serverHello.cipher_suite = new CipherSuite();
					
					serverInit += hs.serverHello.session_id.length;
					hs.serverHello.cipher_suite.cipher_suite =bytesToIntTlsRecord2(hs.message_data,serverInit);
					hs.serverHello.cipher_suite.value();
					serverInit +=2;
					
					if(hs.message_data.length <= serverInit) break;
					
					hs.serverHello.compression_methods = new CompressionMethod();
					hs.serverHello.compression_methods.method=bytesToIntTlsRecord1(hs.message_data,serverInit);// hs.message_data[serverInit];
					serverInit +=1;
					
					if(hs.message_data.length <= serverInit) break;
					
					int extensions_length= bytesToIntTlsRecord2(hs.message_data,serverInit);
					serverInit +=2;
					int extensionCount = 0;
					if(extensions_length>0)
					{
						hs.serverHello.extensions = new ArrayList<Extension>();
						while(extensionCount<extensions_length)
						{
							Extension ext = new Extension();
							ext.extension_type = bytesToIntTlsRecord2(hs.message_data,serverInit+extensionCount);
							ext.length = bytesToIntTlsRecord2(hs.message_data,serverInit+2+extensionCount);
							if(ext.length>0)
							{
								ext.extension_data = new byte[ext.length];
								System.arraycopy(hs.message_data,serverInit+4+extensionCount,ext.extension_data,0,ext.length);
							}
							ext.typeValue();
							hs.serverHello.extensions.add(ext);
							extensionCount += 2+2+ext.length;
						}
					}
					
					break;
				}
				case 4:
				{
					hs.Message_types = "NewSessionTicket";
					break;
				}
				case 11:
				{
					hs.Message_types = "Certificate";
					break;
				}
				case 12:
				{
					hs.Message_types = "ServerKeyExchange";
					break;
				}
				case 13:
				{
					hs.Message_types = "CertificateRequest";
					break;
				}
				case 14:
				{
					hs.Message_types = "ServerHelloDone";
					break;
				}
				case 15:
				{
					hs.Message_types = "CertificateVerify";
					break;
				}
				case 16:
				{
					hs.Message_types = "ClientKeyExchange";
					break;
				}
				case 20:
				{
					hs.Message_types = "Finished";
					break;
				}
			}
			tls.handshakes.add(hs);
			}
		}else if(content_type == 21)
		{
			tls.alert = new Alert();
			tls.alert.Level_types_int= data[5];
			tls.alert.Description_types_int = data[6];
			switch(tls.alert.Level_types_int)
			{
				case 1:
				{
					tls.alert.Level_types = "warning";
					break;
				}
				case 2:
				{
					tls.alert.Level_types = "	fatal";
					break;
				}
				
			}
			switch(tls.alert.Description_types_int)
			{
				case 0:
				{
					tls.alert.Description_types = "Close notify";
					break;
				}
				case 10:
				{
					tls.alert.Description_types = "Unexpected message";
					break;
				}
				case 20:
				{
					tls.alert.Description_types = "Bad record MAC";
					break;
				}
				case 21:
				{
					tls.alert.Description_types = "Decryption failed";
					break;
				}
				case 22:
				{
					tls.alert.Description_types = "Record overflow";
					break;
				}
				case 30:
				{
					tls.alert.Description_types = "Decompression failure";
					break;
				}
				case 40:
				{
					tls.alert.Description_types = "Handshake failure";
					break;
				}
				case 41:
				{
					tls.alert.Description_types = "No certificate";
					break;
				}
				case 42:
				{
					tls.alert.Description_types = "Bad certificate";
					break;
				}
				case 43:
				{
					tls.alert.Description_types = "Unsupported certificate";
					break;
				}
				case 44:
				{
					tls.alert.Description_types = "	Certificate revoked";
					break;
				}
				case 45:
				{
					tls.alert.Description_types = "Certificate expired";
					break;
				}
				case 46:
				{
					tls.alert.Description_types = "	Certificate unknown";
					break;
				}
				case 47:
				{
					tls.alert.Description_types = "Illegal parameter";
					break;
				}
				case 48:
				{
					tls.alert.Description_types = "Unknown CA";
					break;
				}
				case 49:
				{
					tls.alert.Description_types = "Access denied";
					break;
				}
				case 50:
				{
					tls.alert.Description_types = "Decode error";
					break;
				}
				case 51:
				{
					tls.alert.Description_types = "Decrypt error";
					break;
				}
				case 60:
				{
					tls.alert.Description_types = "Export restriction";
					break;
				}
				case 70:
				{
					tls.alert.Description_types = "Protocol version";
					break;
				}
				case 71:
				{
					tls.alert.Description_types = "Insufficient security";
					break;
				}
				case 80:
				{
					tls.alert.Description_types = "Internal error";
					break;
				}
				case 86:
				{
					tls.alert.Description_types = "Inappropriate Fallback";
					break;
				}
				case 90:
				{
					tls.alert.Description_types = "User canceled";
					break;
				}
				case 100:
				{
					tls.alert.Description_types = "No renegotiation";
					break;
				}
				case 110:
				{
					tls.alert.Description_types = "Certificate unobtainable";
					break;
				}
				case 111:
				{
					tls.alert.Description_types = "Insufficient security";
					break;
				}
				case 112:
				{
					tls.alert.Description_types = "Unrecognized name";
					break;
				}
				case 113:
				{
					tls.alert.Description_types = "Bad certificate status response";
					break;
				}
				case 114:
				{
					tls.alert.Description_types = "Bad certificate hash value";
					break;
				}
				case 115:
				{
					tls.alert.Description_types = "Unknown PSK identity";
					break;
				}
				case 120:
				{
					tls.alert.Description_types = "No Application Protocol";
					break;
				}
			}
			
			
		}else if(content_type == 20)
		{
			tls.ccs = new ChangeCipherSpec();
			tls.ccs.type= data[5];
		}else if(content_type == 23)
		{
			tls.application = new Application();
			tls.application.data = new byte[tls.length];
			System.arraycopy(data,5,tls.application.data,0,tls.length);
			
		}
		
		return tls;
	}
	public static int bytesToIntTlsRecord1(byte[] src,int offset) {  
	    int value;    
	    value = (int) (src[offset+0] & 0xFF);  
	    return value;  
	}
	public static int bytesToIntTlsRecord2(byte[] src,int offset) {  
	    int value;    
	    value = (int) ((src[offset+1] & 0xFF)   
	            | ((src[offset+0] & 0xFF)<<8));  
	    return value;  
	}
	public static int bytesToIntTlsRecord3(byte[] src,int offset) {  
	    int value;    
	    value = (int) ((src[offset+2] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)
	            | ((src[offset+0] & 0xFF)<<16));  
	    return value;  
	}
	public static int bytesToIntTlsRecord4(byte[] src,int offset) {  
	    int value;    
	    value = (int) ((src[offset+3] & 0xFF)   
	            | ((src[offset+2] & 0xFF)<<8)
	            | ((src[offset+1] & 0xFF)<<16)
	            | ((src[offset+0] & 0xFF)<<24));  
	    return value;  
	}
	public static long bytesTolong(byte[] src, int offset) {  
	    long value;    
	    value = (long) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)   
	            | ((src[offset+2] & 0xFF)<<16)   
	            | ((src[offset+3] & 0xFF)<<24));  
	    return value;  
	}
	public static int bytesToint2(byte[] src, int offset) {  
	    int value;    
	    value = (int) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8));  
	    return value;  
	}
	public static String byteToHexString(byte[] bArray) {
	    int offset = 0;
	    int len = bArray.length;
	    StringBuffer sb = new StringBuffer(len);
	    String sTemp;
	    for (int i = offset; i < offset + len; i++) {
	        sTemp = Integer.toHexString(0xFF & bArray[i]);
	        if (sTemp.length() < 2)
	           sb.append(0);
	           sb.append(sTemp);
	        }
	    return sb.toString();
	}
 
	public static byte[] hexToByteArray(String hexString) {
	    int len = hexString.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4 )+Character.digit(hexString.charAt(i + 1), 16));
	    }
	    return data;
	}




}
