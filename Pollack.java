package submit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import graph.FindState;
import graph.Finder;
import graph.FleeState;
import graph.Node;
import graph.NodeStatus;

/** A solution with find-the-Orb optimized and flee getting out as fast as possible. */
public class Pollack extends Finder {

    /** Get to the orb in as few steps as possible. <br>
     * Once you get there, you must return from the function in order to pick it up. <br>
     * If you continue to move after finding the orb rather than returning, it will not count.<br>
     * If you return from this function while not standing on top of the orb, it will count as <br>
     * a failure.
     *
     * There is no limit to how many steps you can take, but you will receive<br>
     * a score bonus multiplier for finding the orb in fewer steps.
     *
     * At every step, you know only your current tile's ID and the ID of all<br>
     * open neighbor tiles, as well as the distance to the orb at each of <br>
     * these tiles (ignoring walls and obstacles).
     *
     * In order to get information about the current state, use functions<br>
     * currentLoc(), neighbors(), and distanceToOrb() in FindState.<br>
     * You know you are standing on the orb when distanceToOrb() is 0.
     *
     * Use function moveTo(long id) in FindState to move to a neighboring<br>
     * tile by its ID. Doing this will change state to reflect your new position.
     *
     * A suggested first implementation that will always find the orb, but <br>
     * likely won't receive a large bonus multiplier, is a depth-first search. <br>
     * Some modification is necessary to make the search better, in general. */
    @Override
    public void find(FindState state) {
        // TODO 1: Walk to the orb
        HashMap<Long, Boolean> t= new HashMap<>();
        findin(state, t);

    }

    /** moves the node until the distance between it and the orb is zero then returns. */
    public void findin(FindState state, HashMap<Long, Boolean> t) {
        long u= state.currentLoc();
        t.put(u, false);

        if (state.distanceToOrb() == 0) { return; }
        for (NodeStatus w : state.neighbors()) {
            long id= w.getId();
            if (!t.containsKey(id)) {
                state.moveTo(id);
                t.put(id, false);
                findin(state, t);
                if (state.distanceToOrb() == 0) { return; }
                state.moveTo(u);
            }
        }

    }

    /** Get out the cavern before the ceiling collapses, trying to collect as <br>
     * much gold as possible along the way. Your solution must ALWAYS get out <br>
     * before steps runs out, and this should be prioritized above collecting gold.
     *
     * You now have access to the entire underlying graph, which can be accessed <br>
     * through FleeState state. <br>
     * currentNode() and exit() will return Node objects of interest, and <br>
     * allsNodes() will return a collection of all nodes on the graph.
     *
     * Note that the cavern will collapse in the number of steps given by <br>
     * stepsLeft(), and for each step this number is decremented by the <br>
     * weight of the edge taken. <br>
     * Use stepsLeft() to get the steps still remaining, and <br>
     * moveTo() to move to a destination node adjacent to your current node.
     *
     * You must return from this function while standing at the exit. <br>
     * Failing to do so before steps runs out or returning from the wrong <br>
     * location will be considered a failed run.
     *
     * You will always have enough steps to flee using the shortest path from the <br>
     * starting position to the exit, although this will not collect much gold. <br>
     * For this reason, using Dijkstra's to plot the shortest path to the exit <br>
     * is a good starting solution
     *
     * Here's another hint. Whatever you do you will need to traverse a given path. It makes sense
     * to write a method to do this, perhaps with this specification:
     *
     * // Traverse the nodes in moveOut sequentially, starting at the node<br>
     * // pertaining to state <br>
     * // public void moveAlong(FleeState state, List<Node> moveOut) */
    @Override
    public void flee(FleeState state) {
        // TODO 2. Get out of the c vern in time, picking up as much gold as possible.
        ArrayList<Node> visit= new ArrayList<>();
        ender(visit, state);

    }

    /** Moves to maximum number of nodes before the shortest path more than the amount of steps left
     * plus the maximum weight per step */
    public int golddigger(FleeState state, Node go, ArrayList<Node> visit) {
        Node arrived= state.currentNode();
        visit.add(state.currentNode());
        if (go == null &&
            Path.pathSum(Path.shortestPath(arrived, state.exit())) >= state.stepsLeft() - 15) {
            moveAlong(state, state.exit());
            return 1;
        }
        if (go != null && Path.pathSum(Path.shortestPath(go, state.exit())) +
            Path.pathSum(Path.shortestPath(arrived, go)) >= state.stepsLeft() - 15) {
            moveAlong(state, state.exit());
            return 1;
        }
        for (Node n : nextTo(arrived, state)) {
            if (Path.pathSum(Path.shortestPath(n, state.exit())) +
                Path.pathSum(Path.shortestPath(arrived, n)) >= state.stepsLeft() - 15) {
                moveAlong(state, state.exit());
                return 1;
            }
            if (!visit.contains(n)) {
                state.moveTo(n);
                if (golddigger(state, arrived, visit) == 1) return 1;
                state.moveTo(arrived);
            }
        }
        return 0;
    }

    /** Returns when you exit the cabin> */
    public boolean ender(ArrayList<Node> visit, FleeState state) {
        int i= golddigger(state, null, visit);
        if (i == 1) {
            return true;
        } else {
            moveAlong(state, state.exit());
            return true;
        }

    }

    /** Returns an ArrayList containing the current nodes neighbors<br>
     */
    public ArrayList<Node> nextTo(Node at, FleeState state) {
        ArrayList<Node> neighbors= new ArrayList<>();
        for (Node n : state.allNodes()) {
            if (Path.shortestPath(at, n).size() == 2) {
                neighbors.add(n);
            }
        }
        return neighbors;
    }

    /** Moves from the current states current node to final node */
    public void moveAlong(FleeState state, Node moveOut) {
        List<Node> route= new ArrayList<>();
        Node currentNode= state.currentNode();
        route= Path.shortestPath(currentNode, state.exit());
        for (int i= 1; i < route.size(); i++ ) {
            state.moveTo(route.get(i));
        }

    }

}