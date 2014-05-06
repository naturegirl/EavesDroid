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
	writeDictionaries();	// comment out if you don't want to write to file.
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
	
	for (String w : dict) {
	    System.out.println(w);
	}
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
    
    public static void main(String args[]) {
	Dictionary dict = new Dictionary();
	dict.createDictionaries();
	dict.writeDictionaries();
    }

}
