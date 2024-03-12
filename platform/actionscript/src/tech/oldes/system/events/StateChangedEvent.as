package tech.oldes.system.events
{
	import flash.events.Event;
	
	public class StateChangedEvent extends Event
	{
		public static const ON_STATE_CHANGED:String = "onStateChanged";
		private var mValue:String;
		
		public function StateChangedEvent(type:String, value:String="", bubbles:Boolean=false, cancelable:Boolean=false)
		{
			mValue = value;
			super(type, bubbles, cancelable);
		}
		
		public final function get value():String {
			return mValue;
		}

		public override function toString():String {
			return "[ON_STATE_CHANGED: "+value+"]";
		}
	}
}