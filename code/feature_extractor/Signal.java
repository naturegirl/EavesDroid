import java.math.BigInteger;


public class Signal {
    public static final double G = 9.81;

    private BigInteger timestamp;
    private double x;
    private double y;
    private double z;
    private double gforce;
    
    public Signal (BigInteger timestamp, double x, double y, double z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
        this.gforce = Math.sqrt(x*x + y*y + z*z) - G;
    }
    
    public BigInteger getTimeStamp() {
        return this.timestamp;
    }

    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public double getGForce() {
        return this.gforce;
    }
    
    public void setGForce(double gforce) {
        this.gforce = gforce;
    }

    @Override
    public String toString() {
        return this.getTimeStamp() + " " + this.getGForce();
    }
}
