import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class WordFeatureExtractor {

    public static final double G = 9.81;
    public static final BigInteger FACTOR = new BigInteger("1000");
    public static boolean WANT_GFORCE_DATA = false;
    public static BigInteger before_thresh = new BigInteger("250000"); // ms
    public static BigInteger after_thresh = new BigInteger("1000000"); // 1000ms

    private static ArrayList<Features> featuresList;

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
        featuresList = new ArrayList<Features>();
        ob.processWordCSV(directory);
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
        FeatureExtractor fe = new FeatureExtractor();
        for (int i = 0; i < letter_signals.size(); i++) {
            fe.writeGForceToFile(letter_signals.get(i),
                    path + "_" + i + ".gforce.csv");
        }
    }

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
        for (int i = 0; i < 6; i++) {
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

    private ArrayList<Signal> removeAroundMax(ArrayList<Signal> signals,
            int max_index){
        BigInteger peak_time_stamp = signals.get(max_index).getTimeStamp();
        
        // letter start index
        int start_cut_off_index = 0;
        for (int i = max_index; i >= 0; i--) {
            BigInteger diff = signals.get(i).getTimeStamp().subtract(
                    peak_time_stamp).abs();
            start_cut_off_index = i;
            if (diff.compareTo(before_thresh) > 0) {
                break;
            }
        }
        
        // letter end index
        int end_cut_off_index = 0;
        for (int i = max_index; i < signals.size(); i++) {
            BigInteger diff = signals.get(i).getTimeStamp().subtract(
                    peak_time_stamp).abs(); 
            end_cut_off_index = i;
            if (diff.compareTo(after_thresh) > 0) {
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
