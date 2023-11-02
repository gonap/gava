/*
 * Gava Library
 * <https://gonn.org> [++]
 * Copyright (c) 2023 Gon Yi. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.gonn.gava;

public class Tester {
    private final StringBuilder sb;
    private int failed;
    private int passed;

    public Tester(int capacity) {
        this.sb = new StringBuilder(capacity);
        this.passed = 0;
        this.failed = 0;
    }

    public Tester() {this(200);}

    public static boolean test() {
        Tester res = new Tester();
        {
            Tester tmp = new Tester();
            tmp.add(true, false, true)
                    .add(false, true)
                    .add(true, true, true);
            res.add(tmp.toString().equals("OXO-XO-OOO"),
                    tmp.totalFailed() == 2,
                    tmp.totalPassed() == 6);
        }
        return res.passed();
    }

    public static void main(String[] args) {
        System.out.println(test());
    }

    public Tester add(boolean... results) {
        if (sb.length() > 0) sb.append('-');
        for (boolean result : results) {
            sb.append(result ? 'O' : 'X');
            if (result) {
                this.passed += 1;
            } else {
                this.failed += 1;
            }
        }
        return this;
    }

    public Tester reset() {
        this.sb.setLength(0);
        this.passed = 0;
        this.failed = 0;
        return this;
    }

    public int totalPassed() {return this.passed;}

    public int totalFailed() {return this.failed;}

    public boolean passed() {return this.failed == 0;}

    public boolean failed() {return this.failed > 0;}

    @Override
    public String toString() {return this.sb.toString();}

    public void print() {System.out.println(this);}
}

