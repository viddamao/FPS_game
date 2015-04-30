package ray.surface;

import java.util.HashMap;

import ray.math.Point;
import ray.math.Ray;
import ray.math.TriangleMath;
import ray.math.Vector;

public class Triangle extends Surface {
    // These fields are read in from the input file.
    /* These points are the vertices of the triangle */
    protected final Point a = new Point();
    protected final Point b = new Point();
    protected final Point c = new Point();

    // PARSER METHODS
    public void setA(Point pt) {
	a.set(pt);
    }

    public void setB(Point pt) {
	b.set(pt);
    }

    public void setC(Point pt) {
	c.set(pt);
    }

    /**
     * @see Surface#getIntersection()
     */
    @Override
    public double[] getIntersection(Ray ray) {
	// TODO: return t values at which this ray intersects this surface

	HashMap<String, Double> map = TriangleMath.getMap(ray.getOrigin(),
		ray.getDirection(), this.a, this.b, this.c);
	double m = TriangleMath.getM(map);

	if (m == 0) {
	    return new double[0];
	} else {
	    double beta = TriangleMath.getBeta(map, m);
	    double gamma = TriangleMath.getGamma(map, m);
	    double t = TriangleMath.getT(map, m);

	    if (TriangleMath.checkInTriangle(beta, gamma)) {
		double[] tvals = new double[1];
		tvals[0] = t;
		return tvals;

	    } else {
		return new double[0];
	    }

	}

    }

    /**
     * @see Surface#getNormal()
     */
    @Override
    public Vector getNormal(Point point) {
	// TODO: return vector representing this surface's normal at this point
	Vector a = new Vector(this.a);
	Vector b = new Vector(this.b);
	Vector c = new Vector(this.c);

	return (b.sub(a)).cross((b.sub(c)));
    }
}
