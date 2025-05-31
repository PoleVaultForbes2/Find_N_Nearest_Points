import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * PSKDTree is a Point collection that provides nearest neighbor searching using
 * 2d tree
 */
public class PSKDTree<Value> implements PointSearch<Value> {
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    private class Node {
        Point p;
        Value v;
        Node left, right;
        Partition.Direction dir;

        Node(Point p, Value v, Partition.Direction dir){
            this.p = p;
            this.v = v;
            this.dir = dir;
        }
    }

    private Node root;
    private int size;

    // constructor makes empty kD-tree
    public PSKDTree() {
        root = null;
        size = 0;
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
    }

    // switches the direction when going to the next level in the tree
    private static Partition.Direction getDirection(Partition.Direction dir){
        return dir == Partition.Direction.LEFTRIGHT ? Partition.Direction.DOWNUP : Partition.Direction.LEFTRIGHT;
    }

    // add the given Point to kD-tree
    public void put(Point p, Value v) {
        if(isEmpty()){
            minX = maxX = p.x();
            minY = maxY = p.y();
        } else{
            // update the min or max
            double x = p.x();
            double y = p.y();

            if(x < minX) minX = x;
            if(y < minY) minY = y;
            if(x > maxX) maxX = x;
            if(y > maxY) maxY = y;
        }

        // call the recursive put function to insert the node
        root = put(root, p, v, Partition.Direction.LEFTRIGHT);
    }

    private Node put(Node node, Point p, Value v, Partition.Direction dir){
        // if this is the first thing make it root
        if(node == null){
            size++;
            return new Node(p, v, dir);
        }

        // based on the direction compare the x or y value of the nodes
        int cmp;
        cmp = Double.compare(p.xy(dir),node.p.xy(dir));

        if (cmp <= 0){
            node.left = put(node.left, p, v, getDirection(dir));
        } else{
            node.right = put(node.right, p, v, getDirection(dir));
        }
        return node;
    }

    public Value get(Point p) {
        return get(root, p, Partition.Direction.LEFTRIGHT);
    }

    private Value get(Node node, Point p, Partition.Direction dir){
        if(node == null) return null;
        if(node.p.equals(p)) return node.v;

        int cmp;
        // compare either the x value or y value depending on direction
        cmp = Double.compare(p.xy(dir), node.p.xy(dir));

        // recursively check given a point until that point is found and return the value
        if(cmp < 0){
            return get(node.left, p, getDirection(dir));
        } else{
            return get(node.right, p, getDirection(dir));
        }
    }

    public boolean contains(Point p) {
        return get(p) != null;
    }

    public Value getNearest(Point p) {
        Point nearestPoint = nearest(p);
        if(nearestPoint == null) return null;
        else return get(nearestPoint);
    }

    // return an iterable of all points in collection
    public Iterable<Point> points() {
        ArrayList<Point> pointList = new ArrayList<>();
        collectPoints(root, pointList);
        return pointList;
    }

    private void collectPoints(Node node, ArrayList<Point> pointsList){
        if(node == null) return;
        // add all the points to the left and right
        pointsList.add(node.p); // the current node
        collectPoints(node.left, pointsList);
        collectPoints(node.right, pointsList);
    }

    // return an iterable of all partitions that make up the kD-tree
    public Iterable<Partition> partitions() {
        ArrayList<Partition> partitionsList = new ArrayList<>();
        collectPartitions(root, partitionsList, minX, maxX, minY, maxY);
        return partitionsList;
    }

    private void collectPartitions(Node node, ArrayList<Partition> partitionsList,
                                   double minX, double maxX, double minY,double maxY){
        if(node == null) return;

        if(node.dir == Partition.Direction.LEFTRIGHT){
            partitionsList.add(new Partition(node.p.x(), minY, node.p.x(), maxY, node.dir));
            collectPartitions(node.left, partitionsList, minX, node.p.x(), minY,maxY);
            collectPartitions(node.right, partitionsList, node.p.x(), maxX, minY, maxY);
        } else{
            partitionsList.add(new Partition(minX, node.p.y(), maxX, node.p.y(), node.dir));
            collectPartitions(node.left, partitionsList, minX, maxX, minY, node.p.y());
            collectPartitions(node.right, partitionsList, minX, maxX, node.p.y(), maxY);
        }
    }

    // return the Point that is closest to the given Point
    public Point nearest(Point p) {
        if(isEmpty()) return null;
        return nearest(root, p, null, Double.POSITIVE_INFINITY, Partition.Direction.LEFTRIGHT);
    }

