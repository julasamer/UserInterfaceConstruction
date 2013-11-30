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
		
		Location kitchen = new Location("kitchen", CreateList(
				new Device("oven", true), 
				new Device("lights", true), 
				new Device("refrigerator", true), 
				new Device("fire alarm", true)));
		
		Location livingRoom = new Location("living room", CreateList(
				new Device("tv", true),
				new Device("lights", true),
				new Device("stereo", false)));
		
		Location sauna = new Location("sauna", CreateList(new Device("oven", false)));
		
		Location bathroom = new Location("bathroom", CreateList(
				new Device("washing machine", true), 
				new Device("tumble dryer", true),
				new Device("lights", true)));
		
		List<Device> stuff = CreateList((Device)new Door("door", true));
		Location hallway = new Location("hallway", stuff);
		
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
