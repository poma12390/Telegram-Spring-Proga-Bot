package main.utills;

import java.util.Random;
import java.util.Set;

public class RandomUrlPicker {
    private static final Set<String> helpUrlSet = Set.of(
            "https://rebrainme.com/blog/wp-content/uploads/2020/05/haker.jpg",
            "https://images.freeimages.com/images/large-previews/8cf/help-me-1532175.jpg",
            "https://cdn5.vedomosti.ru/crop/image/2022/8p/1d5c49/original-1rp3.jpg?height=934&width=1660");
    public static String pickRandomHelpUrl() {
        // Проверка на пустой набор
        if (helpUrlSet.isEmpty()) {
            return null;
        }

        // Преобразование набора в массив
        String[] urlArray = helpUrlSet.toArray(new String[0]);

        // Генерация случайного индекса
        Random random = new Random();
        int randomIndex = random.nextInt(urlArray.length);

        // Получение URL по случайному индексу
        return urlArray[randomIndex];
    }
}
