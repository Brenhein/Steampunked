package edu.msu.harr1332.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A representation of the playing area
 */
public class PlayingArea{
    // Width of the playing area (integer number of cells)
    private int width;

    // Height of the playing area (integer number of cells
    private int height;

    // Are we dragging a piece
    private boolean dragging = false;

    // Set to a piece we are dragging. If we're not dragging, it is null
    private Pipe draggedPipe = null;


    // Storage for the pipes
    // First level: X, second level Y
    private Pipe [][] pipes;
    private Pipe [] randomPipes;

    // The touches on screen
    private Touch touch1 = new Touch();
    private Touch touch2 = new Touch();

    private Pipe pipeToAdd = null;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;
    // 99.9% of the time you can just ignore this


    /**
     * The nested class that represents a touch on screen
     */
    private class Touch {
        // Touch id
        public int id = -1;

        // Current x location
        public float x = 0;

        // Current y location
        public float y = 0;

        // Previous x location
        public float lastX = 0;

        // Previous y location
        public float lastY = 0;

        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
    }


    /**
     * Construct a playing area
     * @param width Width (integer number of cells)
     * @param height Height (integer number of cells)
     */
    public PlayingArea(int width, int height) {
        this.width = width;
        this.height = height;

        // Allocate the playing area
        // Java automatically initializes all of the locations to null
        pipes = new Pipe[width][height];
        randomPipes = new Pipe[5];

    }

    /**
     * Get the playing area height
     * @return Height
     */
    public int getHeight() { return height; }

    /**
     * Get the playing area width
     * @return Width
     */
    public int getWidth() { return width; }

    /**
     * Sets to true if we are dragging a pipe
     * @param dragging a bool to represent if we are dragging or not
     */
    public void setDraggingPipe(boolean dragging) { this.dragging = dragging; }

    /**
     * Sets the dragging pipe
     * @param draggedPipe the pipe we are currently dragging
     */
    public void setDragging(Pipe draggedPipe) { this.draggedPipe = draggedPipe; }

    /**
     * Get the pipe at a given location.
     * This will return null if outside the playing area.
     * @param x X location
     * @param y Y location
     * @return Reference to Pipe object or null if none exists
     */
    public Pipe getPipe(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        return pipes[x][y];
    }

    public Pipe[][] getPipes() {
        return pipes;
    }

    public void setPipes(Pipe[][] pipes) {
        this.pipes = pipes;
    }

    public Pipe[] getRandomPipes() {
        return randomPipes;
    }

    public void setRandomPipes(Pipe[] randomPipes) {
        this.randomPipes = randomPipes;
    }


    public Pipe getRandomPipe(int x) {
        if (x < 0 || x > 5) {
            return null;
        }
        return randomPipes[x];
    }

    /**
     * Add a pipe to the playing area
     * @param pipe Pipe to add
     * @param x X location
     * @param y Y location
     */
    public void add(Pipe pipe, int x, int y, short owner) {
        pipes[x][y] = pipe;
        pipe.set(this, x, y);
        pipe.setOwner(owner);
    }

    public void addRandom(Pipe pipe, int x, int y, int randomFlag) {
        randomPipes[x] = pipe;
        pipe.set(this, x, y);
        pipe.setRandFlag(randomFlag);
    }

    /**
     * Search to determine if this pipe has no leaks
     * @param start Starting pipe to search from
     * @return true if no leaks
     */
    public boolean search(Pipe start) {
        /*
         * Set the visited flags to false
         */
        for(Pipe[] row: pipes) {
            for(Pipe pipe : row) {
                if (pipe != null) {
                    pipe.setVisited(false);
                }
            }
        }
        return start.search(); // The pipe itself does the actual search
    }


