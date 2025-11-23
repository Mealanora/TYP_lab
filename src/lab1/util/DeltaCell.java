package lab1.util;

import lab1.state_machine.State;

public class DeltaCell {
    public State state;
    public Action action;

    public DeltaCell(State s, Action a) {
        this.state = s;
        this.action = a;
    }
}
