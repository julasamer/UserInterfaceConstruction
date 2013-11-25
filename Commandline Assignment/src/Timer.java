import java.util.Date;

public class Timer extends Action {
	public final Date triggerTime;
	public final Action action;
	
	public Timer(Date date, Action action) {
		super(action.devices);
		
		this.triggerTime = date;
		this.action = action;
	}
	
	public String toString() {
		return "Scheduled timer for"+this.triggerTime+":\n"+ this.action.toString() + " at "+this.triggerTime;
	}

	@Override
	public void execute() {
		// TODO: schedule this thing somehow
	}
}
