//   ____  __   __        ______        __
//  / __ \/ /__/ /__ ___ /_  __/__ ____/ /
// / /_/ / / _  / -_|_-<_ / / / -_) __/ _ \
// \____/_/\_,_/\__/___(@)_/  \__/\__/_// /
//  ~~~ oldes.huhuman at gmail.com ~~~ /_/
//
// SPDX-License-Identifier: Apache-2.0

package tech.oldes.system;

import static tech.oldes.system.SystemFunctions.*;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import com.adobe.air.SystemActivityResultCallback;
import com.adobe.air.AndroidActivityWrapper;
import com.adobe.air.AndroidActivityWrapper.ActivityState;
import com.adobe.air.SystemStateChangeCallback;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;


public class SystemExtensionContext extends FREContext implements
	SystemActivityResultCallback,
	SystemStateChangeCallback
{
	private AndroidActivityWrapper aaw = null;

	public SystemExtensionContext()
	{
		aaw = AndroidActivityWrapper.GetAndroidActivityWrapper();
		aaw.addActivityResultListener(this);
		aaw.addActivityStateChangeListner(this);
	}

	@Override
	public void dispose() {
		if(SystemExtension.VERBOSE > 0) Log.i(SystemExtension.TAG,"Context disposed.");
		if (aaw != null) {
			aaw.removeActivityResultListener(this);
			aaw = null;
		}
	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		Map<String, FREFunction> functions = new HashMap<>();
		functions.put("init",              new Init());
		functions.put("nativeVersion",     new NativeVersion());
		functions.put("systemLog",         new SystemLog());

		functions.put("getDeviceId",       new GetDeviceId());
		functions.put("getResourceString", new GetResourceString());
		functions.put("getSDKInt",         new GetSDKInt());
		functions.put("showToast",         new ShowToast());
		functions.put("showAlertDialog",   new ShowAlertDialog());
		functions.put("keepAwake",         new KeepAwake());
		functions.put("navigateToURL",     new NavigateToURL());
		functions.put("visitURLDialog",    new ShowVisitURLDialog());
		return functions;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(SystemExtension.VERBOSE>1) Log.d(SystemExtension.TAG, "onActivityResult: "+ requestCode +" "+ resultCode );
		//SystemExtension.log("ExtensionContext.onActivityResult" +
		//		" requestCode:" + requestCode +
		//		" resultCode:" + resultCode);

		//if (SystemExtension.googleApiHelper != null) {
		//	SystemExtension.googleApiHelper.onActivityResult(requestCode, resultCode, intent);
		//}
	}

	@Override
	public void onActivityStateChanged( ActivityState state ) {
		if(SystemExtension.VERBOSE>1) Log.d(SystemExtension.TAG, "onActivityStateChanged: "+ state);
		dispatchStatusEventAsync("onStateChanged", state.toString());
	}
	@Override
	public void onConfigurationChanged(Configuration paramConfiguration) {
		if(SystemExtension.VERBOSE>1) Log.d(SystemExtension.TAG, "onConfigurationChanged: "+ paramConfiguration);
	}

}
