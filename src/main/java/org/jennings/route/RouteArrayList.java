/**
 * Considering turning Routes object into an ArrayList
 * 
 * Currently not used
 * 
 * David Jennings
 * 
 */
package org.jennings.route;

import java.util.Arrays;

/**
 *
 * @author david
 */
public class RouteArrayList<Route> {

    private static final int INITIAL_CAPACITY = 10;
    private int size = 0;
    private Object rts[] = {};

    public RouteArrayList() {
        rts = new Object[INITIAL_CAPACITY];
    }

     public Route get(int index) {
           if (index < 0 || index >= size) {
                  throw new IndexOutOfBoundsException("Index: " + index + ", Size "
                               + index);
           }
           return (Route) rts[index]; 
    }    
    
    public void add(Route e) {
        if (size == rts.length) {
            ensureCapacity(); // increase current capacity of list, make it
            // double.
        }
        rts[size++] = e;
    }

     public Object remove(int index) {
        if (index < 0 || index >= size) {
                  throw new IndexOutOfBoundsException("Index: " + index + ", Size "
                               + index);
           }
 
           Object removedElement = rts[index];
           for (int i = index; i < size - 1; i++) {
                  rts[i] = rts[i + 1];
           }
           size--; 
 
           return removedElement;
    }    
    
    private void ensureCapacity() {
        int newIncreasedCapacity = rts.length * 2;
        rts = Arrays.copyOf(rts, newIncreasedCapacity);
    }

}
