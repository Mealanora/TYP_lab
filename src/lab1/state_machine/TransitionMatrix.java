package lab1.state_machine;

import lab1.util.Action;
import lab1.util.DeltaCell;
import lab1.util.Symbols;

import java.util.*;

public class TransitionMatrix {

    public static final Map<State, State[]> matrixTransition =
            new HashMap<State, State[]>() {{

                put(State._0, new State[]{State._1, State.ERROR, State._0, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR});
                put(State._1, new State[]{State._1, State._1, State._2, State._3, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR});
                put(State._2, new State[]{State.ERROR, State.ERROR, State._2, State._3, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR});
                put(State._3, new State[]{State._10, State._4, State._3, State.ERROR, State._3, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR});
                put(State._4, new State[]{State.ERROR, State._4, State._11, State.ERROR, State.ERROR, State._11, State._3, State._3, State.ERROR, State._5, State._7, State.HALT});
                put(State._5, new State[]{State.ERROR, State._6, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State.ERROR});
                put(State._6, new State[]{State.ERROR, State._6, State._11, State.ERROR, State.ERROR, State._11, State._3, State._3, State.ERROR, State.ERROR, State._7, State.HALT});
                put(State._7, new State[]{State.ERROR, State._9, State.ERROR, State.ERROR, State.ERROR, State.ERROR, State._8, State.ERROR, State._8, State.ERROR, State.ERROR, State.ERROR});
                put(State._8, new State[]{State.ERROR, State._9, State._11, State.ERROR, State.ERROR, State._11, State._3, State._3, State.ERROR, State.ERROR, State.ERROR, State.HALT});
                put(State._9, new State[]{State.ERROR, State._9, State._11, State.ERROR, State.ERROR, State._11, State._3, State._3, State.ERROR, State.ERROR, State.ERROR, State.HALT});
                put(State._10, new State[]{State._10, State._10, State._11, State.ERROR, State.ERROR, State._11, State._3, State._3, State.ERROR, State.ERROR, State.ERROR, State.HALT});
                put(State._11, new State[]{State.ERROR, State.ERROR, State._11, State.ERROR, State.ERROR, State._11, State._3, State._3, State.ERROR, State.ERROR, State.ERROR, State.HALT});
            }};

    public static int getIndex(char symbol) {
        if (Symbols.letters.contains(symbol)) return 0;
        if (Symbols.digits.contains(symbol)) return 1;
        if (symbol == ' ') return 2;
        if (symbol == '=') return 3;
        if (symbol == '(') return 4;
        if (symbol == ')') return 5;
        if (symbol == '+') return 6;
        if (symbol == '*') return 7;
        if (symbol == '-') return 8;
        if (symbol == '.') return 9;
        if (Symbols.logarithmicSymbol.contains(symbol)) return 10;
        if (Symbols.end.contains(symbol)) return 11;
        return -1;
    }
}
