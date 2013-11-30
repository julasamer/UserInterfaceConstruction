
public class Door extends Device {
	public Door(String name, boolean enabled) {
		super(name, enabled);
	}
	
	public void setLockState(boolean locked) {
		this.setEnabled(locked);
	}
	
	public IAction parseAction(String command, Location location, Thesaurus thesaurus) {
		if (thesaurus.matchWithSynonyms(command, "unlock")) {
			return new IAction() {
				public void execute() {
					setLockState(false);
				}
				public String toString(Tense tense) {
					return (tense == Tense.Past ? "unlocked" : "will unlock") + " the " + Door.this.name; 
				}
			};
		}
		
		if (thesaurus.matchWithSynonyms(command, "lock")) {
			return new IAction() {
				public void execute() {
					setLockState(true);
				}
				public String toString(Tense tense) {
					return (tense == Tense.Past ? "locked" : "will lock") + " the " + Door.this.name; 
				}
			};
		}
		
		if (thesaurus.matchWithSynonyms(command, "state")) {
			return new IAction() {
				public String toString(Tense tense) {
					if (isEnabled()) {
						return name+" is locked.";
					} else {
						return name+" is unlocked.";
					}
				}
				
				public void execute() {}
			};
		}
		
		return null;
	}
}
