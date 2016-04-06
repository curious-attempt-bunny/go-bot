package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by home on 4/3/16.
 */
public class Evaluate {
    public static void main(String[] args) throws IOException {
        NeuralNet net = new NeuralNet();

        BufferedReader in = new BufferedReader(new FileReader("data/training.consistent.csv"));
        int correct = 0;
        int wrong = 0;
        int total = 0;
        while(true) {
            String line = in.readLine();
            if (line == null) break;
//            System.out.println(line);
            String[] parts = line.split(",");
            int[] inputs = new int[320];
            for(int i=0; i<inputs.length; i++) {
                inputs[i] = Integer.parseInt(parts[i]);
            }
            if (parts.length != 321) throw new RuntimeException();
            int desired = Integer.parseInt(parts[parts.length-1]);
            boolean actual = net.isDesired(inputs);
//            System.out.println(desired+" vs "+actual);
            if ((actual && desired == 1) || (!actual && desired == 0)) {
                correct++;
            } else {
                wrong++;
            }
            total += 1;
        }
        System.out.println(correct+" out of "+total);
    }
}
