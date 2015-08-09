package com.symonjin.aspathfinder;

import java.util.PriorityQueue;

public class GridMap {
    private Tile[][] map;
    private Tile start;
    private Tile finish;

    public GridMap(int width, int height){
        map = new Tile[height][width];

        for(int y = 0; y < height; y++){
            for (int x = 0; x < width; x++) {
                map[y][x] = new Tile(x, y, Tile.State.NORMAL);
            }
        }

        start = map[0][0];
        start.updateState(Tile.State.START);

        finish = map[height-1][width-1];
        finish.updateState(Tile.State.FINISH);

    }



    public void generatePath(){
        // output = new Path();
        //ArrayList<Tile> tilePath = new ArrayList<Tile>();
        PriorityQueue<Tile> openTiles = new PriorityQueue<Tile>();
        openTiles.add(start);

        while (!openTiles.isEmpty()) {
            Tile current = openTiles.poll();
            //current.info();

            if (current.equals(finish)) {
                //Log.i("Pathfinder", "A path was found! ");
                while (current != null) {
                    current.updateState(Tile.State.PATH);
                    //tilePath.add(current);
                    current = current.parent;
                }
                break;
            }

            current.closed();

            for (int y = -1; y < 2; y++) {
                for (int x = -1; x < 2; x++) {
                    //The following conditional prevents us from checking our current tile
                    if (Math.abs(y) + Math.abs(x) != 0) {
                        Tile neighbour = getTileAt(current.getX() + x, current.getY() + y);

                        if (neighbour == null || neighbour.isClosed() || neighbour.isWall()) {
                            //This neighbouring tile is not worth analysing
                            continue;
                        }

                        if (Math.abs(y) + Math.abs(x) == 2) {
                            Tile a = getTileAt(current.getX(), current.getY() + y);
                            Tile b = getTileAt(current.getX() + x, current.getY());
                            if ((a != null && a.isWall() || (b != null && b.isWall()))) {
                                continue;
                            }
                            //This diagonal neighbouring cell cannot be reached as there is
                            //a wall blocking us from diagonal movement

                        }

                        int newMovementCost = current.movementCost + (Math.abs(y) + Math.abs(x) == 2 ? 14 : 10);
                        boolean visited = neighbour.hasVisited();

                        if (!visited || newMovementCost < neighbour.movementCost) {
                            neighbour.visit();
                            neighbour.parent = current;
                            neighbour.movementCost = newMovementCost;
                            calculateHeuristic(neighbour);

                            if (!visited) {
                                //Neighbour has not be analysed before
                                //Therefore add it to the queue of tiles to be processed
                                openTiles.offer(neighbour);

                            } else {
                                //Neighbour has been analysed before
                                //Update its scores by removing the old one and putting the updated one back in
                                openTiles.remove(neighbour);
                                openTiles.offer(neighbour);
                            }
                        }


                    }
                }
            }


        }


        //Process path tiles here if needed...
    }

    private void calculateHeuristic(Tile tile){
        int dx1 = tile.getX() - finish.getX();
        int dy1 = tile.getY() - finish.getY();

        int dx2 = start.getX() - finish.getX();
        int dy2 = start.getY() - finish.getY();
        int cross = Math.abs(dx1*dy2 - dx2*dy1);

        int x = Math.abs(tile.getX() - finish.getX());
        int y = Math.abs(tile.getY() - finish.getY());

        if(x > y){
            tile.heuristicScore = ((14*y + 10*(x-y)) + cross*0.5);
        } else {
            tile.heuristicScore = ((14*x + 10*(y-x)) + cross*0.5);
        }


    }

    private boolean withinMap(int x, int y){
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }

    public Tile getTileAt(int x, int y){
        return withinMap(x,y) ? map[y][x] : null;
    }

    public void toggleWall(int x, int y){
        if(withinMap(x,y) && !(map[y][x] == start || map[y][x] == finish)){
            map[y][x].toggleWall();
        }
    }


    public int getHeight(){return map.length;}
    public int getWidth(){return map[0].length;}


    public void reset(){
        for(int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++) {
                map[y][x].reset(map[y][x].isWall());
            }

        }
        //Log.i("Pathfinder", "Resetting map...");
    }

}
