package owuor.f8th.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import owuor.f8th.interfaces.INetworkOperator;
import owuor.f8th.types.F8th;
import android.util.Log;

public class NetworkOperator implements INetworkOperator<NameValuePair>{

	
	private static InputStream is;
	String json;
	JSONObject jObj=null;
	
	public NetworkOperator(){
		
	}//end of constructor
	
	@Override
	public JSONObject sendHttpRequest(List<NameValuePair> params) {
		// TODO Auto-generated method stub
		String url;
		//String result;
		try {
			url = new String(F8th.AUTHENTIFICATION_SERVER_ADDRESS);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			Log.i("Network Operator", "http request sent");
			StatusLine searchStatus = httpResponse.getStatusLine();
			
			if(searchStatus.getStatusCode() == 200){
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		           sb.append(line + "\n");
		        }
		        is.close();
		        json = sb.toString();
		        Log.i("JSON", json);
			}else{
				String error = String.valueOf(searchStatus.getStatusCode())+"\n"+searchStatus.getReasonPhrase();
				json = "{'success':0,'error':3,'error_msg':'"+error+"'}";
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			json = "{'success':0,'error':3,'error_msg':'server error.\nrestart app'}";
			Log.e("Network Operator UEE",e.getMessage().toString());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			json = "{'success':0,'error':3,'error_msg':'server failure.\ntry again later'}";
			Log.e("Network Operator CPE",e.getMessage().toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			json = "{'success':0,'error':3,'error_msg':'server not running.\ntry again later'}";
			Log.e("Network Operator IOE",e.getMessage().toString());
			e.printStackTrace();
		} catch(Exception e){
			json = "{'success':0,'error':3,'error_msg':'unknown exception error.\nrestart app'}";
			Log.e("Network Operator E",e.getMessage().toString());
			e.printStackTrace();
		}
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);           
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        Log.e("network operator", "data sent back");
        return jObj;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}


}