    /**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                touch1.copyToLast();
                view.invalidate();
                return onTouched(touch1.x, touch1.y);

            case MotionEvent.ACTION_POINTER_DOWN:
                if(touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    touch2.copyToLast();
                    view.invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                view.invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                if(id == touch2.id) {
                    touch2.id = -1;
                } else if(id == touch1.id) {
                    // Make what was touch2 now be touch1 by
                    // swapping the objects.
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                view.invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                move();
                view.invalidate();
                return true;
        }
        return false;
    }

    /**
     * Moves a pipe that is currently being dragged
     */
    private void move() {
        if(touch1.id < 0) {
            return;
        }

        if(touch1.id >= 0) {
            if (draggedPipe != null) {
                float dx = touch1.x - touch1.lastX;
                float dy = touch1.y - touch1.lastY;

                draggedPipe.setDraggingX(draggedPipe.getDraggingX() + dx);
                draggedPipe.setDraggingY(draggedPipe.getDraggingY() + dy);
            }

        }

        if(touch2.id >= 0) {
            if (draggedPipe != null) {
                float dx = touch1.x - touch1.lastX;
                float angle1 = angle(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
                float angle2 = angle(touch1.x, touch1.y, touch2.x, touch2.y);
                float da = angle2 - angle1;
                rotate(da, touch1.x, touch1.y);
            }
        }
    }

    /**
     * Determine the angle for two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed angle in degrees
     */
    private float angle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int id = event.getPointerId(i); // Get the pointer id

            // Convert to image coordinates
            float x = (event.getX(i));
            float y = (event.getY(i));

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     * @param x x location for the touch
     * @param y y location for the touch
     * @return true if the touch is handled
     */
    private boolean onTouched(float x, float y) {
        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        if (dragging) {
            if (draggedPipe != null) {
                if (draggedPipe.hit(x, y)) {
                    return true;
                }
            }
        }
        else {
            for (int p = randomPipes.length - 1; p >= 0; p--) {
                if (randomPipes[p] != null) {
                    if (randomPipes[p].hit(x, y)) { // We hit a piece!
                        draggedPipe = randomPipes[p];
                        dragging = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Rotate the image around the point x1, y1
     * @param dAngle Angle to rotate in degrees
     * @param x1 rotation point x
     * @param y1 rotation point y
     */
    public void rotate(float dAngle, float x1, float y1) {
        float angle = draggedPipe.getAngle() + dAngle;
        draggedPipe.setAngle(angle);

        // Compute the radians angle
        double rAngle = Math.toRadians(dAngle);
        float ca = (float) Math.cos(rAngle);
        float sa = (float) Math.sin(rAngle);
        float xp = (draggedPipe.getDraggingX() - x1) * ca - (draggedPipe.getDraggingY() - y1) * sa + x1;
        float yp = (draggedPipe.getDraggingX() - x1) * sa + (draggedPipe.getDraggingY() - y1) * ca + y1;

        draggedPipe.setDraggingX(xp);
        draggedPipe.setDraggingY(yp);
    }

    /**
     *
     */
    public void removeRandomPipe() {
        for(int i = 0; i < 5; i++) {
            if(randomPipes[i] == draggedPipe) {
                randomPipes[i] = null;
                pipeToAdd = draggedPipe;
                dragging = false;
                draggedPipe = null;
                Pipe newPipe = new Pipe(false, false, false, false);
                addRandom( newPipe, i, 5, 1);
            }
        }
    }

    /**
     * Handles setting up the install by getting the pieces around the position to insert
     * @param gameSize the size of the gameboard
     * @param turn whose turn it is
     * @return If the piece was connected
     */
    public Pipe processInstall(int gameSize, short turn, int bounds) {
        // User didn't even grab a pipe
        if (pipeToAdd == null)
        {
            return null;
        }

        int xPos = Math.round(pipeToAdd.getDraggingX()/pipeToAdd.getWidth());
        int yPos = Math.round(pipeToAdd.getDraggingY()/pipeToAdd.getWidth());

        // Handles installs outside the bounds
        if (pipeToAdd.getDraggingY() >= bounds - pipeToAdd.getHeight()) {
            return null;
        }

        if (pipes[xPos][yPos] == null && xPos > 0 && yPos > 0) {
            pipeToAdd.setOwner(turn);
            pipes[xPos][yPos] = pipeToAdd;
            Pipe north = getPipe(xPos, yPos-1);
            Pipe east = getPipe(xPos + 1, yPos);
            Pipe south = getPipe(xPos, yPos + 1);
            Pipe west = getPipe(xPos -1, yPos);

            boolean flag = validateMove( north, east, south, west, turn );
            if (flag) {
                pipeToAdd.setxIndex(xPos);
                pipeToAdd.setyIndex(yPos);
                return pipeToAdd;
            }
            else {
                pipes[xPos][yPos] = null;
                return null;
            }
        }
        return null;
    }

    /**
     * Validates the move of a player to see if there is a posssible connection, rotating the
     * image to the nearest 90 degrees
     * @param north The north pipe, if any
     * @param east The east pipe, if any
     * @param south The south pipe, if any
     * @param west The west pipe, if any
     * @param turn whose turn it is
     * @return If it was a valid move
     */
    private boolean validateMove(Pipe north, Pipe east, Pipe south, Pipe west, short turn) {
        boolean[] pipeConnections = pipeToAdd.getConnections();

        // Rotates to the nearest 90 degrees
        double angle = pipeToAdd.getAngle();
        double nearest90 = Math.round(angle / 90) * 90; // gets the nearest 90 degrees
        int shiftIndex = Math.abs((int)nearest90 / 90);

        // Shifts the temporary connections by the shift index
        boolean[] temp = {false, false, false, false};
        for(int i = 0; i <= 3; i++){
            temp[(i + shiftIndex) % 4 ] = pipeConnections[i];
        }
        pipeConnections = temp;

        boolean connect = false;
        if (pipeConnections[0]){
            if (north != null /*&& north.getOwner() == turn*/) {
                if (north.getConnections()[2]) {
                    connect = true;
                    pipeConnections[0] = false;
                    boolean[] nConnect = north.getConnections();
                    north.setConnections(nConnect[0], nConnect[1], false, nConnect[3]);
                }
            }
        }
        if (pipeConnections[2]){
            if (south != null /*&& south.getOwner() == turn*/) {
                if (south.getConnections()[0]) {
                    connect = true;
                    pipeConnections[2] = false;
                    boolean[] sConnect = south.getConnections();
                    south.setConnections(false, sConnect[1], sConnect[2], sConnect[3]);
                }
            }
        }
        if (pipeConnections[3]) {
            if (west != null /*&& west.getOwner() == turn*/) {
                if (west.getConnections()[1]) {
                    connect = true;
                    pipeConnections[3] = false;
                    boolean[] wConnect = west.getConnections();
                    west.setConnections(wConnect[0], false, wConnect[2], wConnect[3]);
                }
            }
        }
        if (pipeConnections[1]) { // moved to last check to make sure the pipe is connected to other pieces before connecting to gauge
            if (east != null /*&& east.getOwner() == turn*/) {
                if (east.getConnections()[3]) {
                    if (!east.gauge || connect) { // checks to make sure you aren't placing on a gauge without other connections
                        connect = true;
                        pipeConnections[1] = false;
                        boolean[] eConnect = east.getConnections();
                        east.setConnections(eConnect[0], eConnect[1], eConnect[2], false);
                    }
                }
            }
        }

        if (connect) { // we got a valid connect
            pipeToAdd.setConnections( pipeConnections[0], pipeConnections[1],
                                      pipeConnections[2], pipeConnections[3] );
            pipeToAdd.setAngle((float)nearest90);
        }

        return connect;
    }

    /**
     * Checks to see if a player opens a valve, there will be no steam leaks
     * @param turn The player who hit open valve
     * @return an array of openings, if any
     */
    public ArrayList<Pair<Pipe, Integer>> processOpen(int turn) {
        ArrayList<Pair<Pipe, Integer>> openings = new ArrayList<Pair<Pipe, Integer>>();
        for(Pipe[] row: pipes) {
            for (Pipe pipe : row) {
                if (pipe != null && pipe.getOwner() == turn && !pipe.gauge) {
                    boolean[] connections = pipe.getConnections();
                    for (int i = 0; i < 4; i++) { // loop through connections
                        if (connections[i]) { //if there is an opening
                            openings.add(new Pair(pipe, i));
                        }
                    }
                }
            }
        }
        return openings;
    }
}
