import java.util.*;
import java.io.*;
import java.lang.*;
public class word_generator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    if (args.length != 1) {
            System.out.println("Usage: java rword_labaler <word>"
                    );
            System.out.println("Example: java random_labeler garbage");
            System.exit(0);
        }
        
        String word = args[0].toUpperCase();
        
        try {
        	System.out.println("*****************");
        	Thread.sleep(2000);
            for (int i = 0; i < word.length(); ++i) {
            	System.out.println(word.charAt(i)+"    --------   count: "+(i+1));
            	Thread.sleep(3000);
            }
        } catch (InterruptedException e) {}
	}

}
