import java.util.List;
import java.util.Set;


public class Thesaurus {
	public List<Set<String>> synonyms;

	public Thesaurus(List<Set<String>> synonyms) {
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
}
