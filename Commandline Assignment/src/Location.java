import java.util.List;

public class Location {
	public final String name;
	public final List<Device> devices;
	
	public Location(String name, List<Device> devices) {
		this.name = name;
		this.devices = devices;
	}
}
