package lab1.util;

import java.util.*;

public class Symbols {
    public static final List<Character> operation = Arrays.asList('+', '*');
    public static final List<Character> logarithmicSymbol = Arrays.asList('e', 'E');
    public static final List<Character> equal = Collections.singletonList('=');
    public static final List<Character> end = Collections.singletonList('Ɵ');
    public static final List<Character> digits = new ArrayList<>();
    public static final List<Character> letters = new ArrayList<>();

    static {
        for (char c = '0'; c <= '9'; c++) digits.add(c);
        letters.add('_');
        for (char c = 'a'; c <= 'z'; c++) letters.add(c);
        for (char c = 'A'; c <= 'Z'; c++) letters.add(c);
    }
}
