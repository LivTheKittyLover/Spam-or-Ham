import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class MainClass {

    public static void main(String[] args) {
        File file = new File("/Users/livtarsillo/Desktop/Project/spam_or_not_spam.csv");
        List<Integer> labels = new ArrayList<>();
        List<String> smsMessages = new ArrayList<>();
        List<String> testEmails = new ArrayList<>();
        List<Integer> testLabels = new ArrayList<>();

        try {
            FileReader reader = new FileReader(file);
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(reader);

            for (CSVRecord csvRecord : csvParser) {
                try {
                    int label = Integer.parseInt(csvRecord.get("label"));
                    String email = csvRecord.get("email");
                    labels.add(label);
                    smsMessages.add(email);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing label as an integer for record: " + csvRecord);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SpamClassifier naiveBayes = new SpamClassifier();
        naiveBayes.train(labels, smsMessages);
        naiveBayes.printStatistics(labels);
        naiveBayes.printTopWords();
        naiveBayes.printTopPhrases();

        System.out.println("-----------Please Wait For Data...Processing-----------");
        File testFile = new File("/Users/livtarsillo/Desktop/Project/test_set.csv");
        int numTestSpam = 0;
        int numTestHam = 0;

        try {
            FileReader testReader = new FileReader(testFile);
            CSVParser testCsvParser = CSVFormat.DEFAULT.withHeader().parse(testReader);

            for (CSVRecord csvRecord : testCsvParser) {
                try {
                    String email = csvRecord.get("email");
                    String result = naiveBayes.classify(email);
                    if (result.equals("Spam")) {
                        numTestSpam++;
                    } else {
                        numTestHam++;
                    }
                    testEmails.add(email);
                    testLabels.add(result.equals("Spam") ? 1 : 0);
                } catch (Exception e) {
                    System.err.println("Error processing email for record: " + csvRecord);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        double percentageTestSpam = (double) numTestSpam / (numTestSpam + numTestHam) * 100;
        double percentageTestHam = (double) numTestHam / (numTestSpam + numTestHam) * 100;
        System.out.println("This is for a test set of Ham and Spam:");
        System.out.println("\nTest Set Percentage of Ham: " + Math.round(percentageTestHam) + "%");
        System.out.println("Test Set Percentage of Spam: " + Math.round(percentageTestSpam) + "%");
        double accuracy = calculateAccuracy(numTestSpam, numTestHam);
        System.out.println("Test Set Accuracy: " + Math.round(100 - percentageTestSpam + percentageTestHam) + "%");
        naiveBayes.calculateEuclideanDistance(testEmails, testLabels);

        naiveBayes.createPredictionsFile(testEmails);
    }

    private static double calculateAccuracy(int numTestSpam, int numTestHam) {
        int totalTest = numTestSpam + numTestHam;
        int correctPredictions = Math.min(numTestSpam, numTestHam);
        return (double) correctPredictions / totalTest * 100;
    }
}

