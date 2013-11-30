import java.util.Date;

public class Timer implements IAction {
	public final Date triggerTime;
	public final IAction action;
	
	public Timer(Date date, IAction action2) {		
		this.triggerTime = date;
		this.action = action2;
	}
	
	public String toString(Tense tense) {
		return "Scheduled timer for "+this.triggerTime+":\n"+ this.action.toString(Tense.Future);
	}

	@Override
	public void execute() {
		// TODO: schedule this thing somehow
	}
}
