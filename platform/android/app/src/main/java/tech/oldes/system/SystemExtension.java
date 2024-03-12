//   ____  __   __        ______        __
//  / __ \/ /__/ /__ ___ /_  __/__ ____/ /
// / /_/ / / _  / -_|_-<_ / / / -_) __/ _ \
// \____/_/\_,_/\__/___(@)_/  \__/\__/_// /
//  ~~~ oldes.huhuman at gmail.com ~~~ /_/
//
// SPDX-License-Identifier: Apache-2.0

package tech.oldes.system;

import android.content.Context;
import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class SystemExtension implements FREExtension
{
	public static final String TAG = "ANE_System";
	public static final String VERSION = "1.0.0";
	public static final int VERBOSE = 1;

	public static SystemExtensionContext extensionContext;
	public static Context appContext;
	public static Boolean keepAwake;

	@Override
	public FREContext createContext(String contextType) {
		return extensionContext = new SystemExtensionContext();
	}

	@Override
	public void dispose() {
		if(VERBOSE > 0) Log.i(TAG, "Extension disposed.");
		appContext = null;
		extensionContext = null;
	}

	@Override
	public void initialize() {
		if(VERBOSE > 0) Log.i(TAG, "Extension initialized.");
	}

	public static void handleException(Exception e) {
		if(VERBOSE > 0) e.printStackTrace();
	}
}
