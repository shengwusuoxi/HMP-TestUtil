package main;
import halluo.CollectService;
import halluo.Message;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.Naming;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 采集服务器测试类
 *@author tlj 
 *
 */
public class HMP {  

	public static final String ClASS_PATH = System.getProperty("java.class.path") + File.separator;
	public static boolean help=false;
	public static boolean test=false;
	public static boolean loop=false;
	public static String testtype=null;
	public static int buildNum=10;
	public static String command=null;
	public static String url =null;
	public static String ip=null;
	public static int port=0;
	public static String brandTemplate=null;
	public static String assetId=null;
	public static String username=null;
	public static String password=null;
	public static String resurl=null;
	public static int timer=5;

	public static void execute(String[] args){
		// 	dotest();
		// 	command("listsize".toLowerCase());
		if(args.length != 0){
			command(args);
		}else{
			System.out.println("Command args is null!");
		}
	}  

	public static void command(String[] ncommand)
	{    
		getConnectionParameters(ncommand);
		if(test)
		{
			//           for(int i = 0; i < 10; i++){
			//        	   new Thread(new Runnable() {
			//				@Override
			//				public void run() {
			//					// TODO Auto-generated method stub
			//					dotest();
			//				}
			//			}).start();
			//           }
			dotest();       	  
		}
		else if(help)
		{
			printUsage();
		}else if(loop)
		{
			doLoop();       	  
		}
		else
		{
			excuteCommand(command);
		}
	}

	private static void excuteCommand(String command) {
		// TODO Auto-generated method stub
		try{  
			System.out.println("rmi://"+url+"/CollectService");
			CollectService collectService=(CollectService)Naming.lookup("rmi://"+url+"/CollectService"); 
			if(testtype != null && command.equals("buildcollector")){
				if(testtype.equals("local")){
					command="BuildCollectorLocal".toLowerCase();
				}else if(testtype.equals("localssh")){
					command="BuildCollectorSSHLocal".toLowerCase();
				}
			}
			Message msg = new Message();
			msg.setCode(command);
			msg.setIP(ip);
			msg.setBrandTemplate(brandTemplate);
			msg.setPort(port);
			msg.setAssetId(assetId);
			msg.setUsername(username);
			msg.setPassword(password);
			msg.setResURL(resurl);
			System.out.println( collectService.dowork(msg));
		}catch(Exception ex){  
			ex.printStackTrace();  
		}  	
	}

	private static void getConnectionParameters(String[] args)
			throws IllegalArgumentException {
		int ai = 0;
		String param = "";
		String val = "";
		while (ai < args.length) {
			param = args[ai].trim();
			if (ai + 1 < args.length) {
				val = args[ai + 1].trim();       
			}
			if (param.equalsIgnoreCase("--loop")) {
				loop = true;command="";
				if(!val.startsWith("--") && !val.isEmpty()){
					buildNum = Integer.parseInt(val);
				}else{
					break;
				}
			}else if (param.equalsIgnoreCase("--help")) {
				help = true;url="";command="";
				break;
			}else if (param.equalsIgnoreCase("--test")) {
				test=true;command="";
				if(!val.startsWith("--") && !val.isEmpty()){
					buildNum = Integer.parseInt(val);
				}else{
					break;
				}
			}else if (param.equalsIgnoreCase("--testtype") && !val.startsWith("--") && 
					!val.isEmpty()) {
				testtype = val;
			}else if (param.equalsIgnoreCase("--url") && !val.startsWith("--") &&
					!val.isEmpty()) {
				url = val;
			}else if (param.equalsIgnoreCase("--command") && !val.startsWith("--") &&
					!val.isEmpty()) {
				command = val;
			}else if (param.equalsIgnoreCase("--brandtemplate") && !val.startsWith("--") &&
					!val.isEmpty()) {
				brandTemplate = val;
			}else if (param.equalsIgnoreCase("--ip") && !val.startsWith("--") &&
					!val.isEmpty()) {
				ip = val;
			}else if (param.equalsIgnoreCase("--port") && !val.startsWith("--") &&
					!val.isEmpty()) {
				port =  Integer.parseInt(val);
			}else if (param.equalsIgnoreCase("--assetid") && !val.startsWith("--") &&
					!val.isEmpty()) {
				assetId =  val;
			}else if (param.equalsIgnoreCase("--username") && !val.startsWith("--") &&
					!val.isEmpty()) {
				username =  val;
			}else if (param.equalsIgnoreCase("--password") && !val.startsWith("--") &&
					!val.isEmpty()) {
				password =  val;
			}else if (param.equalsIgnoreCase("--resurl") && !val.startsWith("--") &&
					!val.isEmpty()) {
				resurl =  val;
			}else if (param.equalsIgnoreCase("--timer") && !val.startsWith("--") &&
					!val.isEmpty()) {
				timer =  Integer.parseInt(val);
			}
			val = "";
			ai += 2;  
		}

		if(url == null || command == null ) {   	 
			throw new IllegalArgumentException(
					"Expected --url, --command, arguments.");
		}
	}

