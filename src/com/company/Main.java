package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Main {

    private NeuralNet net = new NeuralNet();
    private Random r = new Random();
    private Move lastMove = null;
    private Move focus = null;

    public Move getMove(BotState state, int timeout) {

        state.setTimebank(timeout);

        ArrayList<Move> availableMoves = state.getAvailableMoves();
        int moveCount = availableMoves.size();

        if (moveCount <= 0) return null;

        Move move = null;

        int[] inputs = new int[320];
        Field f = state.getField();

        availableMoves.remove(lastMove); // naive ko protection

        // remove edge and center for the first 20 moves
        if (state.getMoveNumber() < 20) {
            int i = 0;
            while (i < availableMoves.size()) {
                Move candidate = availableMoves.get(0);
                if (candidate.getX() < 2 || candidate.getY() < 2 || candidate.getX() >= 17 || candidate.getY() >= 17) {
                    availableMoves.remove(i);
                    continue;
                }
                if (candidate.getX() > 3 && candidate.getX() < 16 && candidate.getY() > 3 && candidate.getY() < 16) {
                    availableMoves.remove(i);
                    continue;
                }
                i++;
            }
        }

        // ignore their play if it's not close to us
        Move focus = state.getLastMove();
        boolean closeMove = false;
        if (focus != null) {
            for(int x = focus.getX()-1; x<=focus.getX()+1; x++) {
                for(int y = focus.getY()-1; x<=focus.getY()+1; x++) {
                    if (x>=0 && y>=0 && x<f.getColumns() && y<f.getRows()) {
                        if (f.get(x,y) == f.getMyId()) {
                            closeMove = true;
                        }
                    }
                }
            }

            if (!closeMove && lastMove != null) {
                focus = lastMove;

                // every now and then consider the whole board (as well as early on)
                if (Math.random() < 0.1 || state.getMoveNumber() < 20) {
                    focus = null;
                }
            }
        }

        if (isEmptyRegion(f, 2, 2)) {
            move = cornerPlay(2,2, 2,3, 3,3);
        } else if (isEmptyRegion(f,16,16)) {
            move = cornerPlay(16,16, 16,15, 15,15);
        } else if (isEmptyRegion(f,2,16)) {
            move = cornerPlay(2,16, 2,15, 3,15);
        } else if (isEmptyRegion(f,16,3)) {
            move = cornerPlay(16, 2, 16, 3, 15, 3);
        }

        Move backupMove = null;

        if (move == null) {
            if (focus == null) {
                Collections.shuffle(availableMoves);
            } else {
                int x = focus.getX();
                int y = focus.getY();
                Collections.sort(availableMoves, new Comparator<Move>() {
                    @Override
                    public int compare(Move o1, Move o2) {
                        int dist1 = Math.max(Math.abs(o1.getX() - x), Math.abs(o1.getY() - y));
                        int dist2 = Math.max(Math.abs(o2.getX() - x), Math.abs(o2.getY() - y));

                        return Integer.compare(dist1, dist2);
                    }
                });
            }

            for (Move candidate : availableMoves) {
                if (state.getField().isCaptureMove(candidate.getX(), candidate.getY())) {
                    backupMove = candidate;
                    if (isDesired(inputs, f, candidate)) {
                        System.err.println("Capture move! " + candidate);
                        move = candidate;
                        break;
                    } else {
                        System.err.println("Capture move is undesired: " + candidate);
                    }
                } else if (state.getField().isDefendMove(candidate.getX(), candidate.getY())) {
                    backupMove = candidate;
                    if (isDesired(inputs, f, candidate)) {
                        System.err.println("Defend move! " + candidate);
                        move = candidate;
                        break;
                    } else {
                        System.err.println("Defend move is undesired: " + candidate);
                    }
                }
            }
        }

        if (backupMove != null) {
            for(int y=0; y<19; y++) {
                for(int x=0; x<19; x++) {
                    int v = f.get(x,y);
                    if (v == 0) {
                        if (f.isCaptureMove(x,y)) {
                            System.err.print("c");
                        } else if (f.isDefendMove(x,y)) {
                            System.err.print("d");
                        } else {
                            System.err.print(".");
                        }
                    } else {
                        System.err.print(v);
                    }
                }
                System.err.println();

            }
        }

        while(move == null && !availableMoves.isEmpty()) {
            int index = 0; //r.nextInt(availableMoves.size());
            Move candidate = availableMoves.get(index);
            availableMoves.remove(index);

            boolean desired = isDesired(inputs, f, candidate);
            System.err.println("Desired? " + desired + " for " + candidate);

            if (desired) {
                move = candidate;
                break;
            }
        }

        if (move == null) {
            move = backupMove;
        }

        lastMove = move;

        return move;
    }

    private boolean isDesired(int[] inputs, Field f, Move candidate) {
        int offset = 320/4;
        int i = 0;
        for(int x = candidate.getX()-4; x<=candidate.getX()+4; x++) {
            for(int y = candidate.getY()-4; y<=candidate.getY()+4; y++) {
                if (x == candidate.getX() && y == candidate.getY()) {
                    continue;
                }

//                    System.out.println((x-candidate.getX())+","+(y-candidate.getY())+" i "+i);
                if (x<0 || x>=f.getColumns() || y<0 || y>=f.getRows()) {
                    inputs[i] = 0; // empty
                    inputs[i + offset] = 0; // us
                    inputs[i + 2*offset] = 0; // them
                    inputs[i + 3*offset] = 1; // edge
                } else {
                    int v = f.get(x,y);
                    inputs[i] = (v == 0 ? 1 : 0); // empty
                    inputs[i + offset] = (v == 1 ? 1 : 0); // us
                    inputs[i + 2*offset] = (v == 2 ? 1 : 0); // them
                    inputs[i + 3*offset] = 0; // edge
                }

                i++;
            }
        }

        return net.isDesired(inputs);
    }

    private Move cornerPlay(int x1, int y1, int x2, int y2, int x3, int y3) {
        double r = Math.random();
        if (r <= 0.33) {
            return new Move(x1,y1);
        } else if (r <= 0.66) {
            return new Move(x2,y2);
        } else {
            return new Move(x3,y3);
        }
    }

    private boolean isEmptyRegion(Field f, int x, int y) {
        for(int i=x-2; i<=x+2; i++) {
            for(int j=y-2; j<=y+2; j++) {
                if (f.get(i,j) != 0) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void main(String[] args) {
        BotParser parser = new BotParser(new Main());
        parser.run();
    }
}
