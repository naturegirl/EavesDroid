import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;


public class NYTimesDiff {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        //String nytimes = "../../data/words-nytimes";
        //String harvard = "../../data/words-harvard";
        String nytimes = "../../data/nytimes-article.txt";
        String harvard = "../../data/dictionary/combined_harvard.txt";
        
        Scanner scan1 = new Scanner(new File(nytimes));
        Scanner scan2 = new Scanner(new File(harvard));
        HashSet<String> words_nytimes = new HashSet<String>();
        while (scan1.hasNext()) {
            words_nytimes.add(scan1.next());
        }
        HashSet<String> words_harvard = new HashSet<String>();
        while (scan2.hasNext()) {
            words_harvard.add(scan2.next());
        }
        
        HashSet<String> nytimes_all = (HashSet<String>) words_nytimes.clone();
        
        System.out.println("NYTimes = " + words_nytimes.size());
        words_nytimes.removeAll(words_harvard);
        System.out.println("Harvard = " + words_harvard.size());
        System.out.println("Size = " + words_nytimes.size());
//        nytimes_all.removeAll(words_nytimes);
        for (String str : nytimes_all) {
            System.out.println(str);
        }
        scan1.close();
        scan2.close();
    }

}
