import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Scheduler {
	private HashMap<Integer, Timer> timersById = new HashMap<Integer, Timer>();
	private int currentId = 1;
	
	public void scheduleTimer(Timer timer) {
		timersById.put(currentId++, timer);
	}
	
	public boolean cancelTimer(int id) {
		Timer timer = timersById.get(id);
		
		if (timer != null) {
			timersById.remove(id);
			return true;
		}
		
		return false;
	}
	

	public Date extractAbsoluteDate(String command) {
		Pattern pattern = Pattern.compile("at\\s(\\d?\\d):(\\d\\d)");
	    Matcher matcher = pattern.matcher(command);
	    
	    while (matcher.find()) {	    	
	    	String hours = command.substring(matcher.start(1), matcher.end(1));
	    	String minutes = command.substring(matcher.start(2), matcher.end(2));
	    		    	
	    	int h = Integer.parseInt(hours);
	    	int min = Integer.parseInt(minutes);
	    	
	    	Date now = new Date();
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(now);
	    	calendar.set(Calendar.HOUR, h);
	    	calendar.set(Calendar.MINUTE, min);
	    	calendar.set(Calendar.SECOND, 0);
	    	
	    	if (calendar.getTime().before(now)) {
	    		calendar.add(Calendar.DATE, 1);
	    	}
	    	
	    	return calendar.getTime();
	    }
	    
	    return null;
	}
	
	public Date extractRelativeDate(String command) {
		Pattern pattern = Pattern.compile("in\\s(\\d+)\\s?min");
	    Matcher matcher = pattern.matcher(command);
	    
	    while (matcher.find()) {
	    	String minutes = command.substring(matcher.start(1), matcher.end(1));
	    		    	
	    	int min = Integer.parseInt(minutes);
	    	
	    	Date now = new Date();
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(now);
	    	calendar.add(Calendar.MINUTE, min);
	    	
	    	return calendar.getTime();
	    }
    
	    return null;
	}
	
	public Date extractDate(String command) {
		Date absoluteDate = extractAbsoluteDate(command);
		if (absoluteDate != null) return absoluteDate;
		
		Date relativeDate = extractRelativeDate(command);
		if (relativeDate != null) return relativeDate;
 
		return null;
	}
	
	public IAction parseCommand(String command, IAction parsedAction) {
		Pattern pattern = Pattern.compile("cancel timer (\\d+)");
	    Matcher matcher = pattern.matcher(command);
	    
	    while (matcher.find()) {
	    	String minutes = command.substring(matcher.start(1), matcher.end(1));
	    		    	
	    	int id = Integer.parseInt(minutes);
	    	
	    	return new CancelTimerAction(id);
	    }
    
	    if (command.contains("list timers")) {
	    	return new ListTimersAction();
	    }
	    
	    Date date = extractDate(command);
	    if (date != null && parsedAction != null) {
	    	return new Timer(date, parsedAction);
	    }
	    
	    return parsedAction;
	}
	
	public class ListTimersAction implements IAction {
		public void execute() {	}

		public String toString(Tense tense) {
			Set<Integer> ids = timersById.keySet();
			
			List<Integer> sortedIds = new ArrayList<Integer>(ids);
			Collections.sort(sortedIds);
			
			if (sortedIds.size() == 0) return "There are no active timers.";
			
			String result = "["+sortedIds.get(0)+"]: "+ timersById.get(sortedIds.get(0)).toString(Tense.Past);
			
			for (int index = 1; index < sortedIds.size(); index++) {
				int id = sortedIds.get(index);
				
				result += "\n["+id+"]: "+timersById.get(sortedIds.get(index)).toString(Tense.Past);
			}
			
			return result;
		}
	}
	
	public class CancelTimerAction implements IAction {
		public final int id;
		private String timerDescription = null;
		
		public CancelTimerAction(int id) {
			this.id = id;
		}
		
		public void execute() {
			Timer t = timersById.get(id);
			
			if (t != null) this.timerDescription = t.toString(Tense.Past);
			
			cancelTimer(id);
		}

		@Override
		public String toString(Tense tense) {
			if (this.timerDescription != null) {
				return "Canceled timer:\n"+this.timerDescription;
			} else {
				return "Could not cancel timer, there is no timer with id "+this.id+".";
			}
		}
	}
	
	public class Timer implements IAction {
		public final Date triggerTime;
		public final IAction action;
		
		public Timer(Date date, IAction action2) {		
			this.triggerTime = date;
			this.action = action2;
		}
		
		public String toString(Tense tense) {
			String result = "Scheduled timer for "+this.triggerTime+":\n"+ this.action.toString(Tense.Future);
		
			result = result.replace("\n", "\n\t");
			
			return result;
		}

		@Override
		public void execute() {
			scheduleTimer(this);
		}
	}

}
