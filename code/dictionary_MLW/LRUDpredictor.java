import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Comparator;
import java.util.PriorityQueue;


public class LRUDpredictor {
    
    // the input sequence that we read is in this directory
    private static final String sequence_dir = "../../data/input-words/";
    private static final boolean WANT_SEQUENCE_FOR_TESTING = false;
    private static boolean WANT_WORDS_IN_FILE = false;
    
    HashSet<Character> LeftUp;
    HashSet<Character> LeftDown;
    HashSet<Character> RightUp;
    HashSet<Character> RightDown;

    Character[] left_up_set = new Character[] {'q','w','e','r','t'};
    Character[] left_down_set = new Character[] {'a','s','d','f','g','z','x','c','v','b'};
    Character[] right_up_set = new Character[] {'y','u','i','o','p'};
    Character[] right_down_set = new Character[] {'h','j','k','l','n','m'};
    
    Dictionary dictionary;
    
    public LRUDpredictor() {
	// TODO Auto-generated constructor stub
	LeftUp = new HashSet<Character>(Arrays.asList(left_up_set));
	LeftDown = new HashSet<Character>(Arrays.asList(left_down_set));
	RightUp = new HashSet<Character>(Arrays.asList(right_up_set));
	RightDown = new HashSet<Character>(Arrays.asList(right_down_set));
	
	dictionary = new Dictionary();
    }
    
    /**
     * return all the matching words, that match according to the LRUD sequence
     * @sequence: input sequence, i.e. "lrud"
     * @dict_num: which dictionaries, between 1 ~ 72
     * @return: all words in 1-dict_num dictionaries with the same LRUD sequence 
     */
    private ArrayList<String> getMatchingWords(String sequence, int dict_num) {
	if (dict_num < 1 || dict_num > 72)
	    throw new RuntimeException("Dictionary number is out of range!");
	if (!isValid(sequence))
	    throw new RuntimeException("invalid input sequence!");
	
	ArrayList<String> result = new ArrayList<String>();
	HashSet<String> dict = this.dictionary.getKDictionaries(dict_num);
	for (String entry : dict) {
	    String entry_seq = generateSequence(entry);
	    if (get_distance(sequence, entry_seq) == 0)
		result.add(entry);
	}
	return result;
    }
    
    /**
     * returns the top k closest words in the dictionary specified by dict_num
     * as measured by the distance of their LRUD sequences
     * @param input sequence: same as in getMatchingWords
     * @dict_num: which dictionaries, between 1 ~ 72
     * @param k: how many words we want to return, is upper bound
     * @return: top k closest words measured by LRUD sequence distance
     * in the 1-dict_num dictionaries 
     */
    private ArrayList<String> getClosestWords(String sequence, int dict_num, int k) {
	if (dict_num < 1 || dict_num > 72)
	    throw new RuntimeException("dict_num is out of range!");
	if (!isValid(sequence))
	    throw new RuntimeException("invalid input sequence!");	
	
	HashSet<String> dict = this.dictionary.getKDictionaries(dict_num);
	PriorityQueue<Pair> que = new PriorityQueue<Pair>(k, new Comparator<Pair>(){
	    @Override
	    public int compare(Pair p1, Pair p2) {
	        return (int)Math.signum(p2.getDistance() - p1.getDistance());
	    }
	});
	int distance = 0;
	for (String entry : dict) {
	    String entry_seq = generateSequence(entry);
	    distance = get_distance(sequence, entry_seq);
	    if (distance == Integer.MAX_VALUE) {
	        continue;
	    }
	    que.offer(new Pair(entry, distance));
        if (que.size() == (k + 1)) {
            que.poll();
        }
	}
	
	String[] arr = new String[que.size()];
	for (int i = que.size() - 1; i >= 0; i--) {
	    arr[i] = que.poll().getWord();
	}
	ArrayList<String> result = new ArrayList<String>(Arrays.asList(arr));
	return result;
    }
    
    /**
     * returns the closest (upto k distance) words in the
     * dictionary specified by dict_num
     * as measured by the distance of their LRUD sequences
     * 
     * In case of tie, all the tied words are returned.
     * @param input sequence: same as in getMatchingWords
     * @dict_num: which dictionaries, between 1 ~ 72
     * @param k: how many words we want to return, is upper bound
     * @return: top k closest words measured by LRUD sequence distance
     * in the 1-dict_num dictionaries 
     */
    private ArrayList<String> getClosestDistanceWords(String sequence,
            int dict_num, int k) {
    if (dict_num < 1 || dict_num > 72)
        throw new RuntimeException("dict_num is out of range!");
    if (!isValid(sequence))
        throw new RuntimeException("invalid input sequence!");  
    
    HashSet<String> dict = this.dictionary.getKDictionaries(dict_num);
    HashMap<Integer, ArrayList<String>> list =
            new HashMap<Integer, ArrayList<String>>(); 
    
    int distance = 0;
    for (String entry : dict) {
        String entry_seq = generateSequence(entry);
        distance = get_distance(sequence, entry_seq);
        if (distance == Integer.MAX_VALUE) {
            continue;
        }
        ArrayList<String> word_list = null;
        if (list.containsKey(distance)) {
            word_list = list.get(distance);
        } else {
            word_list = new ArrayList<String>();
            list.put(distance, word_list);
        }
        word_list.add(entry);
    }
    
    ArrayList<String> result = new ArrayList<String>();
    for (int i = 1; i <= k; i++) {
        if (!list.containsKey(i)) {
            continue;
        }
        result.addAll(list.get(i));
    }
    return result;
    }

    
    class Pair {
        String word;
        int distance;
        
