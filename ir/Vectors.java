package ir;

public class Vectors {
	//will be 0 if vectors are right angles to each other (orthogonal)
    public static double dotProduct(double[] v1, double[] v2) {
        double product = 0;
        int size = v1.length;
        for(int i=0; i < size; i++) {
            product += v1[i] * v2[i];
        }
        return product;
    }
    
    //theta should be in degrees
    public static double dotProduct2(double[] a, double[] b, double theta) {
       return magnitude(a) * magnitude(b) * Math.cos(Math.toRadians(theta));        
    }
    
    //result is another vector that is at right angle to both input vectors
    //must be 3D vectors
    public static double[] crossProduct(double[] a, double[] b) {
        double[] cross = new double[3];
        if(a.length == 3 && b.length == 3) {
            cross[0] = a[1]*b[2] - a[2]*b[1];
            cross[1] = a[2]*b[0] - a[0]*b[2];
            cross[2] = a[0]*b[1] - a[1]*b[0];
        }
        return cross;
    }
    
    //result is another vector that is at right angle to both input vectors
    //must be 3D
    public static double[] crossProduct2(double[] a, double[] b) {
        int size = a.length;        
        double magA = magnitude(a), magB = magnitude(b);
        double theta = angleBetween2Vectors(a, b);
        double temp = magA * magB * Math.sin(Math.toRadians(theta));
        double[] unitVector = normalize(crossProduct2(a, b)); //new double[size];        
        double[] cross = new double[size];
        for(int i=0; i < size; i++)
            cross[i] = unitVector[i] * temp;
        return cross;
    }
    
    //returns the angle between 2 vectors (cosine similarity)
    // cos(theta) = (a * b) / |a| * |b| ==> theta = cos
    public static double angleBetween2Vectors(double[] a, double[] b) {
        double dot = dotProduct(a, b);
        double mags = magnitude(a) * magnitude(b);
        return Math.toDegrees(Math.acos(dot/mags));
    }
    
    public static double cosineSimilarity(double[] a, double[] b) {
        a = normalize(a); 
        b = normalize(b);
        double dot = dotProduct(a, b);
        return Math.toDegrees(Math.acos(dot));
    }
    
    //similarity without using the cosine. in the range [0, 1]
    public static double similarity(double[] v1, double[] v2) {
    	return dotProduct(normalize(v1), normalize(v2));
    }
    
    //in 2D
    public static double[] polarToCartesian(double magnitude, double theta) {
        double[] v = {0, 0};
        v[0] = magnitude * Math.cos(Math.toRadians(theta)); //x
        v[1] = magnitude * Math.sin(Math.toRadians(theta)); //y
        return v;
    }
    
    //in 2D
    public static double[] cartesianToPolar(double x, double y) {
        double[] r = {x, y};
        r[0] = magnitude(r); //magnitude
        double theta = Math.toDegrees(Math.atan(y/x));
        if((x < 0 && y > 0) || (x < 0 && y < 0)) theta += 180; //quadrant 2 or 3
        else if(x > 0 && y < 0) theta += 360; //quadrant 4
        r[1] = theta;
        return r;
    }
    
    //same as length of a vector |v|
    public static double magnitude(double[] v) {
        double mag = 0;
        int size = v.length;
        for(int i=0; i < size; i++) {
            mag += v[i] * v[i];
        }
        return Math.sqrt(mag);
    }
    
    public static double[] normalize(double[] v) {
        double mag = magnitude(v);
        int size = v.length;
        double[] norm = new double[size];
        for(int i=0; i < size; i++)
            norm[i] = v[i] / mag;
        return norm;
    }
    
    public static String printVector(double[] v) {    	
    	StringBuffer sb = new StringBuffer("[");
    	int size = v.length;
    	for(int i=0; i < size; i++)
    		sb.append(v[i]).append(", ");
    	
    	String str = sb.toString();
    	str = str.substring(0, str.length()-2) + "]";
    	return str;
    }
}