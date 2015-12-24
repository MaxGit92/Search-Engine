package modeles;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
	Map<String, Double> base;
	public ValueComparator(Map<String, Double> base) {
	this.base = base;
}
	@Override
	public int compare(String o1, String o2) {
		if(base.get(o1) <= base.get(o2)){
			return 1;
		}
		return -1;
	}
}
