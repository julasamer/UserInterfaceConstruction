import java.util.List;

public abstract class Action implements IAction {	
	public final Device device;
	public final Location location;
	
	public Action(Device device, Location location) {
		this.device = device;
		this.location = location;
	}
	
	public abstract void execute();
	
	public static class TriggerAction extends Action {
		public final boolean triggeredState;
		
		public TriggerAction(Device device, Location location, boolean triggeredState) {
			super(device, location);
			this.triggeredState = triggeredState;
		}
		
		public void execute() {
			device.setEnabled(this.triggeredState);
		}
		
		public String toString(Tense tense) {
			return (tense == Tense.Past ? "turned" : "will turn") + " " + 
					(triggeredState ? "on" : "off")+" " + device.name + " in "+ location.name;
		}
	}
	
	public static class GetStateAction extends Action {
		public GetStateAction(Device device, Location location) {
			super(device, location);
		}

		public void execute() {	}
		
		public String toString(Tense tense) {
			return device.name + " in " + location.name + " is " + (device.isEnabled() ? "on" : "off") + ".";
		}
	}
	
	public static class MultiAction implements IAction {
		public final List<IAction> actions;
		
		public MultiAction(List<IAction> actions) {
			this.actions = actions;
		}
		
		public void execute() {
			for (IAction action : actions) action.execute();
		}
		
		public String toString(Tense tense) {			
			if (actions.size() == 0) return "";
			
			String result = actions.get(0).toString(tense);
			
			for (int index = 1; index < actions.size(); index++) {
				result += "\n" + actions.get(index).toString(tense);
			}
			
			return result;
		}
	}
	
	public static class PrintHelpAction implements IAction {
		public void execute() {}

		public String toString(Tense tense) {
			return "HACli Help\n"
					+ "You can use the following commands:\n"
					+ "\tturn on\n"
					+ "\tturn off\n"
					+ "\tlock\n"
					+ "\tunlock\n"
					+ "to control devices. Use \"all\" to apply the command to all devices.\n"
					+ "Write state (+device name or location) to get information about their states.\n"
					+ "To learn about scheduling commands, write \"help scheduling\".\n"
					+ "Send \"help\" + command name for additional help.\n"
					+ "Send \"list devices\" to list all devices.\n";
		}
	}
	
	public static class PrintTurnOnOffHelp implements IAction {
		public void execute() {}

		public String toString(Tense tense) {
			return "Turning on/off things\n"
					+ "To control the state of a device, use turn on/off and the device name or a location, for example:\n"
					+ "turn on lights - turns on all the lights\n"
					+ "turn on lights in bathroom - turns on the lights in the bathroom only\n"
					+ "turn off sauna - turns off all devices in the sauna (ie. the oven)\n"
					+ "Send \"list devices\" to list all devices.\n";
		}
	}
	
	public static class PrintLockHelp implements IAction {
		public void execute() {}

		public String toString(Tense tense) {
			return "(Un)Locking the door\n"
					+ "To lock or unlock the door, write:\n"
					+ "lock door - locks the door\n"
					+ "unlock door - unlocks the door";
		}
	}
	
	public static class PrintSchedulingHelp implements IAction {
		public void execute() {}

		public String toString(Tense tense) {
			return "Scheduling commands\n"
					+ "To schedule a command, write the command followed by \"in m min\" or \"at hh:mm\". Eg.:\n"
					+ "lock the door at 20:00 - locks the door at 20:00.\n"
					+ "turn off the stereo in 15min - turns off the stereo in a quarter hour.\n"
					+ "To cancel a task, write \"cancel\" + the tasks number.\n"
					+ "Write \"list timers\" to list all currently active tasks and their numbers.\n";
		}
	}

	public static class PrintDevices implements IAction {
		public final List<Location> locations;
 
		public PrintDevices(List<Location> locations) {
			this.locations = locations;
		}
		
		public void execute() {}

		public String toString(Tense tense) {
			String result = "Available devices:";

			for (Location location : locations) {
				result += "\n"+location.name;
				
				for (Device device : location.devices) {
					result += "\n\t"+device.name;
				}
			}
			
			return result;
		}
	}
}
