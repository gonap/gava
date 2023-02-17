/*
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;


/**
 * Box an object just like the java.util.Optional
 *
 * @param <T> Type of the content to be boxed
 * @author Gon Yi
 * @version 0.0.5
 */
public class Box<T> {
    private T value;

    /**
     * Box object with T in it.
     *
     * @param t Boxed T.
     */
    public Box(T t) {
        this.value = t;
    }

    /**
     * Static constructor:
     *
     * @param obj A target object to be boxed.
     * @param <T> Type of the target object.
     * @return Boxed target object.
     */
    public static <T> Box<T> of(T obj) {
        return new Box<>(obj);
    }

    /**
     * Static constructor:
     *
     * @param fx  A lambda to create a target object to be boxed.
     * @param <R> Type of the target object.
     * @return Boxed target object.
     */
    public static <R> Box<R> of(Fx01<R> fx) {
        return new Box<>(fx.run());
    }

    /**
     * Check if the box content is NULL.
     *
     * @return true if the content is NULL.
     */
    public boolean isEmpty() {
        return this.value == null;
    }

    /**
     * Check if the box content is NOT NULL.
     *
     * @return true if the content is full.
     */
    public boolean isFull() {
        return this.value != null;
    }

    /**
     * Get the box content.
     * When the box is empty, this will return null.
     *
     * @return Content of the box.
     */
    public T get() {
        return this.value;
    }

    /**
     * Get the box content.
     * If it is null, return the fallback instead.
     *
     * @param fallback A value to be returned when the box is empty.
     * @return Content of the box.
     */
    public T get(T fallback) {
        return this.value == null ? fallback : value;
    }

    /**
     * If the condition function returns true, empty the box.
     *
     * @param skipFn A lambda function returning true means empty the box.
     * @return Self
     */
    public Box<T> skip(FxBool<T> skipFn) {
        if (this.value != null && skipFn.run(this.value)) this.value = null;
        return this;
    }

    /**
     * View or modifying the box content.
     * This is for a mutable content. Use thenSet() for immutable content.
     *
     * @param modFn A function that can view or modify the content.
     * @return self
     */
    public Box<T> then(Fx10<T> modFn) {
        if (this.value != null)
            modFn.run(this.value);
        return this;
    }

    /**
     * Replace the box content.
     * This is to replace the immutable content in a box.
     * Content of the box will be replaced with the output of modFn.
     * NOTE: This is between same content type. Use "map" method in order to change to a different type.
     *
     * @param modFn A function that takes and returns type T data.
     * @return self
     */
    public Box<T> thenSet(FxUnary<T> modFn) {
        if (this.value != null)
            this.value = modFn.run(this.value);
        return this;
    }

    /**
     * If the box is full, and the condition has met, then execute the function.
     *
     * @param condition Condition to evaluate weather to run
     * @param modFn     If the condition has met, execute.
     * @return Self.
     */
    public Box<T> thenIf(FxBool<T> condition, Fx10<T> modFn) {
        if (this.value != null && condition.run(this.value))
            modFn.run(this.value);
        return this;
    }

    /**
     * Execute fnTrue if the condition has met, otherwise, execute fnFalse.
     *
     * @param condition Condition to evaluate
     * @param fnTrue    A lambda to run if the condition has met
     * @param fnFalse   A lambda to run if the condition has NOT met.
     * @return New value
     */
    public Box<T> thenIf(FxBool<T> condition, Fx10<T> fnTrue, Fx10<T> fnFalse) {
        if (this.value == null) return this;
        if (condition.run(this.value)) fnTrue.run(this.value);
        else fnFalse.run(this.value);
        return this;
    }

    /**
     * If the box is full, and the condition has met, then execute the function.
     *
     * @param condition Condition to evaluate weather to run
     * @param modFn     If the condition has met, execute.
     * @return Self.
     */
    public Box<T> thenSetIf(FxBool<T> condition, FxUnary<T> modFn) {
        if (this.value != null && condition.run(this.value))
            this.value = modFn.run(this.value);
        return this;
    }

    /**
     * Same as thenSet but with a condition
     *
     * @param condition Condition to check
     * @param fnTrue    A lambda to run if the result of condition is true.
     * @param fnFalse   A lambda to run if the result of condtion is false.
     * @return Self with new value.
     */
    public Box<T> thenSetIf(FxBool<T> condition, FxUnary<T> fnTrue, FxUnary<T> fnFalse) {
        if (this.value == null) return this;
        this.value = (condition.run(this.value)) ? fnTrue.run(this.value) : fnFalse.run(this.value);
        return this;
    }

    /**
     * If the box is empty, execute the Runnable.
     *
     * @param r A runnable to be executed. Since the box is empty, it won't do anything to the box.
     *          To modify the box, use orSet instead.
     * @return Self
     */
    public Box<T> or(Runnable r) {
        if (this.value == null) r.run();
        return this;
    }

    /**
     * If the box is empty, returns the box with a new value
     *
     * @param valueFn A function that returns new value for the box.
     * @return Self
     */
    public Box<T> orSet(Fx01<T> valueFn) {
        if (this.value == null) this.value = valueFn.run();
        return this;
    }

    /**
     * Evaluate the value regardless of whether the box is empty or not.
     *
     * @param evalFn A lambda function to evaluate the value. This does not return anything.
     * @return Self
     */
    public Box<T> eval(Fx10<T> evalFn) {
        evalFn.run(this.value);
        return this;
    }

    /**
     * Validate the box with validation function.
     * This will validate regardless of box's state.
     * If the validation function couldn't validate the content of box, it will throw the throwable.
     *
     * @param validationFn A function validating the value. Returns true if validated.
     * @param throwable    A throwable or an exception to be thrown if didn't get validated.
     * @param <X>          Any throwable types
     * @return Self
     * @throws X Throwable if didn't get validated
     */
    public <X extends Throwable> Box<T> validate(FxBool<T> validationFn, X throwable) throws X {
        if (!validationFn.run(this.value)) throw throwable;
        return this;
    }

    /**
     * Regardless of the box's state (whether full or not), set the content of the box.
     *
     * @param setFn A function returning new value
     * @param <R>   return box type
     * @return A box with new value.
     */
    public <R> Box<R> set(Fx01<R> setFn) {
        return new Box<>(setFn.run());
    }

    /**
     * Regardless of the box's state (whether full or not), set the content of the box.
     *
     * @param newValue New value to be used. This has to be the same type with current.
     * @return A box with new value.
     */
    public Box<T> reset(T newValue) {
        this.value = newValue;
        return this;
    }

    /**
     * Converts box content to a new content in a box.
     *
     * @param mapFn Lambda function which takes current content, and returns a Box with new type of content.
     * @param <R>   New return type
     * @return new boxed R.
     */
    public <R> Box<R> map(Fx11<T, R> mapFn) {
        return new Box<>(this.value == null ? null : mapFn.run(this.value));
    }

    /**
     * Map value to another type. But, if there's an exception thrown,
     * this will fallback.
     *
     * @param fnMap      Mapping function
     * @param fnFallback Fallback function
     * @param <R>        Return value
     * @return New value in new box.
     */
    public <R> Box<R> map(FxThrow<T, R> fnMap, Fx11<Exception, R> fnFallback) {
        if (this.value == null) return of(null);
        try {
            return of(fnMap.run(this.value));
        } catch (Exception e) {
            return of(fnFallback.run(e));
        }
    }

    @Override
    public String toString() {
        return "" + this.value;
    }

}


