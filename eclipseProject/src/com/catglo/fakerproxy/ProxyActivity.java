package com.catglo.fakerproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.catglo.fakerproxy.HttpProxy.ProxyLogListener;
import com.catglo.fakerproxy.HttpProxy.ProxyStatusListener;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ProxyActivity extends Activity {
	
	public static final int LOCALHOST_RELAY_PORT=8082;
	public static final String API_HOST = "google.com";
	public static final int API_PORT = 80;
	public static final int MAX_DEVICE_ON_SCREEN_HISTORY_LIST=30;
	
	private TextView statusText;
	private ViewGroup historyLayout;
	private ToggleButton enableLoggingToggle;
	private ToggleButton enablInterceptToggle;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		HashMap<String,String> fakeApis = new HashMap<String,String>();
		
//		fakeApis.put("/api/tap/rewards", "rewards.json");
//		fakeApis.put("/api/tap/rewards?redeemed=true", "rewards.json");
//		fakeApis.put("/api/tap/rewards?redeemed=false", "rewards.json");		
	//	fakeApis.put("/services/customers/me/profile?fields","rewards.json");
		
		final HttpProxy proxyThread = new HttpProxy(getAssets()
				                            ,LOCALHOST_RELAY_PORT
				                            ,API_HOST
				                            ,API_PORT
				                            ,fakeApis);
		
		
		
		statusText = (TextView)findViewById(R.id.statusText);
		proxyThread.setProxyStatusListener(new ProxyStatusListener(){public void statusChanged(final String newStatus) {
			runOnUiThread(new Runnable(){public void run() {
				statusText.setText(newStatus);		
			}});
		}});
		
		historyLayout = (ViewGroup)findViewById(R.id.historyLayout);
		proxyThread.setProxyLogListener(new ProxyLogListener(){public void log(final String log) {
			runOnUiThread(new Runnable(){public void run() {
				if (historyLayout.getChildCount()>MAX_DEVICE_ON_SCREEN_HISTORY_LIST){
					historyLayout.removeViewAt(historyLayout.getChildCount()-1);
				}
				TextView t = new TextView(ProxyActivity.this);
				t.setText(log);
				historyLayout.addView(t, 0);
			}});
		}});
		
		enableLoggingToggle = (ToggleButton)findViewById(R.id.enableLoggingToggle);
		enableLoggingToggle.setChecked(true);
		enableLoggingToggle.setOnCheckedChangeListener(new OnCheckedChangeListener(){public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			proxyThread.enableLogging = isChecked;
		}});
		
		
		enablInterceptToggle = (ToggleButton)findViewById(R.id.enablInterceptToggle);
		enablInterceptToggle.setChecked(true);
		enablInterceptToggle.setOnCheckedChangeListener(new OnCheckedChangeListener(){public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			proxyThread.enableIntercept = isChecked;
		}});
		
		
		proxyThread.start();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
