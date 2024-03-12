package tech.oldes.system.events
{
	import flash.events.Event;
	
	public class AlertDialogEvent extends Event
	{
		public static const ON_ALERT_DIALOG:String = "onAlertDialog";
		private var mValue:String;
		
		public function AlertDialogEvent(type:String, value:String="", bubbles:Boolean=false, cancelable:Boolean=false)
		{
			mValue = value;
			super(type, bubbles, cancelable);
		}
		
		public final function get value():String {
			return mValue;
		}
		
		public override function toString():String {
			return "[ON_ALERT_DIALOG: "+value+"]";
		}
	}
}