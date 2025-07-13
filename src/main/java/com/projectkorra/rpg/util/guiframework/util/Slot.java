package com.projectkorra.rpg.util.guiframework.util;

import java.util.Objects;

public class Slot {
    private final int x;
    private final int y;

    private static final int COLUMNS = 9;

    public Slot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Slot fromIndex(int index) {
        return new Slot(index % COLUMNS, index / COLUMNS);
    }

    public int getIndex() {
        return y * COLUMNS + x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return x == slot.x && y == slot.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Slot{" + "x=" + x + ", y=" + y + '}';
    }
}
