package Model.NeuralNetwork;

/*
 * info:
 *   this class is intended to create a feed forward neural network
 *   the hyper parameter for the net are configured bia a builder object
 *
 *   at the time , a momentum is not being used , a possible upgrade would be
 *   the implementation of Adam update algorithm using a variable momentum
 */


import org.nd4j.linalg.api.ndarray.INDArray;
import java.util.List;

import static java.lang.Math.sqrt;

public class Network{

    private double learninRate;
    private double momentum;
    private double weightDecay;
    private Activation.ACTIVATION activation;
    private Cost.COST cost;
    private List<Layer> layerList;
    private INDArray lastActivation;
    private int epoch;
    private Cost costFunction;
    private double desireError;

    public Network(double learninRate, double momentum, double weightDecay, Activation.ACTIVATION activation, Cost.COST cost, List<Layer> layerList, int epoch) {
        if(learninRate < 0 || momentum < 0 || weightDecay < 0 || activation == null || layerList == null || epoch < 0)
            throw new IllegalArgumentException("the parameters where wrong");
        this.learninRate = learninRate;
        this.momentum = momentum;
        this.weightDecay = weightDecay;
        this.activation = activation;
        this.cost = cost;
        this.layerList = layerList;
        lastActivation = null;
        this.epoch = epoch;
        costFunction = new Cost(cost);
        desireError = sqrt(2) / 2;// magic
    }


    public double getLearninRate() {
        return learninRate;
    }

    public double getMomentum() {
        return momentum;
    }

    public double getWeightDecay() {
        return weightDecay;
    }

    public Activation.ACTIVATION getActivation() {
        return activation;
    }

    public Cost.COST getCost() {
        return cost;
    }

    public INDArray predict(INDArray input){
        if(input.columns() != layerList.get(0).getInput())
            throw  new IllegalArgumentException(" the input does not match the number of input neurons");

        INDArray activation = layerList.get(0).propagate(input);

        for (int i = 1; i <layerList.size() ; i++) {
            activation =  layerList.get(i).propagate(activation);
        }

        lastActivation = activation;

        return activation;
    }

    public double calculateCost (INDArray lastActivation, INDArray output){
        return costFunction.getCost(lastActivation, output);
    }

    public void train(INDArray input ,INDArray output){

        if(input == null || input.columns() != layerList.get(0).getInput())
            throw  new RuntimeException("the network hasn't been used yet");
        boolean converged = false;
        for (int current = 0; current < epoch && !converged; current++) {

            INDArray error  = output.add(predict(input).mul(-1));
            for (int i = layerList.size()-1; i >= 0 ; i--) {
                error = layerList.get(i).backPropagate(error);
            }

            if (calculateCost(lastActivation, output) <= desireError) {
                converged = true;
            }

        }


        System.out.println("propagation complete");

        System.out.println(" the error for this network is " + calculateCost(lastActivation,output));
    }

}

