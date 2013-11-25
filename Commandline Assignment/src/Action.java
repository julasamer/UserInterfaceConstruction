import java.util.ArrayList;
import java.util.Map;


public abstract class Action {
	public static enum ActionType {
		TriggerOn,
		TriggerOff,
		State, 
		Lock,
		Unknown
	}
	
	public static Action createAction(Map<Location, ArrayList<Device>> devices, ActionType actionType) {
		switch (actionType) {
			case TriggerOn: return new TriggerAction(devices, true); 
			case TriggerOff: return new TriggerAction(devices, false); 
			case State: return new GetStateAction(devices); 
			case Lock: return null;
			case Unknown: return null;
		}
		
		return null;
	}
	
	public Map<Location, ArrayList<Device>> devices;
	
	public Action(Map<Location, ArrayList<Device>> devices) {
		this.devices = devices;
	}
	
	public abstract void execute();
	
	public static class TriggerAction extends Action {
		public final boolean triggeredState;
		
		public TriggerAction(Map<Location, ArrayList<Device>> devices, boolean triggeredState) {
			super(devices);
			this.triggeredState = triggeredState;
		}
		
		public void execute() {
			for (ArrayList<Device> devices : this.devices.values()) {
				for (Device device : devices) {
					device.setEnabled(this.triggeredState);
				}
			}
		}
		
		public String toString() {
			String result = "Turn "+ (triggeredState ? "on" : "off")+":";
			
			for (Location location : this.devices.keySet()) {
				ArrayList<Device> devices = this.devices.get(location);
				
				for (Device device : devices) {
					result += "\n\t" + device.name + " in "+ location.name;
				}
			}
		
			return result;
		}
	}
	
	public static class GetStateAction extends Action {
		public GetStateAction(Map<Location, ArrayList<Device>> devices) {
			super(devices);
		}

		public void execute() {	}
		
		public String toString() {
			String result = "States:";
			
			for (Location location : this.devices.keySet()) {
				ArrayList<Device> devices = this.devices.get(location);
				
				for (Device device : devices) {
					result += "\n\t" + device.name + " in " + location.name + " is " + (device.isEnabled() ? "on" : "off") + ".";
				}
			}
		
			return result;
		}
	}
}
