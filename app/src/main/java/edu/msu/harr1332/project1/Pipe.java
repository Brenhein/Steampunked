package edu.msu.harr1332.project1;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * An example of how a pipe might be represented.
 */
public class Pipe implements Serializable{
    // Owner of the pipe
    private short owner;

    // Getter/setter for owner
    short getOwner() {return owner;}
    void setOwner(short owner) {this.owner = owner;}

    // Is it a gauge
    boolean gauge = false;

    /**
     * Playing area this pipe is a member of
     */
    private PlayingArea playingArea = null;


    /**
     * The image for the actual pipe.
     */
    protected Bitmap pipe;

    /**
     * The current parameters
     */
    //private Parameters params = new Parameters();

    /**
     * Array that indicates which sides of this pipe
     * has flanges. The order is north, east, south, west.
     *
     * As an example, a T that has a horizontal pipe
     * with the T open to the bottom would be:
     *
     * false, true, true, true
     */
    protected boolean[] connect = {false, false, false, false};


    /**
     * X location in the playing area (index into array)
     */
    private int x = 0;

    /**
     * Y location in the playing area (index into array)
     */
    private int y = 0;


    private int xIndex = 0;

    private int yIndex = 0;

    private int randFlag = 0;

    // X, Y location and angle
    private float draggingY = 0;
    private float draggingX = 0;
    private float angle = 0;



    private String bitmapName;

    /**
     * Depth-first visited visited
     */
    protected boolean visited = false;

    private int randomValue = 0;

    /**
     * Constructor
     * @param north True if connected north
     * @param east True if connected east
     * @param south True if connected south
     * @param west True if connected west
     */
    public Pipe(boolean north, boolean east, boolean south, boolean west) {
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;
    }

    /*Getter and Setter for Angle*/
    public float getAngle() {return angle;}
    public void setAngle(float angle) {this.angle = angle;}

    /*Getter and Setter for X and Y*/
    public float getDraggingY() { return draggingY; }
    public float getDraggingX() { return draggingX; }

    public void setConnections(boolean north, boolean east, boolean south, boolean west) {
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;
    }

    public int getxIndex() {
        return xIndex;
    }

    public void setxIndex(int xIndex) {
        this.xIndex = xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }

    public void setyIndex(int yIndex) {
        this.yIndex = yIndex;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }



    /**
     * gets connection array
     * @return connections as boolean array
     */
    public boolean[] getConnections() { return connect; }

    public void setDraggingY(float draggingY) {
        this.draggingY = draggingY;
    }
    public void setDraggingX(float draggingX) {
        this.draggingX = draggingX;
    }

    public int getWidth() {
        return pipe.getWidth();
    }

    public int getHeight() {
        return pipe.getHeight();
    }

    public Bitmap getPipeBitmap() {
        return pipe;
    }

    /**
     * Sets pipe bitmap
     * @param bitmap bitmap to set pipe to
     */
    public void setPipe(Bitmap bitmap) { pipe = bitmap; }

    public void setRandFlag(int randomFlag) {
        randFlag = randomFlag;

        ArrayList<Integer> random_list = getRandomList();
        randomValue = random_list.get(0);
    }

    public ArrayList getRandomList() {
        ArrayList<Integer> myList = new ArrayList<>();
        myList.add(0);
        myList.add(1);
        myList.add(2);
        myList.add(3);
        Collections.shuffle(myList);
        return myList;
    }

    public String getBitmapName() {
        return bitmapName;
    }

    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    /**
     * Search to see if there are any downstream of this pipe
     *
     * This does a simple depth-first search to find any connections
     * that are not, in turn, connected to another pipe. It also
     * set the visited flag in all pipes it does visit, so you can
     * tell if a pipe is reachable from this pipe by checking that flag.
     * @return True if no leaks in the pipe
     */
    public boolean search() {
        visited = true;

        for(int d=0; d<4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if(!connect[d]) {
                continue;
            }

            Pipe n = neighbor(d);
            if(n == null) {
                // We leak
                // We have a connection with nothing on the other side
                return false;
            }

            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if(!n.connect[dp]) {
                // We have a bad connection, the other side is not
                // a flange to connect to
                return false;
            }

            if(n.visited) {
                // Already visited this one, so no leaks this way
                continue;
            } else {
                // Is there a lead in that direction
                if(!n.search()) {
                    // We found a leak downstream of this pipe
                    return false;
                }
            }
        }

        // Yah, no leaks
        return true;
    }

