package main;

import halluo.CollectService;
import halluo.Message;

import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.Naming;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 2835446369144621288L;
	public static final String LINE_FEED = System.getProperty("line.separator");//换行符
	public static final String PLATFORM_NAME = Strings.getString("MainWindow.PLATFORM_NAME");//换行符

	static final String[] ModelTplStrings = { "HP_BLADE", "HP_FRAME", "DELL_BLADE", "IBM_BLADE", 
			"IBM_FRAME", "HUAWEI_FRAME", "ZTE_BLADE"
	};

	final String[] codeStrings = {"buildcollector","startdebug", "stopdebug", "startcollect", "stopcollect", "resetallinfo", 
			"reboot", "showcountinfo", "showhistory", "showstatis", "showallinfo","showdbconninfo",
			"showsshinfo", "maxsize", "listsize", "cachesize", "showpoolinfo",
			"showcachepoolinfo", "showsysteminfo"
	};

	final String[] codeStringsCHS = { "建立采集器","打开调试", "关闭调试", "开启采集", "关闭采集", "重置所有信息", 
			"重启采集服务器", "显示计数信息", "显示历史信息", "显示统计", "显示所有信息","测试数据库连接",
			"测试所有SSH连接", "显示采集池最大容量", "显示采集池当前数", "显示缓冲池当前数", "显示采集池信息",
			"显示缓冲池信息", "显示系统信息"
	};

	private JTextField tfCltIP;
	private JTextField tfSSHIP;
	private JTextField tfSSHPort;
	private JTextField tfUserName;
	private JPasswordField tfPwd;
	private JTextField tfPlatformIP;
	private JTextArea txaInfo;
	private String code = codeStrings[0];
	private String buildCode = "buildcollector";
	private String modelTpl = ModelTplStrings[0];
	private JTextField tfAssetID;
	JRadioButton rdbtnDefault;
	JRadioButton rdbtnLocal;
	JRadioButton rdbtnLocalSSH;
	JComboBox cbModelTpl;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if(args.length > 0){
			if(args.length == 1){
				HMP.printUsage();
			}else{
				HMP.execute(args);
			}
		}else{
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainWindow window = new MainWindow();
						window.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Create the application.
	 */
	public MainWindow() throws Exception{
		setTitle(Strings.getString("MainWindow.this.title")); 
		setResizable(false);
		setFont(UIManager.getFont("FileChooser.listFont"));
		initialize();
	}


	class CltCnnBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String cltIP = tfCltIP.getText();
			String retInfo = testRMIConnection(cltIP);
			txaInfo.setText(retInfo.trim());
		}

	}
	class SSHCnnBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String retInfo = testSSHConnection(tfCltIP.getText(), inflateMessage());
			txaInfo.setText(retInfo.trim());
		}

	}
	
	class ResCnnBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String retInfo = testResConnection(tfCltIP.getText(),inflateMessage());
			txaInfo.setText(retInfo.trim());
		}             

	}	

	class SendCmdBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String retInfo = sendCommand(tfCltIP.getText(),inflateMessage());
			txaInfo.setText(retInfo.trim());
		}

	}


	class ModelTplCmbtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JComboBox cb = (JComboBox)e.getSource();
			modelTpl = (String)cb.getSelectedItem();
		}

	}

	class CodeCmbtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JComboBox cb = (JComboBox)e.getSource();
			int i = cb.getSelectedIndex();
			if(i == 0){
				code = buildCode;
				tfAssetID.setEnabled(true);
				rdbtnDefault.setEnabled(true);
				rdbtnLocal.setEnabled(true);
				rdbtnLocalSSH.setEnabled(true);
			}else{
				code = codeStrings[i];
				tfAssetID.setEnabled(false);
				rdbtnDefault.setEnabled(false);
				rdbtnLocal.setEnabled(false);
				rdbtnLocalSSH.setEnabled(false);
			}
		}

	}

	class CollectTypeRdbtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String actionCmd = e.getActionCommand();
			if(actionCmd.equals("default")){
				buildCode = "buildcollector";
			}else if(actionCmd.equals("local")){
				buildCode = "buildcollectorlocal";
			}else if(actionCmd.equals("localssh")){
				buildCode = "buildcollectorsshlocal";
			}
			if(isBuildCode()){
				code = buildCode;
			}
		}
	}
	
	class AssetIPTfFoucusListener implements FocusListener{
		
		String preIP = "";
		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			String cltIP = tfCltIP.getText();
			if(!"".equals(cltIP) && !"".equals(tfSSHIP.getText())){
				if(!preIP.equals(tfSSHIP.getText())){
					tfAssetID.setText("");
					cbModelTpl.setSelectedItem(null);
				}
				new Thread(new AssetInfoSetThread()).start();
			}
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			preIP = tfSSHIP.getText();
		}
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		ToolTipManager.sharedInstance().setInitialDelay(100);
		this.setBounds(100, 100, 563, 495);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		JButton cltCnnButton = new JButton(Strings.getString("MainWindow.cltCnnButton.text")); 
		cltCnnButton.setToolTipText(Strings.getString("MainWindow.cltCnnButton.toolTipText")); //$NON-NLS-1$
		//this.getRootPane().setDefaultButton(cltCnnButton);
		cltCnnButton.addActionListener(new CltCnnBtnListener());
		cltCnnButton.setBounds(405, 16, 135, 60);
		this.getContentPane().add(cltCnnButton);
		tfCltIP = new JTextField();
		tfCltIP.setToolTipText(Strings.getString("MainWindow.tfCltIP.toolTipText")); 
		tfCltIP.setBounds(115, 16, 275, 23);
		tfCltIP.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				new Thread(new inflateTemplateThread()).start();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
			}
		});
		this.getContentPane().add(tfCltIP);
		tfCltIP.setColumns(10);

		JLabel lblCltIP = new JLabel(Strings.getString("MainWindow.lblCltIP.text")); 
		lblCltIP.setBounds(20, 16, 102, 23);
		this.getContentPane().add(lblCltIP);
		JScrollPane sp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(20, 340, 520, 117);
		getContentPane().add(sp);
		txaInfo = new JTextArea();
		txaInfo.setLineWrap(true);
		txaInfo.setBackground(SystemColor.inactiveCaptionBorder);
		txaInfo.setEditable(false);
		txaInfo.setBounds(0, 0, 420, 73);
		sp.setViewportView(txaInfo);

		JLabel lblSSHIP = new JLabel(Strings.getString("MainWindow.lblSSHIP.text")); 
		lblSSHIP.setBounds(20, 90, 102, 23);
		this.getContentPane().add(lblSSHIP);

		tfSSHIP = new JTextField();
		tfSSHIP.setToolTipText(Strings.getString("MainWindow.tfSSHIP.toolTipText")); //$NON-NLS-1$
		tfSSHIP.setBounds(115, 90, 275, 23);
		tfSSHIP.addFocusListener(new AssetIPTfFoucusListener());
		this.getContentPane().add(tfSSHIP);
		tfSSHIP.setColumns(10);

		JLabel lblSSHPort = new JLabel(Strings.getString("MainWindow.lblSSHPort.text")); 
		lblSSHPort.setBounds(20, 127, 102, 23);
		this.getContentPane().add(lblSSHPort);

		tfSSHPort = new JTextField();
		tfSSHPort.setToolTipText(Strings.getString("MainWindow.tfSSHPort.toolTipText")); //$NON-NLS-1$
		tfSSHPort.setBounds(115, 127, 275, 23);
		this.getContentPane().add(tfSSHPort);
		tfSSHPort.setColumns(10);

		JLabel lblUserName = new JLabel(Strings.getString("MainWindow.lblUserName.text")); 
		lblUserName.setBounds(20, 164, 102, 23);
		this.getContentPane().add(lblUserName);

		tfUserName = new JTextField();
		tfUserName.setBounds(115, 164, 275, 23);
		this.getContentPane().add(tfUserName);
		tfUserName.setColumns(10);

		JLabel lblPwd = new JLabel(Strings.getString("MainWindow.lblPwd.text")); 
		lblPwd.setBounds(20, 201, 102, 23);
		this.getContentPane().add(lblPwd);

		tfPwd = new JPasswordField();
		tfPwd.setBounds(115, 201, 275, 23);
		this.getContentPane().add(tfPwd);
		tfPwd.setColumns(10);

		JButton btnSSHCnn = new JButton(Strings.getString("MainWindow.btnSSHCnn.text")); 
		btnSSHCnn.setToolTipText(Strings.getString("MainWindow.btnSSHCnn.toolTipText")); //$NON-NLS-1$
		btnSSHCnn.addActionListener(new SSHCnnBtnListener());
		btnSSHCnn.setBounds(405, 164, 135, 60);
		this.getContentPane().add(btnSSHCnn);

		JLabel lblModelTpl = new JLabel(Strings.getString("MainWindow.lblModelTpl.text")); 
		lblModelTpl.setBounds(20, 238, 90, 23);
		getContentPane().add(lblModelTpl);

		JLabel lblPlatformIP = new JLabel(Strings.getString("MainWindow.lblPlatformIP.text")); 
		lblPlatformIP.setBounds(20, 53, 102, 23);
		getContentPane().add(lblPlatformIP);

		cbModelTpl = new JComboBox(ModelTplStrings);
		cbModelTpl.setSelectedIndex(0);
		cbModelTpl.setBounds(115, 238, 98, 21);
		getContentPane().add(cbModelTpl);
		cbModelTpl.addActionListener(new ModelTplCmbtnListener());

		JLabel lblCmd = new JLabel(Strings.getString("MainWindow.lblCmd.text")); 
		lblCmd.setBounds(219, 238, 36, 23);
		getContentPane().add(lblCmd);

		JComboBox cbCmd = new JComboBox(codeStringsCHS);
		cbCmd.setSelectedIndex(0);
		cbCmd.setBounds(254, 238, 135, 21);
		getContentPane().add(cbCmd);
		cbCmd.addActionListener(new CodeCmbtnListener());

		JLabel lblCollectType = new JLabel(Strings.getString("MainWindow.lblCollectType.text")); 
		lblCollectType.setBounds(20, 307, 90, 23);
		getContentPane().add(lblCollectType);

		rdbtnDefault = new JRadioButton(Strings.getString("MainWindow.rdbtnDefault.text"));
		rdbtnDefault.setSelected(true);
		rdbtnDefault.setBounds(115, 307, 73, 23);
		getContentPane().add(rdbtnDefault);
		rdbtnDefault.setActionCommand(Strings.getString("MainWindow.rdbtnDefault.actionCommand")); 
		rdbtnDefault.addActionListener(new CollectTypeRdbtnListener());

		rdbtnLocal = new JRadioButton(Strings.getString("MainWindow.rdbtnLocal.text")); 
		rdbtnLocal.setToolTipText(Strings.getString("MainWindow.rdbtnLocal.toolTipText")); //$NON-NLS-1$
		rdbtnLocal.setBounds(195, 307, 73, 23);
		getContentPane().add(rdbtnLocal);
		rdbtnLocal.setActionCommand(Strings.getString("MainWindow.rdbtnLocal.actionCommand")); 
		rdbtnLocal.addActionListener(new CollectTypeRdbtnListener());

		rdbtnLocalSSH = new JRadioButton(Strings.getString("MainWindow.rdbtnLocalSSH.text")); 
		rdbtnLocalSSH.setToolTipText(Strings.getString("MainWindow.rdbtnLocalSSH.toolTipText")); //$NON-NLS-1$
		rdbtnLocalSSH.setBounds(275, 307, 85, 23);
		getContentPane().add(rdbtnLocalSSH);
		rdbtnLocalSSH.setActionCommand(Strings.getString("MainWindow.rdbtnLocalSSH.actionCommand")); 
		rdbtnLocalSSH.addActionListener(new CollectTypeRdbtnListener());

		ButtonGroup btnGp = new ButtonGroup();
		btnGp.add(rdbtnDefault);
		btnGp.add(rdbtnLocal);
		btnGp.add(rdbtnLocalSSH);

		tfPlatformIP = new JTextField();
		tfPlatformIP.setToolTipText(Strings.getString("MainWindow.tfPlatformIP.toolTipText")); //$NON-NLS-1$
		tfPlatformIP.setColumns(10);
		tfPlatformIP.setBounds(115, 53, 275, 23);
		getContentPane().add(tfPlatformIP);

		JButton btnSendCmd = new JButton(Strings.getString("MainWindow.btnSendCmd.text")); 
		btnSendCmd.setToolTipText(Strings.getString("MainWindow.btnSendCmd.toolTipText")); //$NON-NLS-1$
		btnSendCmd.addActionListener(new SendCmdBtnListener());
		btnSendCmd.setBounds(405, 238, 135, 60);
		getContentPane().add(btnSendCmd);

		JLabel lblAssetID = new JLabel(Strings.getString("MainWindow.lblAssetID.text"));
		lblAssetID.setBounds(20, 275, 102, 23);
		getContentPane().add(lblAssetID);

		tfAssetID = new JTextField();
		tfAssetID.setColumns(10);
		tfAssetID.setBounds(115, 275, 275, 23);
		getContentPane().add(tfAssetID);
		
		JButton btnResCnn = new JButton(Strings.getString("MainWindow.btnResCnn.text")); 
		btnResCnn.setToolTipText(Strings.getString("MainWindow.btnResCnn.toolTipText")); //$NON-NLS-1$
		btnResCnn.addActionListener(new ResCnnBtnListener());
		btnResCnn.setBounds(405, 90, 135, 60);
		getContentPane().add(btnResCnn);

	}


	public String sendCommand(String cltIP, Message msg) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		CollectService collectService;
		String url = getFullRMIURL(cltIP);
		if(!"".equals(url)){
			if(!isBuildCode() || (msg.getAssetId() != null && !"".equals(msg.getAssetId()))){
				try {	
					collectService = (CollectService)Naming.lookup(url);
					sb.append(collectService.dowork(msg));
				} catch (Exception e) {
					sb.append("SSH连接失败!\n" + e.getMessage());
				}
			}else{
				sb.append("采集设备ID不能为空");
				tfAssetID.requestFocusInWindow();
			}

		}else{
			sb.append("采集服务器IP不能为空！");
			tfCltIP.requestFocusInWindow();
		}
		return sb.toString();		
	}
	
	protected String testResConnection(String cltIP, Message msg) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		CollectService collectService;
		String url = getFullRMIURL(cltIP);
		if("".equals(url)){
			tfCltIP.requestFocusInWindow();
			return "采集服务器IP不能为空！";
		}
		String modeltplt = (String)cbModelTpl.getSelectedItem();
		if(modeltplt == null || "".equals(modeltplt)){
			cbModelTpl.requestFocus();
			return "设备品牌模板不能为空！";
		}
		if(!"".equals(tfPlatformIP.getText())){
			try {
				collectService = (CollectService)Naming.lookup(url);
				msg.setCode("showresourceinfo");
				sb.append(collectService.dowork(msg));
			} catch (Exception e) {
				sb.append("采集服务器连接失败!\n" + e.getMessage());
			}
		}else{
			tfPlatformIP.requestFocusInWindow();
			sb.append("应用服务器IP不能为空！");
		}
		return sb.toString();
	}

	protected String testSSHConnection(String cltIP, Message msg) {
		StringBuilder sb = new StringBuilder();
		CollectService collectService;
		String url = getFullRMIURL(cltIP);
		if("".equals(url)){
			tfCltIP.requestFocusInWindow();
			return "采集服务器IP不能为空！";
		}
		if(!"".equals(msg.getIP())){
			try {
				collectService = (CollectService)Naming.lookup(url);
				msg.setCode("showsshinfo");
				sb.append(collectService.dowork(msg));
			} catch (Exception e) {
				sb.append("SSH连接失败!\n" + e.getMessage());
			}
		}else{
			sb.append("设备SSH地址不能为空！");
			tfSSHIP.requestFocusInWindow();
		}
		return sb.toString();
	}

	protected String testRMIConnection(String cltIP) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		CollectService collectService;
		String url = getFullRMIURL(cltIP);
		if(!"".equals(url)){
			try {
				collectService = (CollectService)Naming.lookup(url);
				Message msg = new Message();
				msg.setCode("test");
				collectService.dowork(msg);
				sb.append("采集服务器连接成功！");
			} catch (Exception e) {
				sb.append("采集服务器连接失败!\n" + e.getMessage());
			}
		}else{
			tfCltIP.requestFocusInWindow();
			sb.append("采集服务器IP不能为空！");
		}
		return sb.toString();
	}

	private String getFullRMIURL(String cltIP){
		String url = "";
		if(cltIP != null && !"".equals(cltIP)){
			if(cltIP.matches("^[\\s\\S]+:\\d+$")){
				url = "rmi://"+cltIP+"/CollectService";
			}else{
				url = "rmi://"+cltIP+":6600/CollectService";
			}
		}
		return url;
	}
	
	private String getResURL(String platformIP){
		String url = "http://127.0.0.1:8080/hmp/resourceGetter_get.action";
		if(platformIP != null && !"".equals(platformIP)){
			if(platformIP.matches("^[\\s\\S]+:\\d+$")){
				url = "http://"+platformIP+":/"+PLATFORM_NAME+"/resourceGetter_get.action";
			}else{
				url = "http://"+platformIP+":8080/"+PLATFORM_NAME+"/resourceGetter_get.action";
			}
		}
		return url;
	}

	private Message inflateMessage(){
		Message msg = new Message();
		msg.setAssetId(tfAssetID.getText());
		msg.setBrandTemplate(modelTpl);
		msg.setCode(code);
		msg.setIP(tfSSHIP.getText());
		msg.setPassword(new String(tfPwd.getPassword()));
		String sshPort = tfSSHPort.getText();
		msg.setPort(sshPort==null||"".equals(sshPort)?22:Integer.parseInt(sshPort));
		msg.setResURL(getResURL(tfPlatformIP.getText()));
		msg.setUsername(tfUserName.getText());
		return msg;
	}
	
	private boolean isBuildCode(){
		if(code.contains("buildcollector")){
			return true;
		}
		return false;
	}
	
	private class AssetInfoSetThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = inflateMessage();
			msg.setCode("getassetinfobyip");
			try {
				CollectService collectService = (CollectService)Naming.lookup(getFullRMIURL(tfCltIP.getText()));
				String ret = collectService.dowork(msg);
				if(ret != null && !"".equals(ret)){
					String rs[] = ret.split(",");
					tfAssetID.setText(rs[0]);
					cbModelTpl.setSelectedItem(rs[1]);
				}
			} catch (Exception e1) {
			}
		}
		
	}
	
	private class inflateTemplateThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = inflateMessage();
			msg.setCode("getalltemplate");
			try {
				CollectService collectService = (CollectService)Naming.lookup(getFullRMIURL(tfCltIP.getText()));
				String ret = collectService.dowork(msg);
				if(ret != null && !"".equals(ret.trim())){
					Object oldItem = cbModelTpl.getSelectedItem();
					String rs[] = ret.split(" ");
					Arrays.sort(rs);
					cbModelTpl.removeAllItems();
					for(String item:rs){
						cbModelTpl.addItem(item);
					}
					cbModelTpl.setSelectedItem(oldItem);
				}
			} catch (Exception e1) {
			}
		}
		
	}
}
