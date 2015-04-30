package ray.math;

/**
 * 
 * @author Wei,Wenjun
 *
 */

public class SphereMath {

    public static double getDist(double numA, double numB) {
	return numB - numA;
    }

    public static double getA(double dx, double dy, double dz) {
	return dx * dx + dy * dy + dz * dz;
    }

    public static double getB(double dx, double dy, double dz, Point start,
	    Point center) {
	return 2 * dx * (start.x - center.x) + 2 * dy * (start.y - center.y)
		+ 2 * dz * (start.z - center.z);
    }

    public static double getC(double dx, double dy, double dz, Point start,
	    Point center, double radius) {
	return center.x
		* center.x
		+ center.y
		* center.y
		+ center.z
		* center.z
		+ start.x
		* start.x
		+ start.y
		* start.y
		+ start.z
		* start.z
		+ -2
		* (center.x * start.x + center.y * start.y + center.z * start.z)
		- radius * radius;
    }

    private static double plusCal(double a, double b, double c) {
	return (-b + Math.sqrt(((b * b) - (4 * a * c)))) / (2 * a);
    }

    private static double minusCal(double a, double b, double c) {
	return (-b - Math.sqrt(((b * b) - (4 * a * c)))) / (2 * a);
    }

    public static double[] getT(double a, double b, double c) {

	if (((b * b) - (4 * a * c)) < 0)
	    return new double[0];

	double p = plusCal(a, b, c);
	double m = minusCal(a, b, c);

	if (p < 0 && m < 0)
	    return new double[0]; // if both neg

	if (p >= 0 && m < 0) { // if m is neg
	    double[] tVal = new double[1];
	    tVal[0] = p;
	    return tVal;
	}

	if (p < 0 && m >= 0) { // if p is neg
	    double[] tVal = new double[1];
	    tVal[0] = m;
	    return tVal;
	}

	if (p == m && p >= 0 && m >= 0) { // if p==m
	    double[] tVal = new double[1];
	    tVal[0] = p;
	    return tVal;
	}

	double[] tVal = new double[2]; // if both p and m are diff and positive
	tVal[0] = p;
	tVal[1] = m;
	return tVal;
    }

    public static Vector getNormal(Point p, Point center, double radius) {
	Vector normal = new Vector();

	double x = (p.x - center.x) / radius;
	double y = (p.y - center.y) / radius;
	double z = (p.z - center.z) / radius;

	normal.set(x, y, z);

	return normal;
    }

    public static Vector getReflection(Vector vL, Vector normal) {
	return normal.scale(2 * vL.dot(normal)).sub(vL);

    }

}
