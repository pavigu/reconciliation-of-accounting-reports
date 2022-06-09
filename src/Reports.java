import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Reports {
    static String folderPath;
    static String[] filesNames;

    public static void printFolderStatus(boolean printFilesNames) {
        if (Files.exists(Path.of(folderPath))) {
            filesNames = new File(folderPath).list();
            if (filesNames.length > 0) {
              if (printFilesNames) {
                  System.out.println("Содержимое папки: " + Arrays.toString(filesNames));
              }
                MonthlyReports.isExistList();
                YearlyReport.isExist();
            } else {
                System.out.println("[!] Папка пуста.");
            }
        } else {
            System.out.println("[!] Нет папки с таким адресом.");
        }
    }

    public static void prepare() {
        if (MonthlyReports.isExistList && YearlyReport.isExist) {
            MonthlyReports.analyze();
            YearlyReport.analyze();
            MonthlyReports.formSummaryTable();
        } else {
            if (MonthlyReports.isExistList) {
                System.out.println("Внимание! Загрузите в папку годовой отчёт, чтобы выполнить проверку.");
            } else if (YearlyReport.isExist) {
                System.out.println("Внимание! Загрузите в папку месячные отчёты, чтобы выполнить проверку наличия расхождений.");
            }
        }
    }

    static String[][] makeArrayFromFile(String path) {
        String stringFromCSV = makeLongStringFromCSV(path);
        String[][] tableFromCSV = convertStringToArray(stringFromCSV);
        return tableFromCSV;
    }

    /*
     * Принимает адрес файла вида "P:/temp/2021/m2022-3.csv"
     * Возвращает содержимое csv в виде строки из всех строк таблицы, разделённых символом переноса \n
     * */
    private static String makeLongStringFromCSV(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла.");
            return null;
        }
    }

    /*
     * Принимает CSV-файл в виде строки:
     * "element; element; element\nelement; element; element\nelement; element; element"
     * Возвращает 2D массив:
     * {
     *  {element, element, element},
     *  {element, element, element},
     *  {element, element, element},
     * }
     * */
    private static String[][] convertStringToArray(String csvAsString) {
        String[] stringsArray = csvAsString.split("\\n");
        int numberOfArrayRows = stringsArray.length;
        int numberOfArrayCols = stringsArray[0].split(";").length;
        String[][] tableArray = new String[numberOfArrayRows][numberOfArrayCols];
        for (int i = 0; i < numberOfArrayRows; i++) {
            for (int j = 0; j < numberOfArrayCols; j++) {
                tableArray[i][j] = stringsArray[i].split(";")[j];
                tableArray[i][j] = removeQuotes(tableArray[i][j]);
            }
        }
        return tableArray;
    }

    private static String removeQuotes(String string) {
        string = string.replace("\"", "");
        return string;
    }

    public static void compareReports() {
        System.out.println("СРАВНЕНИЕ ОТЧЁТОВ");
        System.out.println("Поиск расхождений данных по месяцу из месячного отчёта и из годового");
        if (YearlyReport.year.equals(MonthlyReports.year)) {
            System.out.println("Год месячных отчётов (" + MonthlyReports.year + ") и год годового отчёта (" + YearlyReport.year + ") совпадают.");
        } else {
            System.out.println("Внимание! Год месячных отчётов (" + MonthlyReports.year + ") и год годового отчёта (" + YearlyReport.year + ") не совпадают.");
        }
        for (int i = 0; i < YearlyReport.summaryTable.length; i++) {
            System.out.print("МЕСЯЦ " + YearlyReport.year + "-" + MonthlyReports.getPrefix(i) + (i + 1) + ": ");
            String monthlyReportName = "m" + MonthlyReports.year + "-" + MonthlyReports.getPrefix(i) + (i + 1) + ".csv";
            String yearlyReportName = "y" + YearlyReport.year + ".csv";
            String incomesInMonthlyReport = MonthlyReports.summaryTable[i][0];
            String incomesInYearlyReport = YearlyReport.summaryTable[i][0];
            String expensesInMonthlyReport = MonthlyReports.summaryTable[i][1];
            String expensesInYearlyReport = YearlyReport.summaryTable[i][1];
            boolean isDifferenceInIncomes = false;
            boolean isDifferenceInExpenses = false;
            boolean isNoMonthlyReport = false;

            if (incomesInYearlyReport.equals(incomesInMonthlyReport)) {
                isDifferenceInIncomes = false;
            } else if (incomesInMonthlyReport == null && Integer.parseInt(incomesInYearlyReport) == 0) {
                isNoMonthlyReport = true;
            } else {
                isDifferenceInIncomes = true;
            }

            if (expensesInYearlyReport.equals(expensesInMonthlyReport)) {
                isDifferenceInExpenses = false;
            } else if (expensesInMonthlyReport == null && Integer.parseInt(expensesInYearlyReport) == 0) {
                isNoMonthlyReport = true;
            } else {
                isDifferenceInExpenses = true;
            }

            if (isNoMonthlyReport) {
                System.out.println("месячного отчёта нет, но значение по месяцу в годовом отчёте " + yearlyReportName + " указано*");
            } else {
                if (isDifferenceInIncomes && isDifferenceInExpenses) {
                    System.out.println("есть расхождения и между доходами, и между расходами. Проверьте файлы " + monthlyReportName + " и " + yearlyReportName);
                } else if (isDifferenceInIncomes) {
                    System.out.println("по расходам расхождений нет, но есть по доходам. Проверьте файлы " + monthlyReportName + " и " + yearlyReportName);
                } else if (isDifferenceInExpenses) {
                    System.out.println("по доходам расхождений нет, но есть по расходам. Проверьте файлы " + monthlyReportName + " и " + yearlyReportName);
                } else {
                    System.out.println("расхождений в отчётах "+ monthlyReportName + " и " + yearlyReportName + " нет");
                }
            }
        }
        System.out.println("*Сумма месяца в годовом отчёте указана (значение — 0), а отчёта по месяцу нет. " +
                "\nЕсли деятельность ещё не велась в этот месяц, проигнорируйте предупреждение." +
                "\nЕсли велась, добавьте отчёт по месяцу в папку " + folderPath + " и запустите анализ снова.");
    }

    static String getPrefix(int i) {
        String prefix;
        if (i >= 0 && i < 9) {
            prefix = "0";
        } else {
            prefix = "";
        }
        return prefix;
    }
}