package lab1;

import lab1.tree.TokenTree;
import lab1.util.Symbols;

import java.util.*;

public class Generator {

    public String generateTable(List<String> splitExp) {
        List<String> parameterNames = new ArrayList<>();
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
        Stack<TokenTree> stack = new Stack<>();

        for (String token : rpn) {
            if (Symbols.digits.contains(token.charAt(0)) || Symbols.letters.contains(token.charAt(0))) {
                stack.push(new TokenTree(token, 0, "", null, null));
            } else if (Symbols.operation.contains(token.charAt(0)) || Symbols.equal.contains(token.charAt(0))) {
                TokenTree right = stack.pop();
                TokenTree left = stack.pop();
                stack.push(new TokenTree(token, 0, "", left, right));
            }
        }

        TokenTree tree = stack.pop();
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

        if (tree.getLevel() == 0 && Symbols.letters.contains(tree.getOper().charAt(0)))
            tree.setCode(tree.getOper());
        else if (tree.getLevel() == 0 && Symbols.digits.contains(tree.getOper().charAt(0)))
            tree.setCode("=" + tree.getOper());
        else if (tree.getOper().equals("="))
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

    public String generateOptimizedCode(String unoptimized) {
        List<String> lines = new ArrayList<>(Arrays.asList(unoptimized.split("\n")));
        List<List<String>> code = new ArrayList<>();

        for (String line : lines)
            code.add(new ArrayList<>(Arrays.asList(line.trim().split("\\s+"))));

        for (int i = 0; i < code.size() - 1; i++) {
            if (code.get(i).get(0).equals("STORE") &&
                    code.get(i + 1).get(0).equals("LOAD") &&
                    code.get(i).get(1).equals(code.get(i + 1).get(1))) {
                code.remove(i);
                code.remove(i);
                i--;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (List<String> line : code) sb.append(String.join("\t", line)).append("\n");
        return sb.toString();
    }
}
