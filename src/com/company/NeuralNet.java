package com.company;
import com.company.net.*;

public class NeuralNet {
    static final double[][][] layers = new double[][][] {
            new double[][] {
                    Unit00.WEIGHTS,
                    Unit01.WEIGHTS,
                    Unit02.WEIGHTS,
                    Unit03.WEIGHTS,
                    Unit04.WEIGHTS,
                    Unit05.WEIGHTS,
                    Unit06.WEIGHTS,
                    Unit07.WEIGHTS,
                    Unit08.WEIGHTS,
                    Unit09.WEIGHTS,
                    Unit010.WEIGHTS,
                    Unit011.WEIGHTS,
                    Unit012.WEIGHTS,
                    Unit013.WEIGHTS,
                    Unit014.WEIGHTS,
                    Unit015.WEIGHTS,
                    Unit016.WEIGHTS,
                    Unit017.WEIGHTS,
                    Unit018.WEIGHTS,
                    Unit019.WEIGHTS,
                    Unit020.WEIGHTS,
                    Unit021.WEIGHTS,
                    Unit022.WEIGHTS,
                    Unit023.WEIGHTS,
                    Unit024.WEIGHTS,
                    Unit025.WEIGHTS,
                    Unit026.WEIGHTS,
                    Unit027.WEIGHTS,
                    Unit028.WEIGHTS,
                    Unit029.WEIGHTS
            },
            new double[][] {
                    Unit10.WEIGHTS,
                    Unit11.WEIGHTS
            }
    };

    public boolean isDesired(int[] inputs) {
        double[] hidden = new double[30];
        for(int i = 0; i<hidden.length; i++) {
            double[] ws = layers[0][i];
            double sum = ws[ws.length-1];
            if (ws.length != inputs.length + 1) throw new RuntimeException(ws.length+" vs "+inputs.length);
            for(int j = 0; j<inputs.length; j++) {
                sum += inputs[j]*ws[j];
            }
            hidden[i] = 1.0/(1.0+Math.exp(-sum));
//            System.out.print(Math.round(hidden[i]));
        }
//        System.out.println();
        double[] output = new double[2];
        for(int i = 0; i<output.length; i++) {
            double[] ws = layers[1][i];
            double sum = ws[ws.length-1];
            if (ws.length != hidden.length + 1) throw new RuntimeException();
            for(int j = 0; j<hidden.length; j++) {
                sum += hidden[j]*ws[j];
            }
            output[i] = 1.0/(1.0+Math.exp(-sum));
        }
        return output[0] <= output[1];
    }
}
