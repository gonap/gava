/*
 * Gava Library
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
 * @version 1.1.0
 */
public interface Storable<T, R> {
    /**
     * Get a record for the key.
     *
     * @param key to the record
     * @return record IF exists OTHERWISE returns null.
     */
    R get(T key);

    /**
     * Add a record with the key. IF the key already exists, overwrite the record.
     *
     * @param key to the record.
     * @param rec to save.
     * @return true if successfully added/updated.
     */
    boolean set(T key, R rec);

    /**
     * Delete a record
     *
     * @param key to the record
     * @return true if a record exists and deleted, otherwise false.
     */
    boolean delete(T key);
    
    /**
     * Close the storable
     * @return true if successsful, otherwise false.
     */    
    boolean close();
}
