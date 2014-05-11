
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class GenTrainingTesting {

    HashSet<String> training_lr;
    HashSet<String> training_ud;
    HashMap<Character, ArrayList<String>> testing;
    
    String heading;
    String template_lr;
    public static final double FRACTION = 0.66;
    
    GenTrainingTesting() {
        this.training_lr = new HashSet<String>();
        this.training_ud = new HashSet<String>();
        this.testing = new HashMap<Character, ArrayList<String>>();
        this.heading = "";
        this.template_lr = "";
    }
    
    public void readTemplateLR(File template_file) throws FileNotFoundException {
        Scanner scanner = new Scanner(template_file);
        while (scanner.hasNextLine()) {
            this.template_lr += scanner.nextLine() + "\n";
        }
        scanner.close();
    }

    public void fetchAllFeatures(File file)
            throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        this.heading = scanner.nextLine();
        Random rand = new Random();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int last_comma = line.lastIndexOf(',');
            int int_label = Integer.parseInt(line.substring(last_comma + 1));
            char az_label = (char)(int_label - 1 + 'A' + 32);
            String data = line.substring(0, last_comma);
            if (rand.nextDouble() <= FRACTION) {
                this.training_lr.add(data + "," + this.getLRLabel(az_label));
                this.training_ud.add(data + "," + this.getUDLabel(az_label));
            } else {
                ArrayList<String> features = null;
                if (this.testing.containsKey(az_label)) {
                    features = this.testing.get(az_label);
                } else {
                    features = new ArrayList<String>();
                    this.testing.put(az_label, features);
                }
                features.add(data);
            }
        }
        scanner.close();
    }
    
    private int getUDLabel(char ch) {
        char[] up = {'q','w','e','r','t','y','u','i','o','p'};
        char[] down = {'a','s','d','f','g','h','j','k','l','z','x','c','v',
                        'b','n','m'};
        for (int i = 0; i < up.length; i++) {
            if (up[i] == ch) {
                return 1;   // U
            }
        }
        for (int i = 0; i < down.length; i++) {
            if (down[i] == ch) {
                return 2;   // D
            }
        }
        return 0;
    }

    private int getLRLabel(char ch) {
        char[] left = {'a','b','c','d','e','f','g','q',
                        'r','s','t','v','w','x','z'};
        char[] right = {'h','i','j','k','l','m','n','o','p','u','y'};
        for (int i = 0; i < left.length; i++) {
            if (left[i] == ch) {
                return 1;   // L
            }
        }
        for (int i = 0; i < right.length; i++) {
            if (right[i] == ch) {
                return 2;   // R
            }
        }
        return 0;
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
        String template_file_lr = "../scripts/template_lr.arff";
        String outdir_path = "../../data/dataset";
        GenTrainingTesting ob = new GenTrainingTesting();
        File outdir = new File(outdir_path);
        ob.deleteDirectory(outdir);
        outdir.mkdir();
        ob.fetchAllFeatures(new File(features_file));
        ob.readTemplateLR(new File(template_file_lr));
        ob.writeTrainingTesting(outdir_path);
    }
    
    private void writeTrainingTesting(String path) throws IOException {
        String training_lr = path + "/training.lr.arff";
        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new FileWriter(training_lr)));
        pw.println(this.template_lr);
        for (String data : this.training_lr) {
            pw.println(data);
        }
        pw.close();
        
        String training_ud = path + "/training.ud.arff";
        pw = new PrintWriter(new BufferedWriter(
                new FileWriter(training_ud)));
        pw.println(this.template_lr);
        for (String data : this.training_ud) {
            pw.println(data);
        }
        pw.close();

        String testing = path + "/testing.csv";
        pw = new PrintWriter(new BufferedWriter(
                new FileWriter(testing)));
        pw.println(this.heading);
        for (Character key : this.testing.keySet()) {
            int label = key - 'a' + 1;
            for (String data : this.testing.get(key)) {
                pw.println(data + "," + label);
            }
        }
        pw.close();
}

}
