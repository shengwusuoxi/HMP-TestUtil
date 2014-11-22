package halluo;
import java.rmi.Remote;
 

public interface CollectService extends Remote {  
    public String  dowork(Message message) throws Exception;  
}  