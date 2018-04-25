
package com.htc.lib1.cs.account.restobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link SearchResults} represents a list of search results from a query to
 * identity server.
 * 
 * @param <T>
 */
public class SearchResults<T> implements Serializable {
    private static final long serialVersionUID = -5926265115877274095L;

    /**
     * The list of objects retrieve from the server. The list of objects in the
     * <Result> may potentially be only a subset of the whole list in the
     * server. For example, Results can contain items from 1-20 and there are a
     * total of 100 (see <Count>) in the sever.
     */
    public List<T> Results;

    /**
     * The count for the objects. Count differs from Results.size() in that
     * Count describes the total number of objects that can be retrieved from
     * the server. See <Results>
     */
    public int Count;

    /**
     * Instantiates a JsonSearchResults object
     */
    public SearchResults() {
        this.Results = new ArrayList<T>();
    }

    /**
     * Returns the number of elements in Results. See <Results>
     * 
     * @return Result size.
     */
    public int size() {
        return Results.size();
    }

    /**
     * Returns the element at index (i) in Results. See <Results>
     * 
     * @param i
     * @return Element T.
     */
    public T get(int i) {
        return Results.get(i);
    }

    /**
     * Appends a new element to the end of Results. See <Results>
     * 
     * @param o
     */
    public void add(T o) {
        Results.add(o);
    }
}
