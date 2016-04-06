package com.company;

public class Move {
    int mX, mY;

    public Move() {
    }

    public Move(int x, int y) {
        mX = x;
        mY = y;
    }

    public int getX() { return mX; }
    public int getY() { return mY; }

    public String toString() { return mX+","+mY; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (mX != move.mX) return false;
        return mY == move.mY;

    }

    @Override
    public int hashCode() {
        int result = mX;
        result = 31 * result + mY;
        return result;
    }
}
