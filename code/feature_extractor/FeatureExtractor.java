import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author aks0
 *
 */
public class FeatureExtractor {
    
    public static final double G = 9.81;
    public static final BigInteger FACTOR = new BigInteger("1000");
    public static boolean WANT_GFORCE_DATA = false;
    public static boolean WANT_LR_LABEL = false;
    public static boolean WANT_UP_LABEL = false;
    public static final boolean WANT_PAIRED_LABEL = false;
    public static boolean WANT_TRIAD_LABEL = false;
    public static final boolean WANT_SEPTET_LABEL = false;
    public static BigInteger BEFORE_THRESH = new BigInteger("40000"); // 40ms
    public static BigInteger AFTER_THRESH = new BigInteger("85000"); // 85ms
    public static final boolean WANT_WINDOW_SIGNAL = false;
    private String inputDir = null;
    
    private static ArrayList<Features> featuresList;

    private void parseCommandLineArgs(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java FeatureExtractor <dir-name>");
            System.out.println("Example: java FeatureExtractor data");
            System.out.println("for details: java FeatureExtractor --help");
            System.exit(0);
        }
        ArrayList<String> cmd_args = new ArrayList<String>(Arrays.asList(args));
        if (cmd_args.contains("-h")) {
            this.printHelpMessage();
            System.exit(0);
        } else {
            int index = cmd_args.indexOf("-d");
            if (index == -1) {
                System.out.println("No directory name provided.");
                this.printHelpMessage();
                System.exit(0);
            }
            this.inputDir = cmd_args.get(index + 1);
            File dir = new File("../../data/" + inputDir);
            if (!dir.exists() || !dir.isDirectory()) {
                System.out.println(this.inputDir + " is not a directory.");
                System.exit(0);
            }
        }
        if (cmd_args.contains("-gforce")) {
            WANT_GFORCE_DATA = true;
        }
        if (cmd_args.contains("-label")) {
            int index = cmd_args.indexOf("-label");
            String label_type = cmd_args.get(index + 1);
            if (label_type.equals("lr")) {
                WANT_LR_LABEL = true;
            } else if (label_type.equals("ud")) {
                WANT_UP_LABEL = true;
            } else if (label_type.equals("triad")) {
                WANT_TRIAD_LABEL = true;
            }
        }
    }

    private void printHelpMessage() {
        System.out.println("Usage: java FeatureExtractor <dir-name>");
        System.out.println("Example: java FeatureExtractor data");
        System.out.println("for details: java FeatureExtractor --help");
        System.out.println("Arguments:\n");
        System.out.println("\t-d <dir-name>\tthe directory which you want" +
                " to process");
        System.out.println("\t-h\t\tdisplays this message");
        System.out.println("\t-gforce\t\tgenerates gforce values in addition" +
                " to the features");
        System.out.println("\t-label <arg>\tthe label that you want:\n\t\t\tlr "
                + "for L/R labels,\n\t\t\tud for U/D labels,\n\t\t\ttriad for" +
                " Triad labelling\n\t\t\t" +
                "It defaults to 26 alphabet labelling.");
    }

    public static void main(String[] args) throws IOException {
        FeatureExtractor ob = new FeatureExtractor();
        ob.parseCommandLineArgs(args);
        String path = "../../data/" + ob.inputDir;
        File directory = new File(path);
        featuresList = new ArrayList<Features>();
        ob.processKeyPresses(directory);
        String featuresFile = "../../data/features/" + args[0] + ".csv";
        ob.writeToFile(featuresList, featuresFile);
    }
    
    /**
     * Recursively traverse the directory of key presses to extract the
     * features for the letters' signals and label them correctly
     * 
     * @param directory
     * @throws IOException 
     */
    private void processKeyPresses(File directory) throws IOException {
        File[] files = directory.listFiles();
        int label = this.getLabel(directory.getName());

        for (File file: files) {
            if (file.getAbsolutePath().endsWith(".csv")) {
                ArrayList<Signal> signals = this.readCSV(file);
                //this.smoothGForce(signals);
                //this.stripSignalHead(signals);
                //this.stripSignalTail(signals);
                if (WANT_WINDOW_SIGNAL) {
                    signals = this.getWindowSignal(signals);
                }
                // write g-force's to file
                if (WANT_GFORCE_DATA) {
                    String filepath = file.getAbsolutePath();
                    this.writeGForceToFile(
                            signals,
                            filepath.substring(0, filepath.length()-4) +
                            ".gforce.csv"
                            );
                }
                Features features = this.getFeatures(signals);
                features.setLabel(label);
                featuresList.add(features);
            } else if (file.isDirectory()) {
                this.processKeyPresses(file);
            }
        }
    }
    
    private ArrayList<Signal> getWindowSignal(ArrayList<Signal> signals) {
        moveToBaseReference(signals);
        
        @SuppressWarnings("unchecked")
        ArrayList<Signal> sorted_signals = (ArrayList<Signal>) signals.clone();
        // reverse sorted according to the absolute values of the g-force values
        Collections.sort(sorted_signals, new Comparator<Signal>() {
            public int compare(Signal signal1, Signal signal2) {
                double force1 = Math.abs(signal1.getGForce());
                double force2 = Math.abs(signal2.getGForce());
                double diff = force1 - force2;
                return -(int)Math.signum(diff);
            }
        });
        int max_index = signals.indexOf(sorted_signals.get(0));
        ArrayList<Signal> letter = this.removeAroundMax(signals, max_index);
        return letter;
    }
    
    private ArrayList<Signal> shiftRelativeToOrigin(ArrayList<Signal> signals) {
        BigInteger start_time = signals.get(0).getTimeStamp();
        ArrayList<Signal> new_signals = new ArrayList<Signal>();
        for(Signal signal : signals) {
            BigInteger new_timestamp =
                    signal.getTimeStamp().subtract(start_time);
            Signal new_signal = new Signal(new_timestamp,
                    signal.getX(), signal.getY(), signal.getZ());
            new_signals.add(new_signal);
        }
        return new_signals;
    }
    
    public static void moveToBaseReference(ArrayList<Signal> signals) {
        double sum = 0;
        for (int i = 0; i < signals.size(); i++) {
            sum += signals.get(i).getGForce();
        }
        double mean = sum/signals.size();
        for (Signal signal : signals) {
            signal.setGForce(signal.getGForce() - mean);
        }
    }

    private ArrayList<Signal> removeAroundMax(ArrayList<Signal> signals,
            int max_index){
        BigInteger peak_time_stamp = signals.get(max_index).getTimeStamp();
        
        // letter start index
        int start_cut_off_index = 0;
        for (int i = max_index; i >= 0; i--) {
            BigInteger diff = signals.get(i).getTimeStamp().subtract(
                    peak_time_stamp).abs();
            start_cut_off_index = i;
            if (diff.compareTo(BEFORE_THRESH) > 0) {
                break;
            }
        }
        
        // letter end index
        int end_cut_off_index = 0;
        for (int i = max_index; i < signals.size(); i++) {
            BigInteger diff = signals.get(i).getTimeStamp().subtract(
                    peak_time_stamp).abs(); 
            end_cut_off_index = i;
            if (diff.compareTo(AFTER_THRESH) > 0) {
                break;
            }
        }
        
        ArrayList<Signal> letter_signal = new ArrayList<Signal>();
        for (int i = start_cut_off_index; i <= end_cut_off_index; i++) {
            letter_signal.add(signals.get(i));
        }

        return letter_signal;
    }

    private void stripSignalHead(ArrayList<Signal> signals) {
        int max_index = 0;
        int min_index = 0;
        for(int i = 0; i < signals.size(); i++){
            Signal signal = signals.get(i);
            if (signal.getGForce() > signals.get(max_index).getGForce()) {
                max_index = i;
            }
            if (signal.getGForce() < signals.get(min_index).getGForce()) {
                min_index = i;
            }
        }
        int peak_index = Math.min(max_index, min_index);
        BigInteger peak_timestamp = signals.get(peak_index).getTimeStamp();
        BigInteger threshold = new BigInteger("500000"); // 500ms
        int cut_off_index = 0;
        for (int i = peak_index; i >= 0; i--) {
            BigInteger diff = signals.get(i).getTimeStamp().subtract(
                    peak_timestamp).abs(); 
            if (diff.compareTo(threshold) > 0) {
                cut_off_index = i;
                break;
            }
        }
        for (int i = cut_off_index; i >= 0; i--) {
            signals.remove(i);
        }
    }
    
    private void stripSignalTail(ArrayList<Signal> signals) {
        double sum = 0;
        int n = 100;
        for (int i = signals.size()-n; i < signals.size(); i++) {
            sum += signals.get(i).getGForce();
        }
        double mean = sum / n;
        double sq_diff_sum = 0;
        for (int i = signals.size()-n; i < signals.size(); i++) {
            sq_diff_sum += Math.pow(signals.get(i).getGForce()-mean,2);
        }
        double std_dev = Math.sqrt(sq_diff_sum/n);
        System.out.println("Mean = " + mean + "\nStd-dev = " + std_dev);
    }
    
    private int getLabel(String dirName) {
        if (WANT_LR_LABEL) {
            if (dirName.equals("enter") || dirName.equals("space")) {
                return 0;
            }
            return this.getLRLabel(dirName.charAt(0));
        }
        if (WANT_UP_LABEL) {
            if (dirName.equals("enter") || dirName.equals("space")) {
                return 0;
            }
            return this.getUDLabel(dirName.charAt(0));
        }
        if (WANT_PAIRED_LABEL) {
            if (dirName.equals("enter") || dirName.equals("space")) {
                return 0;
            }
            return this.getPairedLabel(dirName.charAt(0));
        }
        if (WANT_TRIAD_LABEL) {
            if (dirName.equals("enter") || dirName.equals("space")) {
                return 0;
            }
            return this.getTriadLabel(dirName.charAt(0));
        }
        if (WANT_SEPTET_LABEL) {
            if (dirName.equals("enter") || dirName.equals("space")) {
                return 0;
            }
            return this.getSeptetLabel(dirName.charAt(0));
        }
        if (dirName.equals("enter")) {
            return 27;
        } else if (dirName.equals("space")) {
            return 28;
        }
        char letter = dirName.toLowerCase().charAt(0);
        return (letter - 'a' + 1);
    }
    
    private int getSeptetLabel(char ch) {
        switch(ch) {
        case 'q':
        case 'w':
        case 'e':
        case 'a':
        case 's':
        case 'z':
        case 'x': return 1;
        case 'r':
        case 't':
        case 'd':
        case 'f':
        case 'g':
        case 'c':
        case 'v': return 2;
        case 'y':
        case 'u':
        case 'h':
        case 'j':
        case 'b':
        case 'n': return 3;
        case 'i':
        case 'o':
        case 'p':
        case 'k':
        case 'l':
        case 'm': return 4;
        }
        return 0;
    }

    private int getTriadLabel(char ch) {
        switch(ch) {
        case 'q':
        case 'a':
        case 'w': return 1;
        case 'z':
        case 's':
        case 'x': return 2;
        case 'e':
        case 'd':
        case 'r': return 3;
        case 'f':
        case 'c':
        case 'v': return 4;
        case 't':
        case 'g':
        case 'y': return 5;
        case 'h':
        case 'b':
        case 'n': return 6;
        case 'u':
        case 'j':
        case 'i': return 7;
        case 'o':
        case 'l':
        case 'p': return 8;
        case 'k':
        case 'm': return 9;
        }
        return 0;
    }

    private int getPairedLabel(char ch) {
        switch(ch) {
        case 'q':
        case 'w': return 1;
        case 'e':
        case 'r': return 2;
        case 't':
        case 'y': return 3;
        case 'u':
        case 'i': return 4;
        case 'o':
        case 'p': return 5;
        case 'a':
        case 's': return 6;
        case 'd':
        case 'f': return 7;
        case 'g':
        case 'b': return 8;
        case 'z':
        case 'x': return 9;
        case 'c':
        case 'v': return 10;
        case 'h':
        case 'j': return 11;
        case 'k':
        case 'l': return 12;
        case 'n':
        case 'm': return 13;
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

    /**
     * Writes the time vs g-force values for a key press
     * @param rows of signal for a keypress
     * @param filename
     * @throws IOException
     */
    public void writeGForceToFile(ArrayList<Signal> signals,
            String filename) throws IOException {
        
        PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(filename)));
        pw.println("timestamp, gforce");
        for(Iterator<Signal> iter = signals.iterator(); iter.hasNext();){
            Signal row = iter.next();
            pw.println(row.getTimeStamp().toString() + "," + row.getGForce());
        }
        pw.close();
    }

    /**
     * Write the features for a letter to a features list file at the
     * same level as the directory.
     * @param featuresList
     * @param datafile
     * @throws IOException
     */
    private void writeToFile(ArrayList<Features> featuresList,
            String datafile) throws IOException {
        
        PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(datafile)));
        pw.println(Features.getFeaturesName());
        for(Iterator<Features> iter = featuresList.iterator(); iter.hasNext();){
            pw.println(iter.next().toString());
        }
        pw.close();
        System.out.println("writing "+datafile);
    }

    public Features getFeatures(ArrayList<Signal> signals) {
        Features features = new Features();
        features.setMin(this.feature_min(signals));
        features.setMax(this.feature_max(signals));
        features.setMean(this.feature_mean(signals));
        features.setVariance(this.feature_variance(signals));
        features.setRms(this.feature_rms(signals));
        features.setSkewness(this.feature_skewness(signals));
        features.setKurtosis(this.feature_kurtosis(signals));
        return features;
    }

    /**
     * Reads the csv file and outputs the Signal computation for
     * each row of data
     * 
     * @param file csv file to read the data from 
     * @param datafile name of the output .data.csv file
     * @throws IOException
     * @return ArrayList of Signals from the file
     * @throws FileNotFoundException 
     */
    public ArrayList<Signal> readCSV(File file)
            throws FileNotFoundException {
        Scanner ob = new Scanner(file);
        BigInteger start_time = null;
        ArrayList<Signal> signals = new ArrayList<Signal>();
        
        while(ob.hasNextLine()) {
            StringTokenizer data = new StringTokenizer(ob.nextLine(), ",");
            BigInteger time = new BigInteger(data.nextToken());
            if (start_time == null) {
                start_time = time;
                time = BigInteger.ZERO;
            } else {
                // convert time passed to micro-second
                time = time.subtract(start_time).divide(FACTOR);
            }
            double x = Double.parseDouble(data.nextToken());
            double y = Double.parseDouble(data.nextToken());
            double z = Double.parseDouble(data.nextToken());
            signals.add(new Signal(time, x, y, z));
        }
        ob.close();
        return signals;
    }
    
    /**
     * Computes the minimum GForce recorded by the accelerometer for the
     * given key presses readings. 
     * @param signals
     * @return minimum g-force recorded
     */
    private double feature_min(ArrayList<Signal> signals) {
        double minGForce = Double.MAX_VALUE;
        for (Iterator<Signal> iter = signals.iterator(); iter.hasNext();) {
            Signal signal = iter.next();
            minGForce = Math.min(signal.getGForce(), minGForce);
        }
        return minGForce;
    }

    /**
     * Computes the maximum GForce recorded by the accelerometer for the
     * given key presses readings. 
     * @param signals
     * @return maximum g-force recorded
     */
    private double feature_max(ArrayList<Signal> signals) {
        double minGForce = Double.MIN_VALUE;
        for (Iterator<Signal> iter = signals.iterator(); iter.hasNext();) {
            Signal signal = iter.next();
            minGForce = Math.max(signal.getGForce(), minGForce);
        }
        return minGForce;
    }

    private double feature_mean(ArrayList<Signal> signals){
      double sum = 0;
      int size = signals.size();
      
      for(int i = 0; i < size; i++){
        sum = sum + (signals.get(i)).getGForce();
      }

      double mean = sum/size;
      return mean;
    }

    private double feature_variance(ArrayList<Signal> signals){
      double mean = feature_mean(signals);
      double sum = 0;
      int size = signals.size();

      for(int i = 0; i<size; i++){
        sum = sum + Math.pow(((signals.get(i)).getGForce() - mean), 2);
      }
      
      double variance = sum/size;
      return variance;
    }
    
    private double feature_rms(ArrayList<Signal> signals){
      double sum = 0;
      int size = signals.size();
      
      for(int i = 0; i<size; i++){
        sum = sum + Math.pow((signals.get(i)).getGForce(), 2);
      } 
      
      double square_mean = sum/size;
      double sqrt = Math.sqrt(square_mean);
      return sqrt;
    }

    /**
     * Skewness is a measure of the asymmetry of our signal.
     * A positive skew means that the right tail is longer
     * and a negative skew means that the left tail is longer.
     *
     * @param signals
     * @return
     */
    private double feature_skewness(ArrayList<Signal> signals) {
        double mean = feature_mean(signals);
        double variance = feature_variance(signals);
        double sum = 0;
        for (Signal s : signals) {
            sum += Math.pow(s.getGForce() - mean, 3); 
        }
        double skewness = sum 
                / (signals.size() - 1)
                / Math.pow(variance, 1.5);
        return skewness;
    }


    /**
     * Kurtosis is a measure of whether the data are peaked or flat relative
     * to a normal distribution. That is, data sets with high kurtosis tend to
     * have a distinct peak near the mean, decline rather rapidly, and have
     * heavy tails. Data sets with low kurtosis tend to have a flat top near
     * the mean rather than a sharp peak. A uniform distribution would be the
     * extreme case.
     * 
     * src: http://itl.nist.gov/div898/handbook/eda/section3/eda35b.htm
     * @param signals
     * @return
     */
    private double feature_kurtosis(ArrayList<Signal> signals){
        double mean = feature_mean(signals);
        double variance = feature_variance(signals);
        
        double quad_sum = 0;
        for (Iterator<Signal> iter = signals.iterator(); iter.hasNext();) {
            quad_sum = quad_sum + Math.pow(iter.next().getGForce() - mean, 4);
        }
        
        double kurtosis = quad_sum
                          / (signals.size() - 1)
                          / (variance * variance);
        return kurtosis;
      }

}
