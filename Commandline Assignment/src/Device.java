import java.util.Set;

public class Device {
	public final String name;
	public final Set<Action.ActionType> supportedActions;
	
	private boolean enabled;
	
	public Device(String name, boolean enabled, Set<Action.ActionType> supportedActions) {
		this.name = name;
		this.enabled = enabled;
		this.supportedActions = supportedActions;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public Action.ActionType parseActionType(String command, Location location) {
		if (command.contains("turn on")) {
			return Action.ActionType.TriggerOn;
		}
		
		if (command.contains("turn off")) {
			return Action.ActionType.TriggerOff;
		}
		
		if (command.contains("state")) {
			return Action.ActionType.State;
		}
		
		return Action.ActionType.Unknown;
	}
}
