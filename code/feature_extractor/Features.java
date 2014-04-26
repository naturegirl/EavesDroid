
public class Features {
    private double min;
    private double max;
    private double mean;
    private double variance;
    private double rms;
    private double skewness;
    private double kurtosis;
    private int label;

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getRms() {
        return rms;
    }

    public void setRms(double rms) {
        this.rms = rms;
    }
    
    @Override
    public String toString() {
        return  this.mean + "," +
                this.skewness + "," +
                this.kurtosis + "," +
                this.variance + "," +
                this.min + "," +
                this.max + "," +
                this.rms + "," +
                label;
    }
    
    public static String getFeaturesName() {
        return "mean, skewness, kurtosis, variance, min, max, rms, label";
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public double getSkewness() {
        return skewness;
    }

    public void setSkewness(double skewness) {
        this.skewness = skewness;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public void setKurtosis(double kurtosis) {
        this.kurtosis = kurtosis;
    }
}
