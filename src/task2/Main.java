package task2;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);

        // Удаление дубликатов
        List<Integer> uniqueNumbers = numbers.stream().distinct().toList();
        System.out.println("Unique numbers: " + uniqueNumbers);

        // Нахождение 3-го наибольшего числа
        //  первый способ:
        Integer thirdMax = numbers.stream().sorted(Comparator.reverseOrder()).skip(2).findFirst().orElse(null);
        System.out.println("3rd maximum number: " + thirdMax);
        //  второй способ:
        Integer thirdMax2 = numbers.stream().sorted((a, b) -> b - a).skip(2).findFirst().orElse(null);
        System.out.println("3rd maximum number: " + thirdMax2);


        // Нахождение 3-го наибольшего уникального числа
        Integer thirdMaxUnique = numbers.stream().distinct().sorted(Comparator.reverseOrder()).skip(2).findFirst().orElse(null);
        System.out.println("3rd maximum unique number: " + thirdMaxUnique);

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Иван", 30, "Инженер"));
        employees.add(new Employee("Петр", 31, "Экономист"));
        employees.add(new Employee("Алексей", 45, "Инженер"));
        employees.add(new Employee("Сергей", 26, "Экономист"));
        employees.add(new Employee("Николай", 41, "Инженер"));
        employees.add(new Employee("Антон", 34, "Инженер"));
        employees.add(new Employee("Андрей", 37, "Инженер"));

        // Получить список имен 3-х самых старших сотрудников с должностью Инженер:
        List<String> topEngineers = employees.stream().filter(e -> "Инженер".equals(e.getPosition()))
                .sorted(Comparator.comparingInt(Employee::getAge).reversed())
                .limit(3).map(Employee::getName).toList();
        System.out.println("3 engineers with maximum age: " + topEngineers);

        // Посчитать средний возраст сотрудников с должностью Инженер:
        double averageAge = employees.stream().filter(e -> "Инженер".equals(e.getPosition()))
                .mapToInt(Employee::getAge).average().orElse(0);
        System.out.println("engineers average age: " + averageAge);

        // Найти самое длинное слово в списке:
        List<String> words = List.of("велосипед", "гроза", "сапог", "протуберанец", "восход");
        String longestWord = words.stream().reduce("", (a, b) -> a.length() >= b.length() ? a : b);
        System.out.println("the longest word: " + longestWord);

        // Построить хеш-мапу слов и их частоты:
        String inputString = "мяч кнут пряник ветер рука солнце сундук кнут небо мяч погода ветер погода ветер";
        Map<String, Long> wordCountMap = Arrays.stream(inputString.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("the map of word and counts: " + wordCountMap);

        // Отпечатать строки из списка в порядке увеличения длины слова:
        words.stream().sorted(Comparator.comparingInt(String::length)
                .thenComparing(Comparator.naturalOrder())).forEach(System.out::println);

        // Найти самое длинное слово среди всех строк в массиве:
        String[] array = {"велосипед был сломан", "гроза началась рано", "сапог с дыркой", "солнечный протуберанец образовался", "восход был красочным"};
        String longestInArray = Arrays.stream(array).flatMap(str -> Arrays.stream(str.split(" ")))
                .reduce("", (a, b) -> a.length() >= b.length() ? a : b);
        System.out.println("the longest word in array: " + longestInArray);
    }
}
