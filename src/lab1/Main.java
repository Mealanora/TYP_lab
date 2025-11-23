package lab1;

import lab1.state_machine.ExpressionStateMachine;
import lab1.tree.TokenTree;
import lab1.util.FileHelper;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String expression = FileHelper.read();
            ExpressionStateMachine sm = new ExpressionStateMachine();
            List<String> rpn = sm.getRPN(expression);

            Generator generator = new Generator();
            String table = generator.generateTable(rpn);
            TokenTree tree = generator.generateTree(rpn);

            generator.generateUnoptimizedCode(tree);

            String unoptimized = tree.getCode();
            String optimized = generator.generateOptimizedCode(unoptimized);
            FileHelper.write(table, unoptimized, optimized);

            System.out.println("Готово! Результат записан в output.txt");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при анализе выражения: " + e.getMessage());
        }
    }
}
