package com.nathanwood1.cg4j.nodes.io;

import com.nathanwood1.cg4j.Eval;
import com.nathanwood1.cg4j.Tensor;
import com.nathanwood1.cg4j.nodes.Node;
import com.nathanwood1.cg4j.nodes.math.AdditionNode;
import com.nathanwood1.cg4j.optimizers.Optimizer;

import java.util.HashMap;

/**
 * A variable node creates a value that can be modified to minimize/maximize a function
 */
public class VariableNode extends Node {
    public final Tensor val;
    private boolean gradientCreated = false;

    public VariableNode(Tensor val) {
        super(val.shape, null);
        this.val = val;
    }

    public VariableNode(Tensor val, String name) {
        super(val.shape, name);
        this.val = val;
    }

    @Override
    protected String getNodeClassName() {
        return "VariableNode";
    }

    /**
     * Use {@code Eval#evaluate(Node)}
     *
     * @see Eval#evaluate(Node)
     */
    @Override
    public Tensor evaluate(Eval e) {
        return val;
    }

    /**
     * Creates the gradients.
     * @param deltas The deltas of all variables.
     * @param parentDelta Last node's delta.
     */
    @Override
    public void createGradients(HashMap<VariableNode, Node> deltas, Node parentDelta) {
        if (gradientCreated) {
            ((VariableDeltaNode) deltas.get(this)).addChild(parentDelta);
        } else {
            deltas.put(this, new VariableDeltaNode(parentDelta));
            gradientCreated = true;
        }
    }

    private class VariableDeltaNode extends AdditionNode {
        public VariableDeltaNode(Node... children) {
            super(children);
        }

        public void addChild(Node child) {
            Node[] childrenU = new Node[children.length + 1];
            System.arraycopy(children, 0, childrenU, 0, children.length);
            childrenU[children.length] = child;
            this.children = childrenU;
        }

        @Override
        protected String getNodeClassName() {
            return "VariableDeltaNode";
        }

    }

}
