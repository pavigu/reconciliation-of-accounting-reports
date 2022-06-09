import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonthlyReports extends Reports {
    static String year;
    private static ArrayList<String> names;
    static HashMap<String, String[][]> data;
    static Map<String, String[][]> sortedData;
    static String[][] summaryTable;
    static boolean isExistList;

    public static void analyze() {
        printDataForYear(false);
    }

    private static void printDataForYear(boolean print) {
        if (print) {
            System.out.println("Печать сводки месячных отчётов для сравнения");
            for (int i = 0; i < summaryTable.length; i++) {
                System.out.print(year + "-" + getPrefix(i) + (i + 1) + ". ");
                System.out.print("Доходы: " + summaryTable[i][0] + " руб. ");
                System.out.print("Расходы: " + summaryTable[i][1] + " руб.");
                System.out.println();
            }
        }
    }

    static void isExistList() {
        Pattern fileNamePattern = Pattern.compile("[m][12][0-9][0-9][0-9][-][0-9][0-9][.][c][s][v]"); // Pattern: "m2022-05.csv"
        Matcher matcher;
        int numberOfAppropriateReports = 0;
        for (int i = 0; i < filesNames.length; i++) {
            matcher = fileNamePattern.matcher(filesNames[i]);
            if (matcher.matches()) {
                numberOfAppropriateReports += 1;
            }
        }
        if (numberOfAppropriateReports != 0) {
            makeNamesList();
            mergeData();
            System.out.print("Папка содержит месячные отчёты за " + year + " год, их " + numberOfAppropriateReports + ": ");
            System.out.println(sortedData.keySet());
            isExistList = true;
        } else {
            System.out.println("[!] В папке нет месячных отчётов. Добавьте их и попробуйте снова.");
        }
    }

    static void makeNamesList() {
        Pattern fileNamePattern = Pattern.compile("[m][12][0-9][0-9][0-9][-][0-9][0-9][.][c][s][v]"); // Pattern: "m2022-05.csv"
        Matcher matcher;
        names = new ArrayList<>();
        for (int i = 0; i < filesNames.length; i++) {
            matcher = fileNamePattern.matcher(filesNames[i]);
            if (matcher.matches()) {
                names.add(filesNames[i]);
            }
        }
        year = names.get(0).substring(1, 5);
    }

    private static void mergeData() {
        data = new HashMap<String, String[][]>();
        for (int i = 0; i < names.size(); i++) {
            String[][] monthlyReport = makeArrayFromFile(folderPath + names.get(i));
            data.put(names.get(i), monthlyReport);
        }
        sortedData = new TreeMap<>(data);
    }

    static void formSummaryTable(){
        summaryTable = new String[12][2];
        for (int i = 0; i < summaryTable.length; i++) {
            String monthlyReportName = "m" + year + "-" + getPrefix(i) + (i + 1) + ".csv"; // example: m2022-05.csv
            if (data.containsKey(monthlyReportName)) {
                String[][] monthData = data.get(monthlyReportName);
                int monthIncomes = 0;
                int monthExpenses = 0;
                int sumOfMonthExpenses;
                int quantityOfMonthExpenses;
                int sumOfMonthIncomes;
                int quantityOfMonthIncomes;
                for (int j = 1; j < monthData.length; j++) {
                    if (monthData[j][1].equals("расход")) {
                        sumOfMonthExpenses = Integer.parseInt(monthData[j][2].trim());
                        quantityOfMonthExpenses = Integer.parseInt(monthData[j][3].trim());
                        monthExpenses += sumOfMonthExpenses * quantityOfMonthExpenses;
                    } else if (monthData[j][1].equals("доход")) {
                        sumOfMonthIncomes = Integer.parseInt(monthData[j][2].trim());
                        quantityOfMonthIncomes = Integer.parseInt(monthData[j][3].trim());
                        monthIncomes += Integer.parseInt(monthData[j][2].trim()) * Integer.parseInt(monthData[j][3].trim());
                    }
                }
                summaryTable[i][0] = String.valueOf(monthIncomes);
                summaryTable[i][1] = String.valueOf(monthExpenses);
            } else {
                summaryTable[i][0] = null;
                summaryTable[i][1] = null;
            }
        }
    }


    public static void printSummaryTable(){
        System.out.println("МЕСЯЧНЫЕ ОТЧЁТЫ");
        System.out.println("Наличие отчёта по месяцам года:");
        for (int i = 0; i < summaryTable.length; i++) {
            String monthlyReportName = "m" + year + "-" + getPrefix(i) + (i + 1) + ".csv"; // example: m2022-05.csv
            String monthName = year + "-" + getPrefix(i) + (i + 1); // example: 2022-05
            System.out.print(monthName + ". ");
            if (data.containsKey(monthlyReportName)) {
                String[][] monthData = data.get(monthlyReportName);
                int monthIncomes = 0;
                int monthExpenses = 0;
                int sumOfMonthExpenses;
                int quantityOfMonthExpenses;
                int sumOfMonthIncomes;
                int quantityOfMonthIncomes;
                for (int j = 1; j < monthData.length; j++) {
                    if (monthData[j][1].equals("расход")) {
                        sumOfMonthExpenses = Integer.parseInt(monthData[j][2].trim());
                        quantityOfMonthExpenses = Integer.parseInt(monthData[j][3].trim());
                        monthExpenses += sumOfMonthExpenses * quantityOfMonthExpenses;
                    } else if (monthData[j][1].equals("доход")) {
                        sumOfMonthIncomes = Integer.parseInt(monthData[j][2].trim());
                        quantityOfMonthIncomes = Integer.parseInt(monthData[j][3].trim());
                        monthIncomes += Integer.parseInt(monthData[j][2].trim()) * Integer.parseInt(monthData[j][3].trim());
                    }
                }
                System.out.print("Есть отчёт " + monthlyReportName + ". Доходы: " + monthIncomes + " руб. ");
                System.out.print("Расходы: " + monthExpenses + " руб. ");
                summaryTable[i][0] = String.valueOf(monthIncomes);
                summaryTable[i][1] = String.valueOf(monthExpenses);

                int monthProfit = monthIncomes - monthExpenses;
                String monthProfitCaption;
                if (monthProfit < 0) {
                    monthProfitCaption = "Убыток";
                } else {
                    monthProfitCaption = "Прибыль";
                }
                System.out.println(monthProfitCaption + ": " + monthProfit + " руб.");

            } else {
                System.out.println("Нет отчёта");
                summaryTable[i][0] = null;
                summaryTable[i][1] = null;
            }
        }
        printReportAnalysis();
    }

    private static void printReportAnalysis() {
        int monthlyExpenses;
        int monthlyIncomes;
        int monthlyProfit;
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
            if (summaryTable[i][0] == null) {
                monthlyIncomes = 0;
            } else {
                monthlyIncomes = Integer.parseInt(summaryTable[i][0]);
            }
            if (summaryTable[i][1] == null) {
                monthlyExpenses = 0;
            } else {
                monthlyExpenses = Integer.parseInt(summaryTable[i][1]);
            }

            monthlyProfit = monthlyIncomes - monthlyExpenses;
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
}