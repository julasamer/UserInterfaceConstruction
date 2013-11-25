import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CommandLineInterfaceTest {
	public static void main(String[] args) {
		new CommandLineInterfaceTest();
	}
	
	public static <T> List<T> CreateList(T... things) {
		ArrayList<T> list = new ArrayList<T>();
		
		for (T thing : things) {
			list.add(thing);
		}
		
		return list;
	}
	
	public static <T> Set<T> CreateSet(T... things) {
		HashSet<T> set = new HashSet<T>();
		
		for (T thing : things) {
			set.add(thing);
		}
		
		return set;
	}
	
	public final HomeAutomationCli cli;
	
	public CommandLineInterfaceTest() {
		HashSet<Action.ActionType> defaultActions = new HashSet<Action.ActionType>();
		defaultActions.add(Action.ActionType.TriggerOn);
		defaultActions.add(Action.ActionType.TriggerOff);
		defaultActions.add(Action.ActionType.State);
		
		Location kitchen = new Location("kitchen", CreateList(
				new Device("oven", true, defaultActions), 
				new Device("lights", true, defaultActions), 
				new Device("refrigerator", true, defaultActions), 
				new Device("fire alarm", true, defaultActions)));
		
		Location livingRoom = new Location("living room", CreateList(
				new Device("tv", true, defaultActions),
				new Device("lights", true, defaultActions),
				new Device("stereo", false, defaultActions)));
		
		Location sauna = new Location("sauna", CreateList(new Device("oven", false, defaultActions)));
		
		Location bathroom = new Location("bathroom", CreateList(
				new Device("washing machine", true, defaultActions), 
				new Device("tumble dryer", true, defaultActions),
				new Device("lights", true, defaultActions)));
		
		Location hallway = new Location("hallway", CreateList(new Device("door", true, defaultActions)));
		
		Set<String> lightSynonyms = CreateSet("lights", "lamps");
		
		List<Set<String>> synonyms = CreateList(lightSynonyms);
		
		this.cli = new HomeAutomationCli(CreateList(kitchen, livingRoom, sauna, bathroom, hallway), synonyms);
		
		this.start();
	}

	public void start() {
		System.out.println("Enter a command. (Enter \"help\" for a list of commands)");
		
		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					
			try {
				String commandText = br.readLine();
				System.out.println(this.cli.executeCommand(commandText));
			} catch (IOException e) {
				System.out.println("Some error occured!");
			}
		}
	}
}
