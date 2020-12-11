package implemention;

/**
 * This class purpose is to help make the dijkstra table
 * @author shadihakim
 */
public class DWGraph_Node_helper {
    /**
     * This represent the shortest destination to the node
     */
    private double _dest;
    /**
     * This represent the node key that is before it
     */
    private Integer _ckey;

    /**
     * A simple constructor to build a row object in the table
     * @param _dest - The destination to the node
     * @param _ckey - The node key before it in the shortest path
     */
    public DWGraph_Node_helper(double _dest, Integer _ckey) {
        this._dest = _dest;
        this._ckey = _ckey;
    }

    /**
     * A simple get function
     * @return the destination to the node
     */
    public double get_dest() {
        return _dest;
    }

    /**
     * A simple set function
     * @param _dest - updated destination to the node
     */
    public void set_dest(double _dest) {
        this._dest = _dest;
    }

    /**
     * A simple get function
     * @return the node key before it in the shortest path
     */
    public Integer get_ckey() {
        return _ckey;
    }

    /**
     * A simple set function
     * @param _ckey - update the node key before it in the shortest path
     */
    public void set_ckey(Integer _ckey) {
        this._ckey = _ckey;
    }
}
