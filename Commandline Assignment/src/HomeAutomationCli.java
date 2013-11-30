import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class HomeAutomationCli {
	public final List<Location> locations;
	
	public final Thesaurus thesaurus;
	public final Scheduler scheduler = new Scheduler();
	
	private int babbelCount = 0;
	
	public HomeAutomationCli(List<Location> locations, Thesaurus thesaurus) {
		this.locations = locations;
		this.thesaurus = thesaurus;
	}
	
	public List<Location> extractLocation(String command) {
		ArrayList<Location> result = new ArrayList<Location>();
		
		for (Location location : this.locations) {
			if (thesaurus.matchWithSynonyms(command, location.name)) {
				result.add(location);
			}
		}
				
		return result;
	}
	
	public Map<Location, ArrayList<Device>> extractDevices(String command) {
		List<Location> locations = extractLocation(command);
		HashMap<Location, ArrayList<Device>> result = new HashMap<Location, ArrayList<Device>>();
		
		List<Location> deviceLookupLocations = locations;
		boolean shouldAddAllDevices = true;
		if (locations.size() == 0) {
			deviceLookupLocations = this.locations;
			shouldAddAllDevices = false;
		}
		
		for (Location location : deviceLookupLocations) {
			for (Device device : location.devices) {
				if (this.thesaurus.matchWithSynonyms(command, device.name)) {
					ArrayList<Device> devices = result.get(location);
					
					if (devices == null) {
						devices = new ArrayList<Device>();
						result.put(location, devices);
					}
					
					devices.add(device);
				}
			}
			
			if (result.get(location) == null && shouldAddAllDevices) {
				result.put(location, (ArrayList<Device>) location.devices);
			}
		}
		
		if (result.size() == 0 && this.thesaurus.matchWithSynonyms(command, "everything")) {
			for (Location location : this.locations) {
				result.put(location, new ArrayList<Device>(location.devices));
			}
		}
		
		return result;
	}
	
	public IAction extractHelpAction(String command) {
		if (command.contains("help turn on") || command.contains("help turn off")) {
			return new Action.PrintTurnOnOffHelp();
		}
		
		if (command.contains("help lock") || command.contains("help unlock")) {
			return new Action.PrintLockHelp();
		}
		
		if (command.contains("help scheduling")) {
			return new Action.PrintSchedulingHelp();
		}
		
		if (command.contains("help")) {
			return new Action.PrintHelpAction();
		}
		
		if (command.contains("list devices")) {
			return new Action.PrintDevices(this.locations);
		}
		
		return null;
	}
	
	public IAction extractAction(String command) {
		IAction helpAction = this.extractHelpAction(command);
		if (helpAction != null) return helpAction;
		
		Map<Location, ArrayList<Device>> devicesByLocation = extractDevices(command);
		
		ArrayList<IAction> actions = new ArrayList<IAction>();
		
		for (Location location : devicesByLocation.keySet()) {
			ArrayList<Device> devices = devicesByLocation.get(location);
			
			for (Device device : devices) {
				IAction action = device.parseAction(command, location, this.thesaurus);
				
				if (action != null) actions.add(action);
			}
		}
		
		if (actions.size() == 0) {
			actions.add(new Action.PrintHelpAction());
			/*
			for (Location location : devicesByLocation.keySet()) {
				ArrayList<Device> devices = devicesByLocation.get(location);
				
				for (Device device : devices) {					
					actions.add(device.getDefaultAction(location));
				}
			}*/
		}
		IAction action = null;
		if (actions.size() == 1) action = actions.get(0);
		else action = new Action.MultiAction(actions);
		
		action = this.scheduler.parseCommand(command, action);
		
		return action;
	}
	
	public String executeCommand(String command) {
		command = command.toLowerCase();
		IAction action = this.extractAction(command);
		
		if (action instanceof Action.PrintHelpAction) {
			babbelCount++;

			if (babbelCount == 4) return "Come on, this isn't so hard.";
			if (babbelCount == 5) return "Can you "+command+"? I can't "+command;
			if (babbelCount == 6) return "LALALALALALA I can't hear you LALALALALA";
		} else {
			babbelCount = 0;
		}
		action.execute();
		
		return action.toString(Tense.Past);
	}
}
