package edu.msu.harr1332.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "steamgame")
public class Game {
    @Attribute
    private String player1;

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }
    @Attribute
    private String player2;

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    @Attribute
    private int gameSize;

    public int getGameSize() {
        return gameSize;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    @Attribute
    private int gameId;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Attribute
    private String pipes;

    public String getPipes() {
        return pipes;
    }

    public void setPipes(String pipes) {
        this.pipes = pipes;
    }

    @Attribute
    private String randomPipes;

    public String getRandomPipes() {
        return randomPipes;
    }

    public void setRandomPipes(String randomPipes) {
        this.randomPipes = randomPipes;
    }

    @Attribute
    private String winner;

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Attribute
    private int turn;

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Game() {}

    public Game(int id, String player1, String player2, int gameSize, String pipes, String randomPipes, String winner, int turn) {
        this.gameId = id;
        this.player1 = player1;
        this.player2 = player2;
        this.gameSize= gameSize;
        this.pipes = pipes;
        this.randomPipes = randomPipes;
        this.winner = winner;
        this.turn = turn;
    }
}
