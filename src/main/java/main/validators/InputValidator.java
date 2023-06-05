package main.validators;

public class InputValidator {
    public static Integer validateInt(String text)throws NumberFormatException{
        return Integer.parseInt(text);
    }
    public static Float validateFloat(String text)throws NumberFormatException{
        return Float.parseFloat(text);
    }
}
