package lab1;

import lab1.tree.TokenTree;
import lab1.util.Symbols;

import java.util.*;

public class Generator {

    public String generateTable(List<String> splitExp) {
        List<String> parameterNames = new ArrayList<>();        // список имен которые попадут в таблицу
        for (String s : splitExp) {
            if (!"(".equals(s) && !")".equals(s) && !"*".equals(s) && !"+".equals(s) && !"=".equals(s))
                parameterNames.add(s);
        }

        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (String name : parameterNames) {
            sb.append(String.format(
                    "%2d %7s\t\t%s%n",
                    i++, name,
                    Symbols.letters.contains(name.charAt(0)) ?
                            "Переменная с плавающей точкой" :
                            "Константа с плавающей точкой"));
        }
        return sb.toString();
    }

    public TokenTree generateTree(List<String> rpn) {
        Stack<TokenTree> stack = new Stack<>();         // строим дерево из ОПЗ

        for (String token : rpn) {
            if (Symbols.digits.contains(token.charAt(0)) || Symbols.letters.contains(token.charAt(0))) {            // если число или переменная
                stack.push(new TokenTree(token, 0, "", null, null));
            } else if (Symbols.operation.contains(token.charAt(0)) || Symbols.equal.contains(token.charAt(0))) {
                TokenTree right = stack.pop();
                TokenTree left = stack.pop();
                stack.push(new TokenTree(token, 0, "", left, right));
            }
        }

        TokenTree tree = stack.pop();                   // корень дерева
        int maxLevel = setLevels(tree);
        invertLevels(tree, maxLevel + 1);
        return tree;
    }

    private int setLevels(TokenTree tree) {
        if (tree.getLeft() == null && tree.getRight() == null) return 0;
        int max = Math.max(setLevels(tree.getRight()), setLevels(tree.getLeft()));
        tree.setLevel(max);
        return max;
    }

    private void invertLevels(TokenTree tree, int maxLevel) {
        if (tree.getLeft() == null && tree.getRight() == null) {
            tree.setLevel(0);
            return;
        }
        invertLevels(tree.getRight(), maxLevel);
        tree.setLevel(maxLevel - tree.getLevel());
        invertLevels(tree.getLeft(), maxLevel);
    }

    public void generateUnoptimizedCode(TokenTree tree) {
        if (tree == null) return;

        generateUnoptimizedCode(tree.getRight());
        generateUnoptimizedCode(tree.getLeft());

        if (tree.getLevel() == 0 && Symbols.letters.contains(tree.getOper().charAt(0)))         // лист-переменная
            tree.setCode(tree.getOper());
        else if (tree.getLevel() == 0 && Symbols.digits.contains(tree.getOper().charAt(0)))     // лист-константа
            tree.setCode("=" + tree.getOper());
        else if (tree.getOper().equals("="))                                                    // присваивание
            tree.setCode(String.format("LOAD\t%s\nSTORE\t%s", tree.getRight().getCode(), tree.getLeft().getCode()));
        else if (tree.getOper().equals("+"))
            tree.setCode(String.format("%s\nSTORE\t$%d\nLOAD\t%s\nADD\t\t$%d",
                    tree.getRight().getCode(), tree.getLevel(),
                    tree.getLeft().getCode(), tree.getLevel()));
        else if (tree.getOper().equals("*"))
            tree.setCode(String.format("%s\nSTORE\t$%d\nLOAD\t%s\nMPY\t\t$%d",
                    tree.getRight().getCode(), tree.getLevel(),
                    tree.getLeft().getCode(), tree.getLevel()));
    }

    public static String generateOptimizedCode(String unoptimizedCode) {
        // Разбиваем на строки
        String[] splitCode = unoptimizedCode.split("\n");

        // Преобразуем каждую строку в список токенов
        LinkedList<List<String>> code = new LinkedList<>();
        for (String line : splitCode) {
            line = line.replace("\t", " ").replace("  ", " ");
            List<String> parts = new ArrayList<>(Arrays.asList(line.split(" ")));
            code.add(parts);
        }

        // --- Оптимизация 1: перестановка LOAD <-> ADD ---
        for (int i = 0; i < code.size() - 1; i++) {
            if (code.get(i).get(0).contains("LOAD") &&
                    code.get(i + 1).get(0).contains("ADD")) {

                code.get(i).set(0, code.get(i).get(0).replace("LOAD", "ADD"));
                code.get(i + 1).set(0, code.get(i + 1).get(0).replace("ADD", "LOAD"));
                Collections.swap(code, i, i + 1);
            }
        }

        // --- Оптимизация 2: перестановка LOAD <-> MPY ---
        for (int i = 0; i < code.size() - 1; i++) {
            if (code.get(i).get(0).contains("LOAD") &&
                    code.get(i + 1).get(0).contains("MPY")) {

                code.get(i).set(0, code.get(i).get(0).replace("LOAD", "MPY"));
                code.get(i + 1).set(0, code.get(i + 1).get(0).replace("MPY", "LOAD"));
                Collections.swap(code, i, i + 1);
            }
        }

        // --- Удаление STORE x → LOAD x подряд ---
        int i = 0;
        while (i < code.size() - 1) {
            if (code.get(i).get(0).equals("STORE") &&
                    code.get(i + 1).get(0).equals("LOAD") &&
                    code.get(i).size() > 1 &&
                    code.get(i + 1).size() > 1 &&
                    code.get(i).get(1).equals(code.get(i + 1).get(1))) {

                code.remove(i);
                code.remove(i);
                i--;
            }
            i++;
        }

        // --- Оптимизация цепочки LOAD – STORE – LOAD ---
        i = 0;
        while (i < code.size() - 2) {
            if (code.get(i).get(0).equals("LOAD") &&
                    code.get(i + 1).get(0).equals("STORE") &&
                    code.get(i + 2).get(0).equals("LOAD")) {

                String box = code.get(i).get(1);
                String box1 = code.get(i + 1).get(1);

                code.remove(i);
                code.remove(i);

                for (int j = i; j < code.size(); j++) {
                    if (code.get(j).size() > 1 && code.get(j).get(1).equals(box1)) {
                        code.get(j).set(1, box);
                    }
                }

                i--;
            }
            i++;
        }

        // Собираем строки обратно
        StringBuilder sb = new StringBuilder();
        for (List<String> line : code) {
            sb.append(String.join(" ", line)).append("\n");
        }

        return sb.toString();
    }
}
