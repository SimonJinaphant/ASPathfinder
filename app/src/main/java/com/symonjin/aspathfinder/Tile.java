package com.symonjin.aspathfinder;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Tile implements Comparable<Tile>{
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public boolean hasVisited() {
        return visited;
    }

    public void visit() {
        this.visited = true;
        updateState(State.VISITED);
    }

    public boolean isClosed() {
        return closed;
    }

    public void closed() {
        this.closed = true;
    }

    public boolean isWall(){
        return wall;
    }

    public void toggleWall(){
        if(this.wall){
            updateState(State.NORMAL);
        }else{
            updateState(State.WALL);
        }
        this.wall = !this.wall;
    }

    private int x, y;
    private boolean visited;
    private boolean closed;
    private boolean wall;
    private State status;

    public int movementCost;
    public double heuristicScore;
    public Tile parent;


    public static enum State{
        NORMAL("#F6F6F6"),
        WALL("#2A2E2F"),
        START("#47BA7D"),
        FINISH("#AE264C"),
        VISITED("#D8D8D8"),
        PATH("#20ADD2");

        private Paint brush;
        State(String color){
            brush = new Paint();
            brush.setColor(Color.parseColor(color));
        }

    }

    public Paint draw(){
        return status.brush;
    }

    @Override
    public int compareTo(Tile other) {
        return (int)((this.movementCost + this.heuristicScore) - (other.movementCost + other.heuristicScore));
        /*
        if(other.movementCost + other.heuristicScore == this.movementCost + this.heuristicScore){
            return 0;
        } else if (other.movementCost + other.heuristicScore > this.movementCost + this.heuristicScore){
            return -1;
        } else{
            return 1;
        }*/
    }


    public boolean equals(Tile o) {
        return x == o.getX() && y == o.getY();
    }

    public void info(){
        Log.i("Tile", "(" +x+ ", " +y+ ", H:" + heuristicScore + ", G:" + movementCost + ", C:" +closed+", W:"+wall+ " V:"+visited+")");
    }

    public Tile(int x, int y, State status){
        this.x = x;
        this.y = y;
        this.visited = false;
        this.closed = false;
        this.wall = (status == State.WALL);

        this.movementCost = 0;
        this.heuristicScore = 0;
        this.parent = null;

        updateState(status);
    }

    public void updateState(State newStatus){
        if(!(status == State.START || status == State.FINISH)){
            this.status = newStatus;
        }
    }

    public void reset(boolean preserveWall){
        this.movementCost = 0;
        this.heuristicScore = 0;
        this.visited = false;
        this.closed = false;
        this.parent = null;
        if(!preserveWall) updateState(State.NORMAL);
    }


    public State getStatus(){
        return status;
    }

}
