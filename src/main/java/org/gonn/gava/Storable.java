/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

/**
 * Minimal Database Interface for CRUD operation
 * A user can use key for Bucket/Database as well. (eg. "employees.Gon")
 * <code>
 * public class ABC implements Storable &lt;String, String&gt; { ... }
 * </code>
 *
 * @param <T> data type for Key
 * @param <R> data type for Record (value)
 * @author Gon Yi
 * @version 1.0.2
 */
public interface Storable<T, R> {
    /**
     * Get a record for the key.
     *
     * @param key to the record
     * @param <E> any exception
     * @return record IF exists OTHERWISE returns null.
     * @throws E any exception
     */
    <E extends Throwable> R get(T key) throws E;

    /**
     * Add a record with the key. IF the key already exists, overwrite the record.
     *
     * @param key to the record.
     * @param <E> any exception
     * @param rec to save.
     * @throws E any exception
     */
    <E extends Throwable> void set(T key, R rec) throws E;

    /**
     * Delete a record
     *
     * @param key to the record
     * @param <E> any exception
     * @return true if a record exists and deleted, otherwise false.
     * @throws E any exception
     */
    <E extends Throwable> boolean delete(T key) throws E;
    
    /**
     * Close the storable
     *
     * @param <E> any exception
     * @throws E any exception
     */    
    <E extends Throwable> void close() throws E;
}
