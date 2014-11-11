package golbang.com.bluled.Service;

/**
 * Created by yoosung-jong on 14. 11. 7..
 */
public interface IServiceListener {
    public void OnReceiveCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4);
}
