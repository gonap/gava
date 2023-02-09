package org.gonn.gava;

import java.util.function.UnaryOperator;

/**
 * Minimal Database Interface for CRUD operation
 * A user can use key for Bucket/Database as well. (eg. "employees.Gon")
 * <code>
 * public class ABC implements Storable &lt;String, String&gt; { ... }
 * </code>
 *
 * @param <T> data type for Key
 * @param <U> data type for Record (value)
 * @author Gon Yi
 * @version 1.0.1
 * @link https://gonn.org
 */
public interface Storable<T, U> {
    /**
     * Get a record for the key.
     *
     * @param key to the record
     * @return record IF exists OTHERWISE returns null.
     */
    <E extends Exception> U get(T key) throws E;

    /**
     * Add a record with the key. IF the key already exists, overwrite the record.
     *
     * @param key to the record.
     * @param rec to save.
     */
    <E extends Exception> void set(T key, U rec) throws E;

    /**
     * Delete a record
     *
     * @param key to the record
     * @return true if a record exists and deleted, otherwise false.
     */
    <E extends Exception> boolean delete(T key) throws E;

    // ====================================================================================================
    // OPTIONAL METHODS
    // ====================================================================================================

    /**
     * Update a record
     *
     * @param key     to the record
     * @param fUpdate function to update the record. e.g. f(currentRecord) returns newRecord.
     * @return true IF a record exists, OTHERWISE, false.
     */
    default <E extends Exception> boolean update(T key, UnaryOperator<U> fUpdate) throws E {
        U orig = this.get(key);
        if (orig == null) return false;
        set(key, fUpdate.apply(orig));
        return true;
    }

    /**
     * Close database connection.
     */
    default <E extends Exception> void close() throws E {
    }

}
