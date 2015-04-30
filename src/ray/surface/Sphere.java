package ray.surface;

import ray.math.Point;
import ray.math.Ray;
import ray.math.SphereMath;
import ray.math.Vector;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author Wei Chen
 */
public class Sphere extends Surface {
    // These fields are read in from the input file.
    /** The center of the sphere. */
    protected Point center = new Point();
    /** The radius of the sphere. */
    protected double radius = 1.0;

    // PARSER METHODS
    public void setCenter(Point center) {
	this.center.set(center);
    }

    public void setRadius(double radius) {
	this.radius = radius;
    }

    /**
     * @see Surface#getIntersection()
     */
    @Override
    public double[] getIntersection(Ray ray) {
	// TODO: return t values at which this ray intersects this surface

	double dx = ray.getDirection().x;
	double dy = ray.getDirection().y;
	double dz = ray.getDirection().z;

	double a = SphereMath.getA(dx, dy, dz);
	double b = SphereMath.getB(dx, dy, dz, ray.getOrigin(), this.center);
	double c = SphereMath.getC(dx, dy, dz, ray.getOrigin(), this.center,
		radius);

	return SphereMath.getT(a, b, c);
    }

    /**
     * @see Surface#getNormal()
     */
    @Override
    public Vector getNormal(Point pt) {
	// TODO: return vector representing this surface's normal at this point
	return SphereMath.getNormal(pt, this.center, this.radius);
    }
}
