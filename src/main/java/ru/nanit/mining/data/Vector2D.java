package ru.nanit.mining.data;

public class Vector2D {

    private float x;
    private float y;

    public Vector2D(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    @Override
    public String toString(){
        return "[" + x + "," + y + "]";
    }
}