    private Point nearest(Node node, Point current, Point best, double bestDist, Partition.Direction dir){
        if(node == null) return best;

        // get the distance between mouse and the node we are searching (root in first case)
        double dist = current.distSquared(node.p);
        if(dist < bestDist){
            best = node.p;
            bestDist = dist;    // update the best if the path is shorter
        }

        // calculate which side to search down and make second the other option
        Node first = (current.xy(dir) < node.p.xy(dir)) ? node.left : node.right;
        Node second = (first == node.left) ? node.right : node.left;

        best = nearest(first, current, best, bestDist, getDirection(dir));

        bestDist = current.distSquared(best);

        // check if we should go down the other side
        double secDist = Math.abs(current.xy(dir) - node.p.xy(dir));
        if(secDist * secDist <= bestDist){
            Point otherSide = nearest(second, current, best, bestDist, getDirection(dir));
            double otherSideDist = current.distSquared(otherSide);
            if(otherSideDist <= bestDist){
                best = otherSide;
                bestDist = otherSideDist;
            }
        }

        return best;
    }

    // return the k nearest Points to the given Point
    public Iterable<Point> nearest(Point p, int k) {
        if(isEmpty() || k <= 0) return new ArrayList<>();

        MaxPQ<Point> maxPQ = new MaxPQ<>((a, b) ->
                Double.compare(a.distSquared(p), b.distSquared(p))  // reverse order for max-heap behavior
        );

        findNearest(root, p, k, maxPQ, Partition.Direction.LEFTRIGHT);

        ArrayList<Point> kNearest = new ArrayList<>();
        while(!maxPQ.isEmpty()){
            kNearest.add(maxPQ.delMax());
        }
        return kNearest;
    }

    private void findNearest(Node node, Point currentPoint, int k, MaxPQ<Point> maxPQ, Partition.Direction dir){
        if(node == null) return;

        double dist = currentPoint.distSquared(node.p);
        double maxDist = Double.POSITIVE_INFINITY;
        if(!maxPQ.isEmpty()) maxDist = currentPoint.distSquared(maxPQ.max());

        // add to the maxPQ if the queue doesn't have k items or is less than the max
        if(maxPQ.size() < k){
            maxPQ.insert(node.p);
            // if the point we are searching is a less distance than the max, remove max and add
        } else if(dist < maxDist){
            maxPQ.delMax();
            maxPQ.insert(node.p);
        }

        // recursively go through both subtrees
        Node first = (currentPoint.xy(dir) < node.p.xy(dir)) ? node.left : node.right;
        Node second = (first == node.left) ? node.right : node.left;

        findNearest(first, currentPoint, k, maxPQ, getDirection(dir));

        // check if we should go down second
        double splitDist = Math.abs(currentPoint.xy(dir) - node.p.xy(dir));
        if(maxPQ.size() < k || (splitDist * splitDist) < maxDist){
            findNearest(second, currentPoint, k, maxPQ, getDirection(dir));
        }
    }

    // return the min and max for all Points in collection.
    // The min-max pair will form a bounding box for all Points.
    // if kD-tree is empty, return null.
    public Point min() {
        if(isEmpty()) return null;
        return new Point(minX, minY);
    }
    public Point max() {
        if(isEmpty()) return null;
        return new Point(maxX, maxY);
    }

    // return the number of Points in kD-tree
    public int size() { return size;}  // return whether the kD-tree is empty
    public boolean isEmpty() { return size == 0;}

    // place your timing code or unit testing here
    public static void main(String[] args) {
//        PSKDTree<String> tree = new PSKDTree<>();
//
//        tree.put(new Point(2,3), "A");
//        tree.put(new Point(5, 4), "B");
//        tree.put(new Point(9, 6), "C");
//        tree.put(new Point(4, 7), "D");
//        tree.put(new Point(8, 1), "E");
//        tree.put(new Point(7, 2), "F");
//
//        Point query = new Point(8, 7);
//        Point nearest = tree.nearest(query);
//        System.out.println("Nearest point to " + query + ": " + nearest);

        // load point data into flat array
        In in = new In("input100k.txt");
        double[] d = in.readAllDoubles();

        // insert points into PointSearch
        PointSearch<Integer> ps = new PSKDTree<>();
        for(int i = 0; i < d.length; i+=2) {
            ps.put(new Point(d[i], d[i+1]), i);
        }

        Stopwatch stopwatch = new Stopwatch();
        // call nearest 1million (pinky to corner of mouth) times!
        for(int i = 0; i < 100000; i++) {
            Point q = Point.gaussian();
            ps.nearest(q);
        }
        double time = stopwatch.elapsedTime();
        System.out.println("Time: " + time);
    }

}
