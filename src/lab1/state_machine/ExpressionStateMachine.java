package lab1.state_machine;

import lab1.util.*;
import java.util.*;

public class ExpressionStateMachine {

    private State currentState = State._0;

    private final Stack<Character> stack = new Stack<>();        // стек скобок
    private final Stack<Character> stackOp = new Stack<>();      // стек операторов
    private final List<String> rpn = new ArrayList<>();          // выходная последовательность
    private final StringBuilder element = new StringBuilder();   // текущее число или имя

    private final List<Action> actions = new ArrayList<>();
    private final Map<State, List<DeltaCell>> matrix;

    public ExpressionStateMachine() {

        // ----------- ОПРЕДЕЛЕНИЕ ДЕЙСТВИЙ -----------

        // (1) открывающая скобка '('
        actions.add(el -> {
            stackOp.push(el);
            stack.push(el);
        });

        // (2) закрывающая скобка ')'
        actions.add(el -> {
            if (stack.isEmpty()) {
                currentState = State.ERROR;
                return;
            }
            if (element.length() > 0) {
                rpn.add(element.toString());
                element.setLength(0);
            }
            stack.pop();
            while (!stackOp.isEmpty() && stackOp.peek() != '(') {
                rpn.add(stackOp.pop().toString());
            }
            if (!stackOp.isEmpty()) stackOp.pop();
        });

        // (3) операторы + * =
        actions.add(el -> {
            if (element.length() > 0) {
                rpn.add(element.toString());
                element.setLength(0);
            }

            if (stackOp.isEmpty()) {
                stackOp.push(el);
            } else if (getPriority(stackOp.peek()) >= getPriority(el)) {
                while (!stackOp.isEmpty() && getPriority(stackOp.peek()) >= getPriority(el)) {
                    rpn.add(stackOp.pop().toString());
                }
                stackOp.push(el);
            } else {
                stackOp.push(el);
            }
        });

        // (4) буквы/цифры/точка/E/e
        actions.add(el -> element.append(el));

        // ----------- СОЗДАНИЕ МАТРИЦЫ ДЕЙСТВИЙ -----------

        Action[][] matrixAction = new Action[12][12];
        for (int i = 0; i < 12; i++)
            for (int j = 0; j < 12; j++)
                matrixAction[i][j] = null;

        // '('
        matrixAction[3][4] = actions.get(0);

        // ')'
        int[] bracketStates = {4, 6, 9, 10, 11};
        for (int i : bracketStates) matrixAction[i][5] = actions.get(1);

        // + * =
        int[] opStates = {1, 2, 4, 6, 9, 10, 11};
        for (int i : opStates) matrixAction[i][3] = actions.get(2);
        for (int i : opStates) matrixAction[i][6] = actions.get(2);
        for (int i : opStates) matrixAction[i][7] = actions.get(2);

        // буквы/цифры/точка/e/E
        int[][] letterPairs = {
                {0, 0}, {0, 1}, {3, 0}, {3, 1}, {4, 1}, {6, 1},
                {7, 6}, {7, 8}, {4, 9}, {6, 10}
        };

        for (int[] pair : letterPairs) {
            matrixAction[pair[0]][pair[1]] = actions.get(3);
        }

        matrixAction[4][10] = actions.get(3); // state _4, symbol e/E
        matrixAction[7][1]  = actions.get(3); // state _7, digit

        this.matrix = createMatrix(matrixAction);
    }

    // ---------------- ОСНОВНЫЕ МЕТОДЫ ----------------

    private Map<State, List<DeltaCell>> createMatrix(Action[][] matrixAction) {
        Map<State, List<DeltaCell>> delta = new HashMap<>();

        State[] values = State.values();
        for (int i = 0; i < 12; i++) {
            List<DeltaCell> row = new ArrayList<>();
            for (int j = 0; j < 12; j++) {
                State next = TransitionMatrix.matrixTransition.get(values[i])[j];
                row.add(new DeltaCell(next, matrixAction[i][j]));
            }
            delta.put(values[i], row);
        }

        return delta;
    }

    private DeltaCell getNextDeltaCell(char symbol) {
        int index = TransitionMatrix.getIndex(symbol);
        if (index < 0) return new DeltaCell(State.ERROR, null);
        return matrix.get(currentState).get(index);
    }

    public State moveNextState(char symbol) {
        DeltaCell cell = getNextDeltaCell(symbol);

        if (cell.action != null)
            cell.action.action(symbol);

        currentState = cell.state;

        if (currentState == State.HALT) {
            if (element.length() > 0) {
                rpn.add(element.toString());
                element.setLength(0);
            }
            while (!stackOp.isEmpty()) {
                rpn.add(stackOp.pop().toString());
            }
        }

        return currentState;
    }

    public List<String> getRPN(String expression) {
        String haltExpression = expression + Symbols.end.get(0);

        for (char el : haltExpression.toCharArray()) {
            moveNextState(el);
            if (currentState == State.ERROR) break;
        }

        if (currentState != State.HALT || !stack.isEmpty())
            throw new IllegalArgumentException("Синтаксическая ошибка в выражении.");

        return rpn;
    }

    public static int getPriority(char symbol) {
        switch (symbol) {
            case '=': return 0;
            case '(':
            case ')': return 1;
            case '+': return 2;
            case '*': return 3;
            default:
                throw new IllegalArgumentException("Недопустимый символ: " + symbol);
        }
    }

    public State getCurrentState() { return currentState; }

    public Stack<Character> getStack() { return stack; }
}
