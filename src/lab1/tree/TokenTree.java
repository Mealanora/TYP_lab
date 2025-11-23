package lab1.tree;

public class TokenTree {
    private String oper;
    private int level;
    private String code;
    private TokenTree left, right;

    public TokenTree(String oper, int level, String code, TokenTree left, TokenTree right) {
        this.oper = oper;
        this.level = level;
        this.code = code;
        this.left = left;
        this.right = right;
    }

    public static void printTree(TokenTree tree, int level) {
        if (tree != null) {
            printTree(tree.right, level + 1);
            for (int i = 0; i < level; i++) System.out.print(" ");
            System.out.println(tree.oper);
            printTree(tree.left, level + 1);
        }
    }

    public String getOper() { return oper; }
    public int getLevel() { return level; }
    public String getCode() { return code; }
    public TokenTree getLeft() { return left; }
    public TokenTree getRight() { return right; }

    public void setLevel(int level) { this.level = level; }
    public void setCode(String code) { this.code = code; }
}
