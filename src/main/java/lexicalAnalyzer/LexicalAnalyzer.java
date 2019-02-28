package lexicalAnalyzer;

import fileManager.FileManager;
import token.Category;
import token.Token;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    private FileManager fileManager = null;
    private String inputLine = null;
    private int index = 0;

    public LexicalAnalyzer(String file) {
        this.fileManager = new FileManager(file);
        this.inputLine = reviseInputLine(fileManager.nextLine());
        System.out.printf("%4d  %s\n", fileManager.currentLineIndex(), inputLine);
    }

    public Token nextToken(){
        Token token = null;
        if (index < inputLine.length()) {

            token = findToken(inputLine, index);

            if (token != null) {

                int endTokenPosition = token.getColumn() - 1 + token.getLexicalValue().length();
                if (endTokenPosition > inputLine.length()) {
                    inputLine = reviseInputLine(fileManager.nextLine());
                    index = 0;
                    System.out.printf("%4d  %s\n", fileManager.currentLineIndex(), inputLine);
                    return nextToken();
                } else {
                    index = endTokenPosition;
                }
            } else {
                System.err.println("Token desconhecido!");
            }
        } else {
            inputLine = reviseInputLine(fileManager.nextLine());
            index = 0;
            System.out.printf("%4d  %s\n", fileManager.currentLineIndex(), inputLine);
            return nextToken();
        }

        return token;
    }

    public Token findToken(String line, int index){

        String letra = "[A-Za-z]";
        String digito = "[0-9]";
        String simbolo = "\\s|!|#|\\$|%|&|\\”|\\(|\\)|\\*|\\+|\\,|\\-|\\.|\\/|:|;|<|=|>|\\?|@|[|]|\\^|_|\\‘|\\{|\\||}|\\∼";

        Map<Pattern, Category> pattern = new LinkedHashMap<Pattern, Category>();

        pattern.put(Pattern.compile(digito+"+"),
                Category.CON_INT);
        pattern.put(Pattern.compile("\\["+digito+"(,"+digito+"+)*\\]"),
                Category.CON_INT_CAD);
        pattern.put(Pattern.compile(digito+"+\\."+digito+"+"),
                Category.CON_FLO);
        pattern.put(Pattern.compile("\\["+digito+"+\\."+digito+"+(,"+digito+"+\\."+digito+"+)*\\]"),
                Category.CON_FLO_CAD);
        pattern.put(Pattern.compile("true|false"),
                Category.CON_BOO);
        pattern.put(Pattern.compile("\\[true|false(,(true|false))*\\]"),
                Category.CON_BOO_CAD);
        pattern.put(Pattern.compile("'("+ letra + "|" + digito + "|" + simbolo + ")'"),
                Category.CON_CHA);
        pattern.put(Pattern.compile("\\['("+ letra + "|" + digito + "|" + simbolo + ")'"+
                        "(,'("+ letra + "|" + digito + "|" + simbolo + ")')*\\]"),
                Category.CON_CHA_CAD);
        pattern.put(Pattern.compile("\"("+ letra + "|" + digito + "|" + simbolo + ")*\""),
                Category.CON_STR);
        pattern.put(Pattern.compile("\\[\"("+ letra + "|" + digito + "|" + simbolo + ")*\"" +
                        "(,\"("+ letra + "|" + digito + "|" + simbolo + ")*\")*\\]"),
                Category.CON_STR_CAD);

        pattern.put(Pattern.compile("main"), Category.MAIN);
        pattern.put(Pattern.compile("void"), Category.VOID);
        pattern.put(Pattern.compile("int"), Category.TIP_INT);
        pattern.put(Pattern.compile("float"), Category.TIP_FLO);
        pattern.put(Pattern.compile("char"), Category.TIP_CHA);
        pattern.put(Pattern.compile("boolean"), Category.TIP_BOO);
        pattern.put(Pattern.compile("string"), Category.TIP_STR);
        pattern.put(Pattern.compile("return"), Category.RETURN);
        pattern.put(Pattern.compile("if"), Category.IF);
        pattern.put(Pattern.compile("else"), Category.ELSE);
        pattern.put(Pattern.compile("for"), Category.FOR);
        pattern.put(Pattern.compile("in"), Category.IN);
        pattern.put(Pattern.compile("to"), Category.TO);
        pattern.put(Pattern.compile("step"), Category.STEP);
        pattern.put(Pattern.compile("while"), Category.WHILE);
        pattern.put(Pattern.compile("var"), Category.VAR);
        pattern.put(Pattern.compile("read"), Category.INPUT);
        pattern.put(Pattern.compile("print"), Category.OUTPUT);
        pattern.put(Pattern.compile("fun"), Category.FUN);
        pattern.put(Pattern.compile("continue"), Category.CONTINUE);
        pattern.put(Pattern.compile("break"), Category.BREAK);
        pattern.put(Pattern.compile("EOF"), Category.EOF);
        pattern.put(Pattern.compile("\\)"), Category.FEC_PAR);
        pattern.put(Pattern.compile("\\("), Category.ABR_PAR);
        pattern.put(Pattern.compile("\\}"), Category.FEC_CHA);
        pattern.put(Pattern.compile("\\{"), Category.ABR_CHA);
        pattern.put(Pattern.compile("\\["), Category.ABR_COL);
        pattern.put(Pattern.compile("\\]"), Category.FEC_COL);
        pattern.put(Pattern.compile("\\+\\+"), Category.OPE_CON);
        pattern.put(Pattern.compile("%%"), Category.OPE_FOR_DEC);
        pattern.put(Pattern.compile("=="), Category.OPE_IGU);
        pattern.put(Pattern.compile("!="), Category.OPE_DIF);
        pattern.put(Pattern.compile("<="), Category.OPE_MEN_IGU);
        pattern.put(Pattern.compile(">="), Category.OPE_MAI_IGU);
        pattern.put(Pattern.compile("<"), Category.OPE_MEN);
        pattern.put(Pattern.compile(">"), Category.OPE_MAI);
        pattern.put(Pattern.compile("\\|\\|"), Category.OPE_OU);
        pattern.put(Pattern.compile("&&"), Category.OPE_E);
        pattern.put(Pattern.compile("\\+"), Category.OPE_ADI);
        pattern.put(Pattern.compile("-"), Category.OPE_SUB);
        pattern.put(Pattern.compile("\\*"), Category.OPE_MUL);
        pattern.put(Pattern.compile("\\/"), Category.OPE_DIV);
        pattern.put(Pattern.compile("!"), Category.OPE_NEG);
        pattern.put(Pattern.compile(","), Category.VIRGULA);
        pattern.put(Pattern.compile(";"), Category.PON_VIR);
        pattern.put(Pattern.compile("="), Category.ATRIBUICAO);
        pattern.put(Pattern.compile("%"), Category.OPE_FOR_CAM);

        pattern.put(Pattern.compile(letra + "(" + letra + "|" + digito +")*"),
                Category.IDENTIFICADOR);

        Token token = null;

        //Remove spaces at the begin of line, and calculates how much spaces were removed
        String noSpacesAtBeginLine = line.substring(index).replaceFirst("\\s*", "");
        int removedSpacesAtBegin = line.substring(index).length() - noSpacesAtBeginLine.length();

        for (Map.Entry<Pattern, Category> element : pattern.entrySet()) {

            Matcher matcher = element.getKey().matcher(noSpacesAtBeginLine);

            if(matcher.find()){
                if(matcher.start() == 0) {
                    token = new Token(element.getValue(), fileManager.currentLineIndex(), index + removedSpacesAtBegin + matcher.start() + 1, matcher.group());
                    return token;
                }
            }
        }

        return token;
    }

    private String reviseInputLine(String line){
        return line.replaceAll("//.*", "").replaceAll("\\s*$", "");
    }
}
