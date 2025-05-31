import java.lang.management.MemoryManagerMXBean;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * PSBruteForce is a Point collection that provides brute force
 * nearest neighbor searching using red-black tree.
 */
public class PSBruteForce<Value> implements PointSearch<Value> {
    private RedBlackBST<Point, Value> bst;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    // constructor makes empty collection
    public PSBruteForce() {
        bst = new RedBlackBST<>();
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
    }

    // add the given Point to KDTree
    public void put(Point p, Value v) {
        if(p == null) return;
        bst.put(p, v);
        // update the min or max
        double x = p.x();
        double y = p.y();

        if(x < minX) minX = x;
        if(y < minY) minY = y;
        if(x > maxX) maxX = x;
        if(y > maxY) maxY = y;
    }

    public Value get(Point p) {
        return (Value) bst.get(p);
    }

    public boolean contains(Point p) {
        return bst.contains(p);
    }

    // return an iterable of all points in collection
    public Iterable<Point> points() {
        return bst.keys();
    }

    // return the Point that is closest to the given Point
    public Point nearest(Point p) {
        if(isEmpty()) return null;
        //go through the iterator and find the nearest
        Point nearestPoint = null;
        double nearestDis = Double.POSITIVE_INFINITY;

        for(Point current : points()){
            // gets the distance from the mouse to each point in the map
            double distance = p.distSquared(current);
            if(distance < nearestDis){
                nearestDis = distance;
                nearestPoint = current;
            }
        }
        return nearestPoint;
    }

    // return the Value associated to the Point that is closest to the given Point
    public Value getNearest(Point p) {
        Point nearestPoint = nearest(p);
        if(nearestPoint == null) return null;
        else return get(nearestPoint);
    }

    // return the min and max for all Points in collection.
    // The min-max pair will form a bounding box for all Points.
    // if KDTree is empty, return null.
    public Point min() {
        if(isEmpty()) return null;
        return new Point(minX, minY);
    }
    public Point max() {
        if(isEmpty()) return null;
        return new Point(maxX, maxY);
    }

    // return the k nearest Points to the given Point
    public Iterable<Point> nearest(Point p, int k) {
        if(isEmpty() || k <= 0) return new ArrayList<>();
        // create a max prio queue to hold the values of the minimum things
        // using a max prio queue means if we find something smaller we can get rid of the biggest path

        MaxPQ<Point> maxPQ = new MaxPQ<>((a, b) ->
                Double.compare(a.distSquared(p), b.distSquared(p))  // reverse order for max-heap behavior
        );

        for(Point current : points()) {
            if(maxPQ.size() < k){
                maxPQ.insert(current);
            } else if(current.distSquared(p) < maxPQ.max().distSquared(p)){
                maxPQ.delMax();
                maxPQ.insert(current);
            }
        }

        ArrayList<Point> nearestPoints = new ArrayList<>();
        while(!maxPQ.isEmpty()){
            nearestPoints.add(maxPQ.delMax());
        }
        return nearestPoints;
    }

    public Iterable<Partition> partitions() { return null; }

    // return the number of Points in KDTree
    public int size() { return bst.size(); }

    // return whether the KDTree is empty
    public boolean isEmpty() { return bst.isEmpty(); }

    // place your timing code or unit testing here
    public static void main(String[] args) {
        // load point data into flat array
        In in = new In("input100k.txt");
        double[] d = in.readAllDoubles();

        // insert points into PointSearch
        PointSearch<Integer> ps = new PSBruteForce<>();
        for(int i = 0; i < d.length; i+=2) {
            ps.put(new Point(d[i], d[i+1]), i);
        }

        Stopwatch stopwatch = new Stopwatch();
        // call nearest 1million (pinky to corner of mouth) times!
        for(int i = 0; i < 100000; i++) {
            System.out.println("working");
            Point q = Point.gaussian();
            ps.nearest(q);
        }
        double time = stopwatch.elapsedTime();
        System.out.println("Time: " + time);
    }
}
