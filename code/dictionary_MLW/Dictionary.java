import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;


public class Dictionary {
    
    private ArrayList<HashSet<String>> dictionaries;
    private static final int NUM_DICT = 72;
    private static final String data_dir = "../../data/dictionary/";

    public Dictionary() {
	// TODO Auto-generated constructor stub
	dictionaries = new ArrayList<HashSet<String>>(NUM_DICT);
	for (int i = 0; i < NUM_DICT; ++i) {
	    HashSet<String> dict = new HashSet<String>();
	    dictionaries.add(i, dict);
	}
	createDictionaries();
	//writeDictionaries();	// comment out if you don't want to write to file.
    }
    
    // write the extracted dictionaries to 72 separate files
    // call constructor and createDictionaires() before that
    private void writeDictionaries() {
	for (int i = 0; i < NUM_DICT; ++i) {
	    //String filename = "harvard"+(i+1)+".txt";
	    String filename = data_dir + "harvard"+(i+1)+".txt";
	    try {
		File file = new File(filename);
		System.out.println("writing file "+filename);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		HashSet<String> dict = dictionaries.get(i);
		for (String word : dict) {
		    bw.write(word+"\n");
		}
		bw.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
    
    // read in harvard_sentences and generate dictionaries
    private void createDictionaries() {
	try {
	    //BufferedReader br = new BufferedReader(new FileReader("harvard_sentences.txt"));
	    BufferedReader br = new BufferedReader(new FileReader(data_dir+"harvard_sentences.txt"));
	    String line;
	    int dict_count = 0;
	    while ((line = br.readLine()) != null) {
		if (line.charAt(0) == 'H') {
		    dict_count++;
		    //System.out.println(line);
		    //System.out.println(dict_count);
		}
		else {
		    StringTokenizer st = new StringTokenizer(line);
		    st.nextToken();	// skip number
		    while (st.hasMoreTokens()) {
			String word = strip_word(st.nextToken());
			dictionaries.get(dict_count-1).add(word);
		    }
		}
	    }
	    br.close();
	} catch (FileNotFoundException e) { 
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	HashSet<String> dict = dictionaries.get(0);
    }

    // Helper method
    // Strip word of ending punctuation marks
    // i.e. "sound." or "sound," => "sound"
    // furthermore, if word contains apostrophe, we strip everything afterthat
    // i.e. "it's" => "it" or "neighbor's" => "neighbor"
    // also do tolowercase.
    private static String strip_word(String s) {
	for (int i = 0; i < s.length(); ++i) {
	    if (s.charAt(i) == '.' || s.charAt(i) == ',' || s.charAt(i) == '\'')
		return s.substring(0, i).toLowerCase();
	}
	return s.toLowerCase();
    }
    
    
    // generate all possible combinations given a list of the triads
    // we assume the triad numbers passed in are from 1~9 (not zero)
    private HashSet<String> generateCombinations(int[] triads) {
	char triad_map[][] = new char[][] {
		{'q','a','w'},	// triad 1
		{'z','s','x'},	// triad 2
		{'e','d','r'},	// triad 3
		{'f','c','v'},	// triad 4
		{'t','g','y'},	// triad 5
		{'h','b','n'},	// triad 6
		{'u','j','i'},	// triad 7
		{'o','l','p'},	// triad 8
		{'k','m','m'},	// triad 9	keep m twice for easier handling, then remove duplicates later
	};
	
	if (triads.length > 15)
	    throw new RuntimeException("Triads length too long");
	for (int i = 0; i < triads.length; ++i) {
	    if (triads[i] < 1 || triads[i] > 9)
		throw new RuntimeException("Triad number value not between 1~9");
	}
	
	int l = triads.length;
	int n = (int) Math.pow(3, triads.length);
	char combinations[][] = new char[n][l+1];
	
	for (int i = 0; i < triads.length; ++i) {	// iterate through number of letters
	    int triad_num = triads[i]-1;
	    for (int j = 0; j < n; ++j) {	// iterate through number of words
		int offset = (j / (int) Math.pow(3, i)) % 3;
		combinations[j][l-1-i] = triad_map[triad_num][offset];
	    }
	}
	for (int j = 0; j < n; ++j)
	    combinations[j][l] = '\0';
	
	HashSet<String> set = new HashSet<String>();
	for (int j = 0; j < n; ++j) {
	    set.add(new String(combinations[j]));
	}
	for (String s : set)
	    System.out.println(s);
	return set;
    }
    
    
    public static void main(String args[]) {
	Dictionary dict = new Dictionary();
	dict.generateCombinations(new int[]{1,1});
    }

}
