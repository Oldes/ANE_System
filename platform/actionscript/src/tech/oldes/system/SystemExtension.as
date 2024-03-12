//   ____  __   __        ______        __
//  / __ \/ /__/ /__ ___ /_  __/__ ____/ /
// / /_/ / / _  / -_|_-<_ / / / -_) __/ _ \
// \____/_/\_,_/\__/___(@)_/  \__/\__/_// /
//  ~~~ oldes.huhuman at gmail.com ~~~ /_/
//
// SPDX-License-Identifier: Apache-2.0

package tech.oldes.system
{
	import flash.events.EventDispatcher;
	import flash.events.Event;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	import tech.oldes.system.events.*
	
	public class SystemExtension extends EventDispatcher
	{
		////////////////////////////////////////////////////////
		//	CONSTANTS
		//
		
		//
		//	ID and Version numbers
		public static const EXT_CONTEXT_ID:String = Const.EXTENSIONID;
		private static const EXT_ID_NUMBER:int = -1;
		
		public static const VERSION:String = Const.VERSION;
		private static const VERSION_DEFAULT:String = "0";
		private static const IMPLEMENTATION_DEFAULT:String = "unknown";
		
		//
		//	Error Messages
		private static const ERROR_CREATION:String = "The SystemExtension context could not be created";

		////////////////////////////////////////////////////////
		//	VARIABLES
		//
		
		//
		// Singleton variables	
		private static var _instance:SystemExtension;
		private static var _extContext:ExtensionContext;
		
		////////////////////////////////////////////////////////
		//	SINGLETON INSTANCE
		//
		public static function get instance():SystemExtension {
			if ( !_instance ) {
				_instance = new SystemExtension( new SingletonEnforcer() );
				_instance.init();
			}
			return _instance;
		}

		public function SystemExtension( enforcer:SingletonEnforcer ) {
			_extContext = ExtensionContext.createExtensionContext( EXT_CONTEXT_ID, null );
			if ( !_extContext ) throw new Error( ERROR_CREATION );
			_extContext.addEventListener( StatusEvent.STATUS, onStatusHandler );
		}

		private function onStatusHandler( event:StatusEvent ):void {
			var e:Event;
			//systemLog("onStatusHandler code: "+ event.code +" level: "+ event.level);

			switch(event.code) {
				case AlertDialogEvent.ON_ALERT_DIALOG:
					e = new AlertDialogEvent(event.code, event.level);
					break;
				case StateChangedEvent.ON_STATE_CHANGED:
					e = new StateChangedEvent(event.code, event.level);
					break;
			}
			if (e) {
				this.dispatchEvent(e);
			}
		}

		private function init():void {
			_extContext.call( "init" );
		}

		public function dispose():void {
			if (_extContext) {
				_extContext.removeEventListener( StatusEvent.STATUS, onStatusHandler );
				_extContext.dispose();
				_extContext = null;
			}
			_instance = null;
		}
		

		
		
		//----------------------------------------
		//
		// Public Methods
		//
		//----------------------------------------

		public function get version():String
		{
			return VERSION;
		}

		public function get nativeVersion():String
		{
			return _extContext.call("nativeVersion") as String;
		}

		public function systemLog(message:String):void
		{
			_extContext.call("systemLog", message);
		}
		
		public function getDeviceId(): String { return _extContext.call("getDeviceId") as String; }
		public function getResourceString(id:String): String { return _extContext.call("getResourceString", id) as String; }
		public function getSDKInt(): int { return _extContext.call("getSDKInt") as int; }
		public function showToast(message:String, long:Boolean=false): Boolean { return _extContext.call("showToast", message, long) as Boolean; }
		public function showAlertDialog(title:String = null, message:String = null): Boolean { return _extContext.call("showAlertDialog", title, message) as Boolean; }
		public function keepAwake(state:Boolean): void { _extContext.call("keepAwake", state); }
		public function navigateToURL(url:String): Boolean { return _extContext.call("navigateToURL", url) as Boolean; }
		public function visitURLDialog(url:String, messageRes:String, nameRes:String=null, comment:String=null): Boolean { return _extContext.call("visitURLDialog", url, messageRes, nameRes, comment) as Boolean; }

	}
}

class SingletonEnforcer {}