package com.yoruba.talomoo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
	private Context _context;

	public ConnectionDetector(Context context){
		this._context = context;
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivty = (ConnectivityManager) 
				_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivty != null) {
			NetworkInfo[] info = connectivty.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
			}
		return false;
	}
}
