package halluo;

import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = -5703455598463351940L;
	private String code; 
	private String IP;
	private int port;
	private String brandTemplate; 
	private String assetId;
	private String username;
	private String password;
	private String resURL;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getBrandTemplate() {
		return brandTemplate;
	}
	public void setBrandTemplate(String brandTemplate) {
		this.brandTemplate = brandTemplate;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getResURL() {
		return resURL;
	}
	public void setResURL(String resURL) {
		this.resURL = resURL;
	}
}
