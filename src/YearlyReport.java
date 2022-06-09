import java.util.regex.Matcher;
import java.util.regex.Pattern;

class YearlyReport extends Reports {
    private static String name;
    private static String[][] data;
    static String[][] summaryTable;
    static String year;
    static boolean isExist;

    static void isExist() {
        Pattern fileNamePattern = Pattern.compile("[y][12][0-9][0-9][0-9][.][c][s][v]"); // Pattern: "y2022.csv"
        Matcher matcher;
        int numberOfAppropriateReports = 0;
        for (int i = 0; i < filesNames.length; i++) {
            matcher = fileNamePattern.matcher(filesNames[i]);
            if (matcher.matches()) {
                numberOfAppropriateReports += 1;
            }
        }
        if (numberOfAppropriateReports == 0) {
            System.out.println("[!] В папке нет годового отчёта. Добавьте его и попробуйте снова.");
        } else if (numberOfAppropriateReports == 1) {
            getName();
            getYear();
            System.out.println("В папке есть 1 годовой отчёт за " + year + " год: " + name);
            isExist = true;
        } else {
            System.out.println("В папке больше одного годового отчёта. За год может быть только один.");
        }
    }

    static void getName() {
        Pattern fileNamePattern = Pattern.compile("[y][12][0-9][0-9][0-9][.][c][s][v]"); // Pattern: "y2022.csv"
        Matcher matcher;
        for (int i = 0; i < filesNames.length; i++) {
            matcher = fileNamePattern.matcher(filesNames[i]);
            if (matcher.matches()) {
                name = filesNames[i];
            }
        }
    }

    private static String getYear() {
        year = name.substring(1, 5);
        return name;
    }

    public static void analyze() {
        data = makeArrayFromFile(folderPath + name);
        convertArrayFromFileToTable();
    }

    static void printAnalysis() {
        System.out.println("Анализ содержимого годового отчёта " + name + ":");
        int monthlyExpenses;
        int monthlyIncomes;
        int monthlyProfit;
        String monthlyProfitCaption;
        int totalExpenses = 0;
        int totalIncomes = 0;
        int totalProfit = 0;
        String totalProfitCaption;
        int maxMonthlyExpenses = 0;
        int maxMonthlyIncomes = 0;
        int maxMonthlyProfit = 0;
        String monthWithMonthlyExpenses = null;
        String monthWithMonthlyIncomes = null;
        String monthWithMonthlyProfit = null;
        for (int i = 0; i < summaryTable.length; i++) {
            monthlyIncomes = Integer.parseInt(summaryTable[i][0]);
            monthlyExpenses = Integer.parseInt(summaryTable[i][1]);
            monthlyProfit = monthlyIncomes - monthlyExpenses;
            if (monthlyProfit >= 0) {
                monthlyProfitCaption = "Прибыль";
            } else {
                monthlyProfitCaption = "Убыток";
            }
            System.out.printf("%s-%d. Доходы: %d руб. Расходы: %d руб. %s: %d руб.\n", year, i + 1, monthlyIncomes, monthlyExpenses, monthlyProfitCaption, monthlyProfit);
            totalIncomes += monthlyIncomes;
            totalExpenses += monthlyExpenses;
            totalProfit += monthlyProfit;
            if (monthlyExpenses > maxMonthlyExpenses) {
                maxMonthlyExpenses = monthlyExpenses;
                monthWithMonthlyExpenses = getPrefix(i) + (i + 1);
            }
            if (monthlyIncomes > maxMonthlyIncomes) {
                maxMonthlyIncomes = monthlyIncomes;
                monthWithMonthlyIncomes = getPrefix(i) + (i + 1);
            }
            if (monthlyProfit > maxMonthlyProfit) {
                maxMonthlyProfit = monthlyProfit;
                monthWithMonthlyProfit = getPrefix(i) + (i + 1);
            }
        }
        if (totalProfit >= 0) {
            totalProfitCaption = "прибыль";
        } else {
            totalProfitCaption = "убыток";
        }
        System.out.println("Самый доходный месяц: " + year + "-" + monthWithMonthlyIncomes);
        System.out.println("Самый расходный месяц: " + year + "-" + monthWithMonthlyExpenses);
        System.out.println("Самый прибыльный месяц: " + year + "-" + monthWithMonthlyProfit);

        System.out.println("Результаты календарного года по 1 точке проката детских электромобилей:");
        System.out.printf("Всего доходов: %d руб./год. Всего расходов: %d руб./год. Всего %s: %d руб./год\n", totalIncomes, totalExpenses, totalProfitCaption, totalProfit);
    }

    private static void convertArrayFromFileToTable() {
        summaryTable = new String[12][2];
        // проход по строкам с расходами
        for (int i = 1; i < data.length; i = i + 2) {
            int monthNumber = Integer.parseInt(data[i][0].trim()) - 1;
            String monthExpenses = data[i][2].trim();
            summaryTable[monthNumber][1] = monthExpenses;
        }
        // проход по строкам с доходами
        for (int i = 2; i < data.length; i = i + 2) {
            int monthNumber = Integer.parseInt(data[i][0].trim()) - 1;
            String monthIncomes = data[i][2].trim();
            summaryTable[monthNumber][0] = monthIncomes;
        }
    }

    static void printContent(boolean print) {
        if (print) {
            System.out.println("\nПечать содержимого годового отчёта " + name);
            for (int i = 0; i < summaryTable.length; i++) {
                String monthlyExpenses = summaryTable[i][1];
                String monthlyIncomes = summaryTable[i][0];
                System.out.println(year + "-" + (i + 1) + ". Доходы: " + monthlyIncomes + " руб. Расходы: " + monthlyExpenses + " руб.");
            }
        }
    }
}