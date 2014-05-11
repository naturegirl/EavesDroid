
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GenParagraphArff {

    HashMap<Character, ArrayList<String>> lettersFeatures;
    String heading;
    String template;
    int unique;
    
    GenParagraphArff() {
        this.lettersFeatures = new HashMap<Character, ArrayList<String>>();
        this.heading = "";
        this.template = "";
        this.unique = 0;
    }
    
    public void readTemplate(File template_file) throws FileNotFoundException {
        Scanner scanner = new Scanner(template_file);
        while (scanner.hasNextLine()) {
            this.template += scanner.nextLine() + "\n";
        }
        scanner.close();
    }
    
    public void fetchAllFeatures(File file)
            throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        this.heading = scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int last_comma = line.lastIndexOf(',');
            int int_label = Integer.parseInt(line.substring(last_comma + 1));
            char label = (char)(int_label - 1 + 'A' + 32);
            ArrayList<String> features = null;
            if (this.lettersFeatures.containsKey(label)) {
                features = this.lettersFeatures.get(label);
            } else {
                features = new ArrayList<String>();
                this.lettersFeatures.put(label, features);
            }
            features.add(line.substring(0, last_comma));
        }
        scanner.close();
    }
    
    public void arffWord(String word, String path) throws IOException {
        String features = "";
        Random rand = new Random();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            ArrayList<String> possible_features = this.lettersFeatures.get(ch);
            int index = rand.nextInt(possible_features.size());
            features += possible_features.get(index) + "\n";
        }
        String arff_filename = path + "/" + word + "." + unique++ + ".arff";
        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new FileWriter(arff_filename)));
        pw.print(this.template);
        pw.print(features);
        pw.close();
    }
    
    public void processInputParagraph(String path) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String word = scanner.next().toLowerCase();
            this.arffWord(word, path);
        }
        scanner.close();
    }
    
    public void deleteDirectory(File dir) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files.length == 0) {
            dir.delete();
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        dir.delete();
    }
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        String features_file =
                "../../data/features/all-letters-labeled.csv";
        String template_file = "../scripts/template.arff";
        String outdir_path = "../../data/paragraph_arff";
        GenParagraphArff ob = new GenParagraphArff();
        File outdir = new File(outdir_path);
        ob.deleteDirectory(outdir);
        outdir.mkdir();
        ob.fetchAllFeatures(new File(features_file));
        for (Character ch : ob.lettersFeatures.keySet()) {
            System.out.println(ch + ": " + ob.lettersFeatures.get(ch).size());
            //System.out.println("features: " + ob.lettersFeatures.get(ch).get(0));
        }
        ob.readTemplate(new File(template_file));
        System.out.println("template: " + ob.template);
        ob.processInputParagraph(outdir_path);
    }

}
