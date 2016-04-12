package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class BotState {
    private int MAX_TIMEBANK;
    private int TIME_PER_MOVE;

    private int roundNr;
    private int moveNr;
    private int timebank;
    private String myName;
    private HashMap<String, Player> players;

    private Field field;
    private Move lastMove;

    public BotState() {
        this.players = new HashMap<String, Player>();
        this.field = new Field();
    }

    /**
     * Parses all the game settings given by the engine
     * @param key : type of data given
     * @param value : value
     */
    public void parseSettings(String key, String value) {
        try {
            switch(key) {
                case "timebank":
                    int time = Integer.parseInt(value);
                    this.MAX_TIMEBANK = time;
                    this.timebank = time;
                    break;
                case "time_per_move":
                    this.TIME_PER_MOVE = Integer.parseInt(value);
                    break;
                case "player_names":
                    String[] playerNames = value.split(",");
                    for(int i=0; i<playerNames.length; i++)
                        players.put(playerNames[i], new Player(playerNames[i]));
                    break;
                case "your_bot":
                    this.myName = value;
                    break;
                case "your_botid":
                    int myId = Integer.parseInt(value);
                    int opponentId = 2 - myId + 1;
                    this.field.setMyId(myId);
                    this.field.setOpponentId(opponentId);
                    break;
                case "field_width":
                    this.field.setColumns(Integer.parseInt(value));
                    break;
                case "field_height":
                    this.field.setRows(Integer.parseInt(value));
                    break;
                default:
                    System.err.println(String.format("Cannot parse settings input with key '%s'", key));
            }
        } catch (Exception e) {
            System.err.println(String.format("Cannot parse settings value '%s' for key '%s'", value, key));
        }
    }

    /**
     * Parse data about the game given by the engine
     * @param key : type of data given
     * @param value : value
     */
    public void parseGameData(String key, String value) {
        try {
            switch(key) {
                case "round":
                    this.roundNr = Integer.parseInt(value);
                    break;
                case "move":
                    this.moveNr = Integer.parseInt(value);
                    break;
                case "field":
                    int[][] oldField = field.getRaw();
                    this.field.initField();
                    this.field.parseFromString(value);
                    lastMove = null;
                    for(int x=0; x<field.getColumns(); x++) {
                        for(int y=0; y<field.getRows(); y++) {
                            if (oldField[x][y] != field.get(x,y)) {
                                if (field.get(x,y) == field.getOpponentId()) {
                                    lastMove = new Move(x,y);
                                    break;
                                }
                            }
                        }
                        if (lastMove != null) break;
                    }
                    break;
                default:
                    System.err.println(String.format("Cannot parse game data input with key '%s'", key));
            }
        } catch (Exception e) {
            System.err.println(String.format("Cannot parse game data value '%s' for key '%s'", value, key));
        }
    }

    /**
     * Parse data about given player that the engine has sent
     * @param playerName : player the data is about
     * @param key : type of data given
     * @param value : value
     */
    public void parsePlayerData(String playerName, String key, String value) {
        try {
            switch(key) {
                case "points":
                    this.players.get(playerName).setPoints(Double.parseDouble(value));
                    break;
                default:
                    System.err.println(String.format("Cannot parse %s data input with key '%s'", playerName, key));
            }
        } catch (Exception e) {
            System.err.println(String.format("Cannot parse %s data value '%s' for key '%s'", playerName, value, key));
        }
    }

    public ArrayList<Move> getAvailableMoves() {
        return field.getAvailableMoves();
    }

    public void setTimebank(int value) {
        this.timebank = value;
    }

    public int getTimebank() {
        return this.timebank;
    }

    public Field getField() { return field; }

    public Move getLastMove() {
        return lastMove;
    }

    public int getMoveNumber() {
        return moveNr;
    }
}