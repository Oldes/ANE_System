//   ____  __   __        ______        __
//  / __ \/ /__/ /__ ___ /_  __/__ ____/ /
// / /_/ / / _  / -_|_-<_ / / / -_) __/ _ \
// \____/_/\_,_/\__/___(@)_/  \__/\__/_// /
//  ~~~ oldes.huhuman at gmail.com ~~~ /_/
//
// SPDX-License-Identifier: Apache-2.0

package tech.oldes.system;

import static android.provider.Settings.*;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class SystemFunctions
{
	static public int getResourceId(String name, String type) {
		Log.d(SystemExtension.TAG, "getResourceId: "+name);
		Resources res = SystemExtension.appContext.getResources();
		return res.getIdentifier(name, type, SystemExtension.appContext.getPackageName());
	}
	static public String getResourceString(String id) {
		Log.d(SystemExtension.TAG, "getResourceString: '"+id+"'");
		Resources res = SystemExtension.appContext.getResources();
		return res.getString(res.getIdentifier(id, "string", SystemExtension.appContext.getPackageName()));
	}
	static public void navigateToURL(String url) {
		if(SystemExtension.VERBOSE > 0) Log.d(SystemExtension.TAG, "navigateToURL: "+ url);
		Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		openUrlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SystemExtension.extensionContext.getActivity().startActivity(openUrlIntent);
	}
	static private void displayDialog(AlertDialog.Builder alertDialogBuilder) {
		if(SystemExtension.VERBOSE > 0) Log.d(SystemExtension.TAG, "displayDialog: "+ alertDialogBuilder);
		try {
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			//Set the dialog to not focusable (makes navigation ignore us adding the window)
			alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

			// show it
			alertDialog.show();

			//Set the dialog to immersive
			alertDialog.getWindow().getDecorView().setSystemUiVisibility(
				SystemExtension.extensionContext.getActivity().getWindow().getDecorView().getSystemUiVisibility());

			//Clear the not focusable flag from the window
			alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		} catch (Exception e) {
			SystemExtension.handleException(e);
		}
	}

	static public class Init implements FREFunction {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			SystemExtension.appContext = context.getActivity().getApplicationContext();
			return null;
		}
	}

	static public class NativeVersion implements FREFunction {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			FREObject result = null;
			try {
				result = FREObject.newObject( SystemExtension.VERSION );
			} catch (Exception e) {
				SystemExtension.handleException( e );
			}
			return result;
		}
	}

	static public class SystemLog implements FREFunction {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			try {
				String message = args[0].getAsString();
				Log.d(SystemExtension.TAG, message);
			} catch (Exception e) {
				SystemExtension.handleException( e );
			}
			return null;
		}
	}

	static public class GetDeviceId implements FREFunction  {
		@SuppressLint("HardwareIds")
		@Override
		public FREObject call(FREContext ctx, FREObject[] args) {
			FREObject result = null;

			try{
				result = FREObject.newObject(
					Secure.getString(SystemExtension.appContext.getContentResolver()
						, Secure.ANDROID_ID));
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}
			return result;
		}
	}

	static public class GetSDKInt implements FREFunction  {
		@Override
		public FREObject call(FREContext ctx, FREObject[] args) {
			FREObject result = null;

			try{
				result = FREObject.newObject(android.os.Build.VERSION.SDK_INT);
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}
			return result;
		}
	}

	static public class GetResourceString implements FREFunction  {
		@Override
		public FREObject call(FREContext ctx, FREObject[] args) {
			try{
				String id = args[0].getAsString();
				String str = getResourceString(id);
		    	if(SystemExtension.VERBOSE > 0) Log.d(SystemExtension.TAG, "GetResourceString: '"+ id +"' = "+str);
		    	return FREObject.newObject(str);
		    	
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}
			return null;
		}
	}
		
	static public class KeepAwake implements FREFunction  {
		@Override
		public FREObject call(FREContext ctx, FREObject[] args) {
			try{
				Boolean keepAwake = SystemExtension.keepAwake = args[0].getAsBool();
				if(SystemExtension.VERBOSE > 0) Log.d(SystemExtension.TAG, "KeepAwake: "+ keepAwake);
				if(keepAwake) {
					SystemExtension.extensionContext.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				} else {
					SystemExtension.extensionContext.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				}
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}
			return null;
		}
	}
	
	static public class ShowToast implements FREFunction  {
		@Override
		public FREObject call(FREContext ctx, FREObject[] args) {
			try{
				String message = args[0].getAsString();
		    	int duration = args[1].getAsBool() ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		    	if(SystemExtension.VERBOSE > 0) Log.d(SystemExtension.TAG, "showToast: '"+ message +"', duration: "+ duration);
		    	Toast.makeText(ctx.getActivity(), message, duration).show();
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}
			return null;
		}
	}
	
	static public class ShowAlertDialog implements FREFunction {
		public static final String NO = "no";
		public static final String YES = "yes";

		@Override
		final public FREObject call(FREContext context, FREObject[] args) {

			String title   = null;
			String message = null;
			try {
				title   = (args[0] == null) ? null : args[0].getAsString();
				message = (args[1] == null) ? null : args[1].getAsString();
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}

			try {
				if(SystemExtension.VERBOSE > 1) Log.d(SystemExtension.TAG, "ShowAlertDialog!!");
				//Log.d(SystemExtension.TAG, "CONTEXT: "+ context + " activity:" + context.getActivity());
				//Log.d(SystemExtension.TAG, "AppTheme: "+ getResourceId("AppTheme", "style"));

				ContextThemeWrapper contextThemeWrapper;
				contextThemeWrapper = new ContextThemeWrapper(context.getActivity(), getResourceId("AppTheme", "style") );
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper);
				
				//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context.getActivity());

				//Log.d(SystemExtension.TAG, "ShowAlertDialog builder:"+ alertDialogBuilder);
				
				alertDialogBuilder
					//	.setIcon(getResourceId("ic_launcher", "mipmap"))
					.setTitle(title)
					.setMessage(message)
					.setCancelable(false)
					.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							// Prevent dialog close on back press button
							return keyCode == KeyEvent.KEYCODE_BACK;
						}
					})
					.setNegativeButton(getResourceString(NO), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
						    SystemExtension.extensionContext.dispatchStatusEventAsync("onAlertDialog", NO);
						}
					})
					.setPositiveButton(getResourceString(YES), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
						    SystemExtension.extensionContext.dispatchStatusEventAsync("onAlertDialog", YES);
						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener() {         
					    @Override
					    public void onCancel(DialogInterface dialog) {
					    	SystemExtension.extensionContext.dispatchStatusEventAsync("onAlertDialog", "cancel");
					    }
					});
				
				displayDialog(alertDialogBuilder);
				
			    return FREObject.newObject(true);
			}
			catch (Exception e) {
				Log.e(SystemExtension.TAG, "ShowAlertDialog failed");
				SystemExtension.handleException(e);
				return null;
			}
		}
	}
	
	static public class ShowVisitURLDialog implements FREFunction {
		public static final String NO = "no";
		public static final String YES = "yes";
		public static String url;
		
		@Override
		final public FREObject call(FREContext context, FREObject[] args) {
			try {
				if(SystemExtension.VERBOSE > 1) Log.d(SystemExtension.TAG, "ShowVisitURLDialog");
				
				url = args[0].getAsString();
				String title   = getResourceString(args[1].getAsString());
				String name = (args[2] == null) ? null : args[2].getAsString();
				String message = (args[3] == null) ? null : args[3].getAsString();
				
				
				if(name != null) {
					name = getResourceString(name);
					title = String.format(title, name);
				}

				if(SystemExtension.VERBOSE > 1) Log.d(SystemExtension.TAG, "ShowVisitURLDialog message: " +message);
				ContextThemeWrapper contextThemeWrapper;
				contextThemeWrapper = new ContextThemeWrapper(context.getActivity(), getResourceId("AppTheme", "style"));
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper);

				//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SystemExtension.appContext);
				//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context.getActivity());
				//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context.getActivity(), AlertDialog.THEME_HOLO_DARK);
				alertDialogBuilder
					.setTitle(title)
					.setMessage(message)
					.setNegativeButton(getResourceString(NO), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
						    //SystemExtension.extensionContext.dispatchStatusEventAsync("onAlertDialog", NO);
						}
					})
					.setPositiveButton(getResourceString(YES), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							navigateToURL(url);
						}
					});
				
				displayDialog(alertDialogBuilder);
				
			    return FREObject.newObject(true);
			}
			catch (Exception e) {
				SystemExtension.handleException(e);
				return null;
			}
		}
	}
	
	static public class NavigateToURL implements FREFunction  {
		@Override
		public FREObject call(FREContext ctx, FREObject[] args) {
			try{
				navigateToURL(args[0].getAsString());
		    	return FREObject.newObject(true);
			} catch (Exception e) {
				SystemExtension.handleException(e);
			}
			return null;
		}
	}
}