        Pair(String word, int distance) {
            this.word = word;
            this.distance = distance;
        }
        
        String getWord() {
            return this.word;
        }
        
        int getDistance() {
            return this.distance;
        }
    }
    
    /*
     * check if sequence is valid. Returns
     */
    private boolean isValid(String sequence) {
	if (sequence.length() % 2 == 1)
	    return false;
	for (int i = 0; i < sequence.length(); ++i) {
	    char c = sequence.charAt(i);
	    if (c != 'l' && c != 'r' && c != 'u' && c!= 'd')
		return false;
	}
	return true;
    }
    
    /* 
     * given a word generate the sequence of L R U D values
     */
    private String generateSequence(String word) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < word.length(); ++i) {
	    char c = word.charAt(i);
	    if (LeftUp.contains(c))
		sb.append("lu");
	    else if (LeftDown.contains(c))
		sb.append("ld");
	    else if (RightUp.contains(c))
		sb.append("ru");
	    else if (RightDown.contains(c))
		sb.append("rd");
	    else
		System.out.println("Error! Character not in any Set!");
	}
	return sb.toString();
    }

    private static int get_distance(String x, String y) {
	if (x.length() != y.length())
	    return Integer.MAX_VALUE;
	if (x.equals(y))
	    return 0;	
	int dist = 0;
	for (int i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) != y.charAt(i))
		dist++;
	}
	return dist;
    }
    
    /*
     * reads the sequence from file
     */
    private String readSequenceFromFile(String filename) {
	String sequence = null;
	try {
	    //BufferedReader br = new BufferedReader(new FileReader(filename));
	    BufferedReader br = new BufferedReader(new FileReader(sequence_dir+filename));
	    sequence = br.readLine();
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return sequence;
    }

    public String getNameFromPath(String str) {
        String tmp = str.substring(str.lastIndexOf('/') + 1);
        String name = tmp.substring(0, tmp.lastIndexOf('.'));
        return name;
    }
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        if (WANT_SEQUENCE_FOR_TESTING) {
            LRUDpredictor seq = new LRUDpredictor();
            if (args.length < 1) {
                System.out.println("Usage: LRUDpredictor word");
                System.exit(1);
            }
            System.out.println(seq.generateSequence(args[0]));
            System.exit(0);
        }
        if (args.length < 3) {
            System.out.println("Usage: LRUDpredictor <filename> <num-dicts-to-search> <num-matches>\n" +
                    "Pass in the filename of the sequence file, number of dictionaries" +
                    "to search for the word in and top-K matches");
            System.out.println("Example: LRUDpredictor ../../data/input-words.arff/akshay.arff 3 5");
            return;
        }
        if (args.length >= 4) {
            if (args[3].equals("true")) {
                WANT_WORDS_IN_FILE = true;
            }
        }
    
	LRUDpredictor pred = new LRUDpredictor();
	String seq = pred.readSequenceFromFile(args[0]);
	    int num_dicts = Integer.parseInt(args[1]);
	    int k = Integer.parseInt(args[2]);
	    System.out.println("file: " + args[0]);
	System.out.println("input sequence: "+seq);
	ArrayList<String> result = pred.getMatchingWords(seq, num_dicts);
    ArrayList<String> result2 = pred.getClosestDistanceWords(seq, num_dicts, k);
	if (!WANT_WORDS_IN_FILE) {
	    System.out.println("Found "+result.size()+" exact match(es):");
    	for (String s : result)
    	    System.out.println(s);
    	
    	System.out.println("Found "+result2.size()+" close match(es):");
    	for (String s : result2) {
    	    int dist = get_distance(seq, pred.generateSequence(s));
    	    System.out.println(s+" "+dist);
    	}
	}
    if (WANT_WORDS_IN_FILE) {
        String path = "../../data/possible-words";
        File outdir = new File(path);
        if (!outdir.exists()) {
            outdir.mkdir();
        }
        String word_label = pred.getNameFromPath(args[0]);
        String outfilename = path + "/" + word_label + ".words";
        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new FileWriter(outfilename)));
        // exact matches to file
        for (String s : result)
            pw.println(s + " 0");
        
        // close matches to file
        for (String s : result2) {
            int dist = get_distance(seq, pred.generateSequence(s));
            pw.println(s + " " + dist);
        }
        pw.close();
    }
    }
}
