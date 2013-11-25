import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeAutomationCli {
	public final List<Location> locations;
	
	public List<Set<String>> synonyms;
	public Set<String> ignoredWords;
	
	public HomeAutomationCli(List<Location> locations, List<Set<String>> synonyms) {
		this.locations = locations;
		this.synonyms = synonyms;
	}
	
	public boolean matchWithSynonyms(String command, String word) {
		if (command.contains(word)) return true;
		
		for (Set<String> synonyms : this.synonyms) {
			if (synonyms.contains(word)) {
				for (String synonym : synonyms) {
					if (command.matches(".*"+synonym+".*")) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public List<Location> extractLocation(String command) {
		ArrayList<Location> result = new ArrayList<Location>();
		
		for (Location location : this.locations) {
			if (matchWithSynonyms(command, location.name)) {
				result.add(location);
			}
		}
		
		if (result.size() == 0) {
			result = new ArrayList<Location>(this.locations);
		}
				
		return result;
	}
	
	public Map<Location, ArrayList<Device>> extractDevices(String command) {
		List<Location> locations = extractLocation(command);
		HashMap<Location, ArrayList<Device>> result = new HashMap<Location, ArrayList<Device>>();
		
		for (Location location : locations) {
			for (Device device : location.devices) {
				if (matchWithSynonyms(command, device.name)) {
					ArrayList<Device> devices = result.get(location);
					
					if (devices == null) {
						devices = new ArrayList<Device>();
						result.put(location, devices);
					}
					
					devices.add(device);
				}
			}
		}
		
		if (result.values().size() == 0) {
			for (Location location : locations) {
				result.put(location, new ArrayList<Device>(location.devices));
			}
		}
		
		return result;
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
	
	public Action extractAction(String command) {
		Map<Location, ArrayList<Device>> devicesByLocation = extractDevices(command);
				
		Action.ActionType type = Action.ActionType.Unknown;
		
		// Turn on/off:
		for (Location location : devicesByLocation.keySet()) {
			ArrayList<Device> devices = devicesByLocation.get(location);
			
			for (Device device : devices) {
				Action.ActionType parsedType = device.parseActionType(command, location);
				
				if (parsedType == Action.ActionType.Unknown) throw new IllegalStateException("Unknown action.");
				
				if (type == Action.ActionType.Unknown) {
					type = parsedType;
				} else {
					if (type != parsedType) throw new IllegalStateException("Action types do not match.");
				}
			}
		}
		
		Action action = Action.createAction(devicesByLocation, type);
		
		Date date = this.extractDate(command);
		if (date != null) {
			action = new Timer(date, action);
		}
		
		// TODO: create timer action if there is a date in the description
		
		return action;
	}
	
	public String executeCommand(String command) {
		Action action = this.extractAction(command);
		
		action.execute();
		
		return action.toString();
	}
}
