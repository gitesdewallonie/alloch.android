package be.gitesdewallonie.allochambredhotes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Lib {
	static public String GetHtml(String url) throws IOException {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader(
				"User-Agent",
				"Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");

		HttpResponse httpResponse = (HttpResponse) httpclient.execute(httpget);
		String s = GetString(httpResponse);
		return s;
	}

	static private String GetString(HttpResponse response) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			response.getEntity().writeTo(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toString();
	}

	static Boolean isNullOrEmpty(String str) {
		if (str == null)
			return true;
		return str.trim().length() == 0;
	}

	public static Boolean isConnected(Context ctx) {
		Boolean ret = false;
		ConnectivityManager conMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr != null) {
			NetworkInfo i = conMgr.getActiveNetworkInfo();
			if (i == null)
				return false;
			if (!i.isConnected())
				return false;
			if (!i.isAvailable())
				return false;
			return true;
		} else
			ret = false;
		return ret;
	}

}