    /**
     * Find the neighbor of this pipe
     * @param d Index (north=0, east=1, south=2, west=3)
     * @return Pipe object or null if no neighbor
     */
    private Pipe neighbor(int d) {
        switch(d) {
            case 0:
                return playingArea.getPipe(x, y-1);

            case 1:
                return playingArea.getPipe(x+1, y);

            case 2:
                return playingArea.getPipe(x, y+1);

            case 3:
                return playingArea.getPipe(x-1, y);
        }

        return null;
    }

    /**
     * Get the playing area
     * @return Playing area object
     */
    public PlayingArea getPlayingArea() {
        return playingArea;
    }

    /**
     * Set the playing area and location for this pipe
     * @param playingArea Playing area we are a member of
     * @param x X index
     * @param y Y index
     */
    public void set(PlayingArea playingArea, int x, int y) {
        this.playingArea = playingArea;
        this.x = x;
        this.y = y;
    }

    /**
     * Draw the puzzle piece
     * @param canvas Canvas we are drawing on
     */
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();

        // Draws the piece on a new canvas
        canvas.translate(draggingX + pipe.getWidth()/2, draggingY + pipe.getHeight()/2);
        canvas.rotate(angle);
        canvas.drawBitmap(pipe, -pipe.getWidth()/2, -pipe.getHeight()/2, null);

        canvas.restore();
    }

    /**
     * Has this pipe been visited by a search?
     * @return True if yes
     */
    public boolean beenVisited() {
        return visited;
    }

    /**
     * Set the visited flag for this pipe
     * @param visited Value to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Bitmap getBitmap(Context context, int pixelSpacing) {
        if (pipe != null) { // return current bitmap
            return pipe;
        }

        else {
            if (randFlag != 1) {
                setPipe(BitmapFactory.decodeResource(context.getResources(), R.drawable.straight));
                setBitmapName("straight");
            }
            else {
                if (randomValue == 0) {
                    setPipe(BitmapFactory.decodeResource(context.getResources(), R.drawable.bend));
                    connect[2] = true;
                    connect[3] = true;
                    setBitmapName("bend");
                }
                else if (randomValue == 1) {
                    setPipe(BitmapFactory.decodeResource(context.getResources(), R.drawable.straight));
                    connect[1] = true;
                    connect[3] = true;
                    setBitmapName("straight");
                }
                else if (randomValue == 2) {
                    setPipe(BitmapFactory.decodeResource(context.getResources(), R.drawable.tee));
                    connect[1] = true;
                    connect[2] = true;
                    connect[3] = true;
                    setBitmapName("tee");
                }
                else {
                    setPipe(BitmapFactory.decodeResource(context.getResources(), R.drawable.cap));
                    connect[3] = true;
                    setBitmapName("cap");
                }
            }

            if (!gauge) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                pipe = Bitmap.createBitmap(pipe, 0, 0, pipe.getWidth(), pipe.getHeight(), matrix, true);
                pipe = Bitmap.createScaledBitmap(pipe, pixelSpacing, pixelSpacing, true);
            }
        }
        return pipe;
    }

    /**
     * Test to see if we have touched a puzzle piece
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY) {
        int height = pipe.getHeight();
        int width = pipe.getWidth();
        boolean xFlag = false;
        boolean yFlag = false;
        if (testX - draggingX < (float)width && testX - draggingX > 0) {
            xFlag = true;
        }
        if (testY - draggingY < (float)height && testY - draggingY > 0) {
            yFlag = true;
        }
        if (xFlag && yFlag) {
            return true;
        }
        return false;
    }

    /**
     * Move the puzzle piece by dx, dy
     * @param dx x amount to move
     * @param dy y amount to move
     */
    public void move(float dx, float dy) {
        draggingX += dx;
        draggingY += dy;
    }

    //private static class Parameters implements Serializable {}
}