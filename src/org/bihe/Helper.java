package org.bihe;

import java.io.*;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Helper {

    private static DictionaryTree initializeDT(String path) throws IOException {
        DictionaryTree dt = new DictionaryTree();
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String st;
        while ((st = br.readLine()) != null) {
            dt.insert(st);
        }
        return dt;
    }

    private static void writeDictionaryToFile(String path, DictionaryTree dt) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path, false));
            out.write(dt.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("Welcome :)\nWhat do you want to do? Please enter number of your selection.\n" +
                "1. Insert to the Dictionary\n" +
                "2. Delete from the Dictionary\n" +
                "3. Search in the Dictionary\n" +
                "4. Exit");
    }

    public static void menu() {
        Scanner scanner = new Scanner(System.in);
        DictionaryTree dt = null;
        try {
            dt = initializeDT("dictionary.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        loop:
        while (true) {
            printMenu();
            byte in;
            try {
                in = scanner.nextByte();
                String word;
                switch (in) {
                    case 1:
                        System.out.println("Enter a word without (*, ?, ~): ");
                        scanner.nextLine();
                        word = scanner.nextLine();
                        try{
                            dt.insert(word);
                            System.out.println("Your word inserted successfully!\n----------------------------------");
                        }catch (IllegalArgumentException e) {
                            System.out.println("Please enter a word without (*, ~, ?)!\n----------------------------------");
                        }
                        break;
                    case 2:
                        System.out.println("Enter a word: ");
                        scanner.nextLine();
                        word = scanner.nextLine();
                        try{
                            dt.delete(word);
                            System.out.println("Your word deleted successfully!\n----------------------------------");
                        } catch (NoSuchElementException e) {
                            System.out.println("This word is not in dictionary.\n----------------------------------");
                        }
                        break;
                    case 3:
                        searchMenu(scanner, dt);
                        break;
                    case 4:
                        writeDictionaryToFile("dictionary.txt", dt);
                        break loop;
                    default:
                        System.out.println("Please enter a right number!");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!!");
            }
        }
    }

    private static void printSearchMenu() {
        System.out.println("Please select the search type by enter its number.\n" +
                "1. Enter a word like (L?g) contains just one '?'. Retrieve words where a letter is missing.\n" +
                "2. Enter a word like (~see) contains just one '~'. Retrieve all valid words in the dictionary that differ by at most one letter from the corresponding word.\n" +
                "3. Enter a word like (Dr*) contains just one '*'. Retrieves words that return all valid words that begin with this subword by entering only a few letters of the word.\n" +
                "4. Enter a word like (*rn) contains just one '*'. Retrieve valid words by entering only a few letters from the end of the word.\n" +
                "5. Enter a word like (p*e) contains just one '*'. Retrieves words by entering a few letters from the beginning and the end of the word.\n" +
                "6. Exit");
    }

    private static void searchMenu(Scanner scanner, DictionaryTree dt) {
        while (true) {
            printSearchMenu();
            byte in;
            try {
                in = scanner.nextByte();
                if (in > 6 || in < 1) {
                    System.out.println("Please enter a right number!");
                } else if (in == 6) {
                    break;
                } else {
                    forSearchingCases(in, scanner, dt);
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!!");
                scanner.next();
            }

        }
    }

    private static void forSearchingCases(byte c, Scanner scanner, DictionaryTree dt) {
        System.out.println("Enter word: ");
        scanner.nextLine();
        String word = scanner.nextLine();
        int length = word.length();
        int indexOfStar = word.indexOf("*");
        int lastIndexOfStar = word.lastIndexOf("*");
        if (c == 1 && word.contains("?") && word.indexOf("?") == word.lastIndexOf("?")) {
            System.out.println("Results:\n----------------------------------");
            dt.searchType1(word).sort().printWordsWithDelete();
        } else if (c == 2 && word.charAt(0) == '~' && word.indexOf("~") == word.lastIndexOf("~")) {
            System.out.println("Results:\n----------------------------------");
            dt.searchType2(word).sort().printWordsWithDelete();
        } else if (c == 3 && indexOfStar == length - 1) {
            System.out.println("Results:\n----------------------------------");
            dt.searchType3(word).sort().printWordsWithDelete();
        } else if (c == 4 && indexOfStar == 0 && lastIndexOfStar == 0) {
            System.out.println("Results:\n----------------------------------");
            dt.searchType4(word).sort().printWordsWithDelete();
        } else if (c == 5 && indexOfStar != 0 && indexOfStar != length - 1 && indexOfStar == lastIndexOfStar) {
            System.out.println("Results:\n----------------------------------");
            dt.searchType5(word).sort().printWordsWithDelete();
        } else {
            System.out.println("Please enter right input according to type of search!");
        }
        System.out.println("----------------------------------");

    }
}
