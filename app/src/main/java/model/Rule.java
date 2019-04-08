package model;

import enums.Condition;
import enums.Operation;

public class Rule {
    public int id;
    public Condition condition;
    public Operation operation;

    public Rule(int id, Condition condition, Operation operation) {
        this.id = id;
        this.condition = condition;
        this.operation = operation;
    }

    public Rule() {
    }
}
