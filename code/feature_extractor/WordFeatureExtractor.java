import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;


public class WordFeatureExtractor {

    public static final double G = 9.81;
    public static final BigInteger FACTOR = new BigInteger("1000");
    public static boolean WANT_GFORCE_DATA = false;
    public static BigInteger BEFORE_THRESH = new BigInteger("250000"); // 250ms
    public static BigInteger AFTER_THRESH = new BigInteger("1000000"); // 1000ms
    public static double G_FORCE_THRESH = 0.25;
    // # indices into the signal
    private static final int BASE_REFERENCE_CUT_OFF = 10;
    
    private static HashMap<String, ArrayList<Features>> featuresMap;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java WordFeatureExtractor " +
                    "<data-folder> <<gforce>>");
            System.out.println("Example: java Word FeatureExtractor akshay");
            System.exit(0);
        }
        if (args.length == 2) {
            if (args[1].equals("gforce")) {
                WANT_GFORCE_DATA = true;
            }
        }
        String path = "../../data/" + args[0];
        File directory = new File(path);

        WordFeatureExtractor ob = new WordFeatureExtractor();
        featuresMap = new HashMap<String, ArrayList<Features>>();
        ob.processWordCSV(directory);
        
        String features_dir = "../../data/" + args[0] + ".feature";
        ob.writeToFile(features_dir);
    }

    private void writeToFile(String features_dir) throws IOException {
        for (String name : featuresMap.keySet()) {
            String filepath = features_dir + "/" + name + ".features.csv";
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(new FileWriter(filepath)));
            String heading = Features.getFeaturesName();
            // remove the ", label" from the features heading
            heading = heading.substring(0, heading.lastIndexOf(','));
            pw.println(Features.getFeaturesName());
            
            for (Features feature : featuresMap.get(name)) {
                String fstr = feature.toString();
                fstr = fstr.substring(0, fstr.lastIndexOf(','));
                pw.println(fstr);
            }
            pw.close();
            System.out.println("writing " + filepath);
        }
    }

    /**
     * Reads the csv file and breaks the word signal into multiple
     * files for each letter
     * 
     * @param file csv file to read the data from 
     * @param datafile name of the output .data.csv file
     * @throws IOException
     * @return ArrayList of Signals from the file
     */
    private ArrayList<Signal> processWordCSV(File directory)
            throws IOException {
        File[] files = directory.listFiles();
        String dirName = directory.getName();

        FeatureExtractor fe = new FeatureExtractor();
        for (File file: files) {
            String filepath = file.getAbsolutePath();
            if (filepath.endsWith(".csv")) {
                ArrayList<Signal> signals = fe.readCSV(file);
                ArrayList<ArrayList<Signal>> letter_signals =
                        this.breakSignal(signals);
                this.orderLetterSignals(letter_signals);
                System.out.println("# letters = " + letter_signals.size());
                this.writeWordLettersGForce(
                        letter_signals,
                        filepath.substring(0, filepath.length()-4)
                        );
            } else if (file.isDirectory()) {
                this.processWordCSV(file);
            }
        }
        return null;
    }

    private void writeWordLettersGForce(
            ArrayList<ArrayList<Signal>> letter_signals, String path)
                    throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        String filepath = file.getAbsolutePath() + "/letter_";
        FeatureExtractor fe = new FeatureExtractor();
        for (int i = 0; i < letter_signals.size(); i++) {
            fe.writeGForceToFile(letter_signals.get(i),
                    filepath + i + ".gforce.csv");
        }
    }

    /**
     * Orders the letter signals according as they appear in the word typed
     * @param letter_signals
     */
    private void orderLetterSignals(
            ArrayList<ArrayList<Signal>> letter_signals) {
        Collections.sort(letter_signals, new Comparator<ArrayList<Signal>>(){
            public int compare(ArrayList<Signal> letter1,
                    ArrayList<Signal> letter2) {
                BigInteger diff = letter1.get(0).getTimeStamp().subtract(
                        letter2.get(0).getTimeStamp());
                return diff.compareTo(BigInteger.ZERO);
            }
        });
    }

    private ArrayList<ArrayList<Signal>> breakSignal(ArrayList<Signal> signals){
        @SuppressWarnings("unchecked")
        ArrayList<Signal> sorted_signals = (ArrayList<Signal>) signals.clone();
        // reverse sorted according to the absolute values of the g-force values
        Collections.sort(sorted_signals, new Comparator<Signal>() {
            public int compare(Signal signal1, Signal signal2) {
                double diff = Math.abs(signal1.getGForce()) -
                        Math.abs(signal2.getGForce());
                return -(int)Math.signum(diff);
            }
        });
        ArrayList<ArrayList<Signal>> letter_signals =
                new ArrayList<ArrayList<Signal>>();
        double base_ref = this.findBaseReference(signals);
        while (sorted_signals.size() > 0) {
            if (Math.abs(sorted_signals.get(0).getGForce() - base_ref)
                < G_FORCE_THRESH) {
                break;
            }
            int max_index = signals.indexOf(sorted_signals.get(0));
            System.out.println("max_index = " + max_index);
            ArrayList<Signal> letter =
                    this.removeAroundMax(signals, max_index);
            letter_signals.add(letter);
            System.out.println("sorted_signals.size() before: = " + sorted_signals.size());
            sorted_signals.removeAll(letter);
        }
        return letter_signals;
    }

    private double findBaseReference(ArrayList<Signal> signals) {
        double sum = 0;
        for (int i = 0; i < BASE_REFERENCE_CUT_OFF; i++) {
            sum += signals.get(i).getGForce();
        }
        return sum/BASE_REFERENCE_CUT_OFF;
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

        System.out.println("letter_signal.size() = " + letter_signal.size());
        return letter_signal;
    }
}