	public static void printUsage() {
		String helpFileName = "采集服务器测试命令.txt";
		try {
			String source = readFile(helpFileName);
			System.out.println(source);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static  void Waitting(long time)
	{		
		long s_timeForDelay = System.currentTimeMillis();
		long s_timeForLimitFps;
		do
		{
			s_timeForLimitFps = System.currentTimeMillis();
		}
		while(s_timeForLimitFps < s_timeForDelay +time && s_timeForLimitFps >= s_timeForDelay);
	}

	public static  void dotest(){  
		test=false;
		url="rmi://"+url+"/CollectService";
		if(testtype == null){
			command="BuildCollector".toLowerCase();
		}else if(testtype.equals("local")){
			command="BuildCollectorLocal".toLowerCase();
		}else if(testtype.equals("localssh")){
			command="BuildCollectorSSHLocal".toLowerCase();
		}
		int i=buildNum;
		while(i-->0)
		{
			Waitting(50);
			try{  
				CollectService collectService=(CollectService)Naming.lookup(url);  
				Message msg = new Message();
				msg.setCode(command);
				msg.setIP("192.168.1."+i);
				if(brandTemplate != null && assetId != null){
					msg.setBrandTemplate(brandTemplate);
					msg.setAssetId(assetId);
				}else{
					msg.setBrandTemplate("HP_BLADE");
					msg.setAssetId("402880fb47f2734e0147f27862790002");
				}
				msg.setPort(22);
				msg.setUsername("fastech");
				msg.setPassword("123456");
				msg.setResURL("http://192.168.1.100:8080/hmp/resourceGetter_get.action");
				System.out.println(collectService.dowork(msg));
			}catch(Exception ex){  
				ex.printStackTrace();  
			}  
		}

	}  

	public static  void doLoop(){
		if(timer <= 0){
			excuteCommand("looponce");
		}else{
			excuteCommand("loopstart");
		}
		loop=false;
		url="rmi://"+url+"/CollectService";
		if(testtype == null){
			command="BuildCollector".toLowerCase();
		}else if(testtype.equals("local")){
			command="BuildCollectorLocal".toLowerCase();
		}else if(testtype.equals("localssh")){
			command="BuildCollectorSSHLocal".toLowerCase();
		}
		
		final CollectService collectService;
		try {
			collectService = (CollectService)Naming.lookup(url);
			final Message msg = new Message();
			msg.setCode(command);
			msg.setPort(22);
			msg.setUsername("fastech");
			msg.setPassword("123456");
			msg.setResURL("http://127.0.0.1:8080/hmp/resourceGetter_get.action");
			if(timer <= 0){
				doCollect(collectService,msg);
			}else{
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						doCollect(collectService,msg);
					}
				}, 0, timer*60*1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void doCollect(CollectService collectService, Message msg){

		// TODO Auto-generated method stub
		int i=buildNum;
		if(brandTemplate != null){
			while(i-->0){
				msg.setBrandTemplate(brandTemplate);
				msg.setAssetId("assetId " + i);
				msg.setIP("10.211.1." + i);
				try {
					System.out.println(collectService.dowork(msg));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			while(i-->0.75*buildNum){
				msg.setBrandTemplate("ZTE_BLADE");
				msg.setAssetId("assetId " + i);
				msg.setIP("10.211.1." + i);
				try {
					System.out.println(collectService.dowork(msg));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
			while(i-->0.5*buildNum){
				msg.setBrandTemplate("DELL_BLADE");
				msg.setAssetId("assetId " + i);
				msg.setIP("10.211.1." + i);
				try {
					System.out.println(collectService.dowork(msg));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
			while(i-->0.25*buildNum){
				msg.setBrandTemplate("HP_FRAME");
				msg.setAssetId("assetId " + i);
				msg.setIP("10.211.1." + i);
				try {
					System.out.println(collectService.dowork(msg));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
			while(i-->0){
				msg.setBrandTemplate("HUAWEI_FRAME");
				msg.setAssetId("assetId" + i);
				msg.setIP("10.211.1." + i);
				try {
					System.out.println(collectService.dowork(msg));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static String readFile(String fileName) throws Exception{  
		if(fileName != null){  
			Reader in = null;  
			char[] c = new char[16];  
			StringBuffer strBuffer = new StringBuffer();  
			try {  
				in = new InputStreamReader(  
				        HMP.class.getResourceAsStream("/" + fileName),"utf-8");  
				int len = 0;  
				while((len = in.read(c)) != -1){  
					strBuffer.append(c, 0, len);
				}  
				return strBuffer.toString();  
			}finally{  
				if(in != null){  
					try {  
						in.close();  
					} catch (IOException e) {  
						e.printStackTrace();  
					}  
				}  
			}  
		}  
		return null;  
	}
	
}  