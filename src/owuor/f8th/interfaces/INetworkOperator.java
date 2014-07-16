package owuor.f8th.interfaces;

import java.util.List;

import org.json.JSONObject;


public interface INetworkOperator<NameValuePair> {
	
	public JSONObject sendHttpRequest(List<NameValuePair> params);
	public void exit();
}
