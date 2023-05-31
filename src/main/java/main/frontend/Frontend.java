package main.frontend;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Frontend {

    private static int selectDay(Message message){
        return 0;
    }

    private static int selectYear(Message message){
        return 0;
    }

    private static String selectMonth(Message message){
        return "";
    }

    public static Date selectDate(Message message){
        int day=selectDay(message);
        String month = selectMonth(message);
        int year = selectYear(message);
        return new Date();
    }

    private static List<KeyboardRow> matixToList(String[][] keys){
        List<KeyboardRow> keyboard = new ArrayList<>();
        for(String[] str:keys){
            KeyboardRow keyboardRow = new KeyboardRow();
            for(String word:str) {
                keyboardRow.add(word);
            }
            keyboard.add(keyboardRow);
        }
        return keyboard;
    }

    public static ReplyKeyboardMarkup drawKeyboard(String[][] keys){
        List<KeyboardRow> keyboard = matixToList(keys);
        ReplyKeyboardMarkup replyKeyboardMarkup = new
                ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
