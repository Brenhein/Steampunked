package edu.msu.harr1332.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "steampunked")
public class GameResult {
    @Attribute(name = "msg", required = false)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Attribute
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Attribute(name="gameID")
    private int gameID;

    public int getGameID(){return gameID;}

    public void setGameID(int gameID) {this.gameID = gameID;}


    public GameResult() {}

    public GameResult(String status, String msg) {
        this.status = status;
        this.message = msg;
    }
}
