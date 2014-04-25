import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
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

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java GForceComputation " +
                    "<directory to traverse>");
            System.exit(0);
        }
        String path = "./" + args[0];
        File f = new File(path);
        File[] files = f.listFiles();
        FeatureExtractor ob = new FeatureExtractor();
        ArrayList<Features> featuresList = new ArrayList<Features>();
        String featuresFile = "./" + args[0] + ".data.csv";

        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".csv")) {
                ArrayList<Signal> signals = ob.processCSV(file);
                Features features = ob.getFeatures(signals);
                featuresList.add(features);
            }
        }
        ob.writeToFile(featuresList, featuresFile);
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
        for(Iterator<Features> iter = featuresList.iterator(); iter.hasNext();){
            pw.println(iter.next().toString());
        }
        pw.close();
    }

    private Features getFeatures(ArrayList<Signal> signals) {
        Features features = new Features();
        features.setMin(this.feature_min(signals));
        features.setMax(this.feature_max(signals));
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
    private ArrayList<Signal> processCSV(File file)
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
        sum = sum + (signals.get(i)).getGforce();
      }

      double mean = sum/size;
      return mean;
    }

    private double feature_variance(ArrayList<Signal> signals){
      double mean = feature_mean(signals);
      double sum = 0;
      int size = signals.size();

      for(int i = 0; i<size; i++){
        sum = sum + Math.pow(((signals.get(i)).getGforce() - mean), 2);
      }
      
      double variance = sum/size;
      return variance;
    }
    
    private double feature_mean(ArrayList<Signal> signals){
      double sum = 0;
      int size = signals.size();
      
      for(int i = 0; i<size; i++){
        sum = sum + Math.pow((signals.get(i)).getGforce(), 2);
      } 
      
      double square_mean = sum/size;
      double sqrt = Math.sqrt(square_mean);
      return sqrt;
    }

}
