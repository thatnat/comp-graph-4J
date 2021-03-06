package com.nathanwood1.cg4j.nodes.tensor;

import com.nathanwood1.cg4j.Eval;
import com.nathanwood1.cg4j.Tensor;
import com.nathanwood1.cg4j.exception.IllegalShapeException;
import com.nathanwood1.cg4j.nodes.Node;
import com.nathanwood1.cg4j.nodes.io.VariableNode;

import java.util.Arrays;
import java.util.HashMap;

public class ReshapeNode extends Node {
    public ReshapeNode(int[] shape, String name, Node child) {
        super(shape, name, child);
    }

    public ReshapeNode(int[] shape, Node child) {
        super(shape, null, child);
    }

    @Override
    protected String getNodeClassName() {
        return "ReshapeNode";
    }

    @Override
    public Tensor evaluate(Eval e) {
        Tensor in = e.evaluate(children[0]);

        int[] newShape = new int[shape.length];
        int unknownIndex = -1;
        int unknownLength = in.length;
        for (int i = 0; i < shape.length; i++) {
            if (shape[i] == -1) {
                if (unknownIndex != -1) {
                    throw new IllegalShapeException("Cannot reshape multiple unknowns ("
                                                            + Arrays.toString(shape)
                                                            + ")"
                    );
                }
                unknownIndex = i;
            } else {
                newShape[i] = shape[i];
                unknownLength /= shape[i];
            }
        }
        newShape[unknownIndex] = unknownLength;
        return new Tensor(in.getVals(), newShape);
    }

    @Override
    public void createGradients(HashMap<VariableNode, Node> deltas, Node parentDelta) {
        children[0].createGradients(deltas, new ReshapeNode(children[0].shape, parentDelta));
    }
}
