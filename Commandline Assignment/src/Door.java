
public class Door extends Device {
	public Door(String name, boolean enabled) {
		super(name, enabled);
	}
	
	public void setLockState(boolean locked) {
		this.setEnabled(locked);
	}
	
	public IAction parseAction(String command, Location location) {
		if (command.contains("unlock")) {
			return new IAction() {
				public void execute() {
					setLockState(false);
				}
				public String toString(Tense tense) {
					return (tense == Tense.Past ? "unlocked" : "will unlock") + " the " + Door.this.name; 
				}
			};
		}
		
		if (command.contains("lock")) {
			return new IAction() {
				public void execute() {
					setLockState(true);
				}
				public String toString(Tense tense) {
					return (tense == Tense.Past ? "locked" : "will lock") + " the " + Door.this.name; 
				}
			};
		}
		
		if (command.contains("state")) {
			return new Action.GetStateAction(this, location);
		}
		
		return null;
	}
}
