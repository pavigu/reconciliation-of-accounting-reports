import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        System.out.println("Создайте или откройте папку для отчётов по выбранному году.");
        System.out.println("В папке должен находиться годовой отчёт (формат имени: y2022.csv) и отчёты по месяцам года (формат имени: m2022-01.csv).");

        while (true) {
            System.out.println("Укажите путь к папке c отчётами (формат: p:/temp/) или введите 1, чтобы выйти:");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            if (command.equals("1")) {
                System.out.println("Программа завершена.");
                break;
            } else {
                Reports.folderPath = command;
                System.out.println("Указан путь " + Reports.folderPath);
                Reports.printFolderStatus(false);
                if (MonthlyReports.isExistList && YearlyReport.isExist) {
                    menu();
                }

            }
        }
    }

    private static void menu() {
        while (true) {
            Reports.prepare();
            System.out.println("Введите команду, что нужно сделать c отчётами из папки " + Reports.folderPath + ":\n" +
                    "1 — сверить отчёты\n" +
                    "2 — вывести информацию о месячных отчётах\n" +
                    "3 — вывести информацию о годовом отчёте\n" +
                    "4 — назад");
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                int command = scanner.nextInt();
                if (command == 1) {
                    Reports.compareReports();
                    pause();
                } else if (command == 2) {
                    MonthlyReports.printSummaryTable();
                    pause();
                } else if (command == 3) {
                    System.out.println("ГОДОВОЙ ОТЧЁТ");
                    YearlyReport.printContent(false);
                    YearlyReport.printAnalysis();
                    pause();
                } else if (command == 4) {
                    break;
                } else {
                    System.out.println("Нужно ввести команду из предложенных.");
                }
            } else {
                System.out.println("Нужно ввести целое число.");
            }
        }
    }

    static void pause() {
        while (true) {
            System.out.println("1 — далее");
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                int command = scanner.nextInt();
                if (command == 1) {
                    break;
                } else {
                    System.out.println("Введите 1, чтобы продолжить.");
                }
            } else {
                System.out.println("Введите 1, чтобы продолжить.");
            }
        }
    }
}