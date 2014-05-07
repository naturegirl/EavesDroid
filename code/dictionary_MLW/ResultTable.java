import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;


/*
 * Used to get the top k results that match a given triad sequence 
 */

public class ResultTable {
    
    private Hashtable<String, Integer> table;
    
    private class Entry implements Comparable<Entry> {
	private int distance;
	private String word;
	
	public Entry(int distance, String word) {
	    this.distance = distance;
	    this.word = word;
	}
	
	@Override
	public int compareTo(Entry that) {
	    return this.distance - that.distance;
	}
	
	public String getWord() {
	    return word;
	}
	public int getDistance() {
	    return distance;
	}
    }

    public ResultTable() {
	// TODO Auto-generated constructor stub
	table = new Hashtable<String, Integer>();
    }
    
    /*
     * insert a new potential word into ResultTable
     * insert new word or
     * overwrite existing word if distance is shorter
     */
    public void insert(String word, int distance) {
	if (!table.containsKey(word))
	    table.put(word, distance);
	else if (table.containsKey(word) && (distance < table.get(word)))
	    table.put(word, distance);
    }

    /*
     * returns the top k suggestions with closest distance
     */
    public ArrayList<String> getTopKSuggestions(int k) {
	System.out.println("table size: "+table.size());
	Entry[] a = new Entry[table.size()];
	int i = 0;
	for (String key : table.keySet()) {
	    a[i++] = new Entry(table.get(key), key);
	}
	Arrays.sort(a);
	ArrayList<String> result = new ArrayList<String>(k);
	for (i = 0; i < Math.min(k, table.size()); ++i) {
	    result.add(a[i].getWord());
	    System.out.println("adding "+a[i].getWord() + " " + a[i].getDistance());
	}
	return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
