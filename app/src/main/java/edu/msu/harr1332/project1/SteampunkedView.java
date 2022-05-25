package edu.msu.harr1332.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Custom view class for our Puzzle.
 */
public class SteampunkedView extends View {
    /// Paint objects
    Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /// Member variable for openings
    ArrayList<Pair<Pipe, Integer>> mOpenings;

    private int pixelSpacing;

   // Gameboard activity
    GameBoardActivity activity;

    private PlayingArea playingArea;
    private boolean firstDraw = true;
    private int gameSize = 0;
    private Bitmap handle;

    private boolean randDraw = true;

    /**
     * The pipe bitmaps. None initially.
     */
    private Bitmap straight = null;
    private Bitmap gauge = null;

    private Bitmap handle1 = null;

    public SteampunkedView(Context context) {
        super(context);
        init(null, 0);

    }

    public SteampunkedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SteampunkedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.playingArea.onTouchEvent(this, event);
    }


    public void handleDiscard() {
        playingArea.removeRandomPipe();
        randDraw = true;
        invalidate();
    }

    public Pipe handleInstall(short turn) {
        playingArea.removeRandomPipe();
        randDraw = true;
        Pipe success = playingArea.processInstall(gameSize, turn, gameSize * pixelSpacing);
        invalidate();
        if (success == null) {
            Toast.makeText(this.getContext(),
                    R.string.move_not_valid,
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        return success;
    }

    public boolean handleOpen(int turn) {
        ArrayList<Pair<Pipe, Integer>> openings = playingArea.processOpen(turn);
        if (openings.size() > 0) {
            for (Pair<Pipe, Integer> open : openings) {
                Pipe p = open.first;
                int i = open.second;
            }
            mOpenings = openings;
            return false;
        }
        return true;
    }

    /**
     * Starts the game and initializes the location of all the pieces
     * @param gameSize the size of the game board
     */
    public void startGame(int gameSize, GameBoardActivity act) {
        // Creates paint
        outlinePaint.setColor(0xff008000);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(10);
        fillPaint.setColor(0xffcccccc);


        // sets the parent activity
        activity = act;

        // Sets the game size
        setPlayingArea(gameSize);
        this.gameSize = gameSize;

        invalidate();

        // Gets the location for the starting pipes
        int start_row = Math.round(gameSize / 2);
        int offset = Math.round(gameSize / 3);

        // Adds the starting pipes to the game
        playingArea.add(new Pipe(false, true, false, false),
                0, start_row - offset, (short) 1);
        playingArea.add(new Pipe(false, true, false, false),
                0, start_row + offset, (short) 2);

        // Adds 5 random pieces at the bottom of the screen
        playingArea.addRandom(new Pipe(false, false, false, false),
                0, 5, 1);
        playingArea.addRandom(new Pipe(false, false, false, false),
                1, 5, 1);
        playingArea.addRandom(new Pipe(false, false, false, false),
                2, 5, 1);
        playingArea.addRandom(new Pipe(false, false, false, false),
                3, 5, 1);
        playingArea.addRandom(new Pipe(false, false, false, false),
                4, 5, 1);

        // Adds the ending pipes to the screen, OFFSET by 1 so that the player can't build
        // directly across
        playingArea.add(new Gauge(false, false, false, true),
                gameSize - 1, start_row - offset + 1, (short) 1);
        playingArea.add(new Gauge(false, false, false, true),
                gameSize - 1, start_row + offset + 1, (short) 2);

        handle1 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.handle);
    }

    /**
     * Creates a new playing area of size gamesize (5x5, 10x10, or 20x20)
     * @param gameSize The size of the game board
     */
    public void setPlayingArea(int gameSize) {
        playingArea = new PlayingArea(gameSize, gameSize);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int start_row = Math.round(gameSize / 2);
        int offset = Math.round(gameSize / 3);

        // Creates turn string
        String playerTurn = "Current Turn: ";

        // Gets players
        String player1Name = activity.GetPlayer1Name();
        String player2Name = activity.GetPlayer2Name();
        boolean player1Turn = activity.GetPlayer1Turn();
        boolean player2Turn = activity.GetPlayer2Turn();

        if (player1Turn) {
            playerTurn += player1Name;
        } else if (player2Turn) {
            playerTurn += player2Name;
        }

        // Draws the text
        TextPaint paint = new TextPaint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
        float x = getWidth() / 2;
        float y = getHeight() - 200;
        paint.setTextSize(100);
        canvas.drawText(playerTurn, x, y, paint);

        if (firstDraw) {
            int boardPixels = Math.min(getHeight(), getWidth());
            pixelSpacing = boardPixels / gameSize;

            handle = BitmapFactory.decodeResource(getResources(), R.drawable.handle);
            handle = Bitmap.createScaledBitmap(handle, pixelSpacing, pixelSpacing, true);

            canvas.drawBitmap(handle, 0, (start_row - offset) * pixelSpacing, null);
            canvas.drawBitmap(handle, 0, (start_row + offset) * pixelSpacing, null);

            // Draws rectangle to hold playing area
            canvas.drawRect(0, 0, activity.GetGameSize() * pixelSpacing, activity.GetGameSize() * pixelSpacing, fillPaint);
            canvas.drawRect(0, 0, activity.GetGameSize() * pixelSpacing, activity.GetGameSize() * pixelSpacing, outlinePaint);

            // Draws the player names
            TextPaint paint1 = new TextPaint();
            paint1.setTextAlign(Paint.Align.CENTER);
            paint1.setTypeface(Typeface.create("Arial", Typeface.BOLD));
            paint1.setTextSize(60);

            float x1 = pixelSpacing / 2;
            float y1 = (start_row * pixelSpacing) + pixelSpacing / 5;
            float y2 = (start_row + 2 * offset) * pixelSpacing + pixelSpacing / 5;

            canvas.drawText(player1Name, x1, y1, paint1);
            canvas.drawText(player2Name, x1, y2, paint1);
        }

        /*
         * Draw the pipe bitmaps
         */
        for (int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                float pixelX = i * pixelSpacing;
                float pixelY = j * pixelSpacing;
                Pipe pipe = playingArea.getPipe(i, j);
                if (pipe == null) {
                    continue;
                }
                Bitmap bitmap = pipe.getBitmap(getContext(), pixelSpacing);

                canvas.save();

                canvas.translate(pixelX + bitmap.getWidth() / 2, pixelY + bitmap.getHeight() / 2);
                canvas.rotate(pipe.getAngle());
                canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, -bitmap.getHeight() / 2, null);

                canvas.restore();


            }

        }
        if (randDraw) {
            for (int i = 0; i < 5; i++) {
                float pixelX = 0;
                float pixelY = gameSize * pixelSpacing;
                if (gameSize == 5) {
                    pixelX = i * pixelSpacing;
                } else if (gameSize == 10) {
                    pixelX = i * (2 * pixelSpacing);
                } else {
                    pixelX = i * (4 * pixelSpacing);
                }
                Pipe pipe = playingArea.getRandomPipe(i);
                if (pipe == null) {
                    continue;
                }
                pipe.setDraggingX(pixelX);
                pipe.setDraggingY(pixelY);
                Bitmap bitmap = pipe.getBitmap(getContext(), pixelSpacing);
                canvas.drawBitmap(bitmap, pixelX, pixelY, null);

            }

            canvas.drawBitmap(handle, 0, (start_row - offset) * pixelSpacing, null);
            canvas.drawBitmap(handle, 0, (start_row + offset) * pixelSpacing, null);
            randDraw = false;
        } else {
            for (int i = 0; i < 5; i++) {
                Pipe pipe = playingArea.getRandomPipe(i);
                if (pipe == null) {
                    continue;
                }
                pipe.draw(canvas, null);

            }
            canvas.drawBitmap(handle, 0, (start_row - offset) * pixelSpacing, null);
            canvas.drawBitmap(handle, 0, (start_row + offset) * pixelSpacing, null);

        }

        // Draws steam on failed opening
        //if (mOpenings.size() > 0) {
          //  for (Pair<Pipe, Integer> opening : mOpenings) {
                //
           // }
        //}
    }



    public Pipe[][] getPipes() {
        return playingArea.getPipes();
    }

    public void setPipes(Pipe[][] pipes) {
        playingArea.setPipes(pipes);
    }

    public Pipe[] getRandomPipes() {
        return playingArea.getRandomPipes();
    }

    public void setRandomPipes(Pipe[] randomPipes) {
        playingArea.setRandomPipes(randomPipes);
    }


    public void putToBundle(Bundle outState) {

    }
}

