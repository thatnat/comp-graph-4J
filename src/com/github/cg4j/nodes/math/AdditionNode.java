package com.github.cg4j.nodes.math;

import com.github.cg4j.Eval;
import com.github.cg4j.Tensor;
import com.github.cg4j.nodes.Node;
import com.github.cg4j.optimizers.Optimizer;

public class AdditionNode extends Node {
    public AdditionNode(String name, Node... children) {
        super(children[0].shape, name, children);
    }

    public AdditionNode(Node... children) {
        super(children[0].shape, null, children);
    }

    @Override
    public String getNodeClassName() {
        return "Addition";
    }

    @Override
    protected boolean canAddChildren() {
        return true;
    }

    @Override
    public Tensor evaluate(Eval e) {
        if (children.length == 1) {
            return e.evaluate(children[0]);
        }
        Tensor out = new Tensor(new float[children[0].length], children[0].shape);
        for (Node child : children) {
            Tensor in = e.evaluate(child);
            for (int i = 0; i < out.length; i++) {
                out.setVal(i, out.getVal(i) + in.getVal(i));
            }
        }
        return out;
    }

    @Override
    public void createGradients(Optimizer optimizer, Node parentDelta) {
        for (Node node : children) {
            node.createGradients(optimizer, parentDelta);
        }
    }
}
