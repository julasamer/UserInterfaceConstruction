public class Device {
	public final String name;
	
	private boolean enabled;
	
	public Device(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public IAction getDefaultAction(Location location) {
		return new Action.GetStateAction(this, location);
	}
	
	public IAction parseAction(String command, Location location) {
		if (command.contains("turn on")) {
			return new Action.TriggerAction(this, location, true);
		}
		
		if (command.contains("turn off")) {
			return new Action.TriggerAction(this, location, false);
		}
		
		if (command.contains("state")) {
			return new Action.GetStateAction(this, location);
		}
		
		return null;
	}
}
