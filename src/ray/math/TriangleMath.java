package ray.math;

import java.util.HashMap;

public class TriangleMath {

    public static HashMap<String, Double> getMap(Point start, Vector direction,
	    Point vertA, Point vertB, Point vertC) {

	HashMap<String, Double> map = new HashMap<String, Double>();
	map.put("a", vertA.x - vertB.x);
	map.put("b", vertA.y - vertB.y);
	map.put("c", vertA.z - vertB.z);
	map.put("d", vertA.x - vertC.x);
	map.put("e", vertA.y - vertC.y);
	map.put("f", vertA.z - vertC.z);
	map.put("g", direction.x);
	map.put("h", direction.y);
	map.put("i", direction.z);
	map.put("j", vertA.x - start.x);
	map.put("k", vertA.y - start.y);
	map.put("l", vertA.z - start.z);

	return map;
    }

    public static double getM(HashMap<String, Double> map) {

	return map.get("a")
		* (map.get("e") * map.get("i") - map.get("h") * map.get("f"))
		+ map.get("b")
		* (map.get("g") * map.get("f") - map.get("d") * map.get("i"))
		+ map.get("c")
		* (map.get("d") * map.get("h") - map.get("e") * map.get("g"));
    }

    public static double getBeta(HashMap<String, Double> map, double m) {
	return (map.get("j")
		* (map.get("e") * map.get("i") - map.get("h") * map.get("f"))
		+ map.get("k")
		* (map.get("g") * map.get("f") - map.get("d") * map.get("i")) + map
		.get("l")
		* (map.get("d") * map.get("h") - map.get("e") * map.get("g")))
		/ m;
    }

    public static double getGamma(HashMap<String, Double> map, double m) {
	return (map.get("i")
		* (map.get("a") * map.get("k") - map.get("j") * map.get("b"))
		+ map.get("h")
		* (map.get("j") * map.get("c") - map.get("a") * map.get("l")) + map
		.get("g")
		* (map.get("b") * map.get("l") - map.get("k") * map.get("c")))
		/ m;
    }

    public static double getT(HashMap<String, Double> map, double m) {
	return (map.get("f")
		* (map.get("a") * map.get("k") - map.get("j") * map.get("b"))
		+ map.get("e")
		* (map.get("j") * map.get("c") - map.get("a") * map.get("l")) + map
		.get("d")
		* (map.get("b") * map.get("l") - map.get("k") * map.get("c")))
		/ m;
    }

    public static boolean checkInTriangle(double beta, double gamma) {
	if (beta > 0 && gamma > 0 && (beta + gamma) < 1)
	    return true;

	return false;
    }

}
