package com.company;

import java.util.Scanner;

public class BotParser {

    final Scanner scan;
    final Main bot;

    private BotState currentState;

    public BotParser(Main bot) {
        this.scan = new Scanner(System.in);
        this.bot = bot;
        this.currentState = new BotState();
    }

    public void run() {
        while(scan.hasNextLine()) {
            String line = scan.nextLine();

            if(line.length() == 0) {
                continue;
            }

            String[] parts = line.split(" ");
            if(parts[0].equals("settings")) {
                this.currentState.parseSettings(parts[1], parts[2]);
            } else if(parts[0].equals("update")) { /* new game data */
                if (parts[1].equals("game")) {
                    if (parts.length > 4) {
                        this.currentState.parseGameDataLong(parts);
                    } else {
                        this.currentState.parseGameData(parts[2], parts[3]);
                    }
                } else {
                    this.currentState.parsePlayerData(parts[1], parts[2], parts[3]);
                }
            } else if(parts[0].equals("action")) {
                if (parts[1].equals("move")) { /* move requested */
                    Move move = null;
                    if (currentState.getLastMove() == null && currentState.getUs() != null && currentState.getThem() != null && currentState.getUs().getPoints() > 10 && currentState.getUs().getPoints() > currentState.getThem().getPoints()) {
                        System.err.println("Passing because they passed and " + currentState.getUs().getPoints()+" > "+currentState.getThem().getPoints());
                    } else {
                        move = this.bot.getMove(this.currentState, Integer.parseInt(parts[2]));
                    }

                    if (move != null) {
                        System.out.println("place_move " + move.getX() + " " + move.getY());
                    } else {
                        System.out.println("pass");
                    }
                }
            } else {
                System.out.println("unknown command");
            }
        }
    }
}