import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.List;

public class SpamClassifier {

    private Map<String, Integer> spamWordFrequencies;
    private Map<String, Integer> hamWordFrequencies;
    private Map<String, Integer> spamPhraseFrequencies;
    private Map<String, Integer> hamPhraseFrequencies;
    private static final String PREDICTIONS_FILE_PATH = "/Users/livtarsillo/Desktop/Project/predictions.txt";

    public SpamClassifier() {
        spamWordFrequencies = new HashMap<>();
        hamWordFrequencies = new HashMap<>();
        spamPhraseFrequencies = new HashMap<>();
        hamPhraseFrequencies = new HashMap<>();
    }

    public void train(List<Integer> labels, List<String> smsMessages) {
        for (int i = 0; i < labels.size(); i++) {
            int label = labels.get(i);
            String[] words = smsMessages.get(i).toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split(" ");

            if (label == 1) {
                updateWordFrequencies(words, spamWordFrequencies);
                updatePhraseFrequencies(words, spamPhraseFrequencies);
            } else {
                updateWordFrequencies(words, hamWordFrequencies);
                updatePhraseFrequencies(words, hamPhraseFrequencies);
            }
        }
    }

    private void updateWordFrequencies(String[] words, Map<String, Integer> wordFrequencies) {
        for (String word : words) {
            int count = wordFrequencies.getOrDefault(word, 0);
            wordFrequencies.put(word, count + 1);
        }
    }

    private void updatePhraseFrequencies(String[] words, Map<String, Integer> phraseFrequencies) {
        for (int i = 0; i < words.length - 2; i++) {
            String phrase = words[i] + " " + words[i + 1] + " " + words[i + 2];
            if (!phrase.matches(".*\\d.*") && !phrase.trim().isEmpty()) {
                int count = phraseFrequencies.getOrDefault(phrase, 0);
                phraseFrequencies.put(phrase, count + 1);
            }
        }
    }

    public void printTopPhrases() {
        System.out.println("\nTop 3 three-word phrases in Spam emails:");
        printTopPhrasesHelper(spamPhraseFrequencies);

        System.out.println("\nTop 3 three-word phrases in Ham emails:");
        printTopPhrasesHelper(hamPhraseFrequencies);
    }

    private void printTopPhrasesHelper(Map<String, Integer> phraseFrequencies) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(phraseFrequencies.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (int i = 0; i < Math.min(3, entries.size()); i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            System.out.println(entry.getKey() + ": " + entry.getValue() + " times");
        }
    }

    public void printTopWords() {
        printTopWordsHelper(spamWordFrequencies, "Spam");
        printTopWordsHelper(hamWordFrequencies, "Ham");

        printTopUniqueWordsHelper(spamWordFrequencies, hamWordFrequencies, "Spam");
        printTopUniqueWordsHelper(hamWordFrequencies, spamWordFrequencies, "Ham");
    }

    private void printTopWordsHelper(Map<String, Integer> wordFrequencies, String category) {
        System.out.println("\nTop 3 words in " + category + " emails:");

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordFrequencies.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (int i = 0; i < Math.min(3, entries.size()); i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            System.out.println(entry.getKey() + ": " + entry.getValue() + " times");
        }
    }

    private void printTopUniqueWordsHelper(Map<String, Integer> sourceWordFrequencies,
                                           Map<String, Integer> otherWordFrequencies, String category) {
        System.out.println("\nTop 3 words specific to " + category + " emails:");

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(sourceWordFrequencies.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : entries) {
            if (!otherWordFrequencies.containsKey(entry.getKey())) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " times");
                count++;
            }

            if (count == 3) {
                break;
            }
        }
    }

    private void printStatsHelper(Collection<Integer> values) {
        List<Integer> valueList = new ArrayList<>(values);
        Collections.sort(valueList);

        int size = valueList.size();
        double mean = calculateMean(valueList);
        double median = calculateMedian(valueList);
        int min = valueList.get(0);
        int max = valueList.get(size - 1);
        double stdDev = calculateStandardDeviation(valueList, mean);

        System.out.println("Min: " + min);
        System.out.println("Max: " + max);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + Math.round(mean));
        System.out.println("Standard Deviation: " + Math.round(stdDev));
    }

    private double calculateMean(List<Integer> values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return (double) sum / values.size();
    }

    private double calculateMedian(List<Integer> values) {
        int size = values.size();
        if (size % 2 == 0) {
            int mid1 = values.get(size / 2 - 1);
            int mid2 = values.get(size / 2);
            return (double) (mid1 + mid2) / 2;
        } else {
            return (double) values.get(size / 2);
        }
    }

    private double calculateStandardDeviation(List<Integer> values, double mean) {
        double sumSquaredDiffs = 0;
        for (int value : values) {
            double diff = value - mean;
            sumSquaredDiffs += diff * diff;
        }
        return Math.sqrt(sumSquaredDiffs / values.size());
    }

    public void printStatistics(List<Integer> labels) {
        int numSpam = 0;
        int numHam = 0;

        for (int label : labels) {
            if (label == 1) {
                numSpam++;
            } else {
                numHam++;
            }
        }

        double percentageSpam = (double) numSpam / labels.size() * 100;
        double percentageHam = (double) numHam / labels.size() * 100;

        System.out.println("\nNumber of Spam Emails: " + numSpam);
        System.out.println("Number of Ham Emails: " + numHam);
        System.out.println("Percentage of Spam: " + Math.round(percentageSpam) + "%");
        System.out.println("Percentage of Ham: " + Math.round(percentageHam) + "%");

        double averageWordSizeSpam = calculateAverageWordSize(spamWordFrequencies);
        double averageWordSizeHam = calculateAverageWordSize(hamWordFrequencies);

        System.out.println("\nAverage Word Size in Spam: " + Math.round(averageWordSizeSpam));
        System.out.println("Average Word Size in Ham: " + Math.round(averageWordSizeHam));
        System.out.println("\nStatistics for Spam Emails:");
        printStatsHelper(spamWordFrequencies.values());

        System.out.println("\nStatistics for Ham Emails:");
        printStatsHelper(hamWordFrequencies.values());
    }

    private double calculateAverageWordSize(Map<String, Integer> wordFrequencies) {
        int totalWordLength = 0;
        int totalWordCount = 0;

        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            totalWordLength += entry.getKey().length() * entry.getValue();
            totalWordCount += entry.getValue();
        }

        return (double) totalWordLength / totalWordCount;
    }

    public String classify(String email) {
        String[] words = email.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split(" ");

        double spamScore = calculateScore(words, spamWordFrequencies, spamPhraseFrequencies);
        double hamScore = calculateScore(words, hamWordFrequencies, hamPhraseFrequencies);
        //scales! VV
        double dynamicThreshold = (spamScore + hamScore) / 2.13;

        if (spamScore > dynamicThreshold) {
            return "Spam";
        } else {
            return "Ham";
        }
    }

    private double calculateScore(String[] words, Map<String, Integer> wordFrequencies,
                                  Map<String, Integer> phraseFrequencies) {
        double score = 0.0;
        int totalWordCount = getTotalWordCount(wordFrequencies);
        int totalPhraseCount = getTotalPhraseCount(phraseFrequencies);

        for (String word : words) {
            int count = wordFrequencies.getOrDefault(word, 0);
            score += Math.log((count + 1.0) / (totalWordCount + wordFrequencies.size() + 1.0)); // Laplace smoothing
        }

        for (int i = 0; i < words.length - 2; i++) {
            String phrase = words[i] + " " + words[i + 1] + " " + words[i + 2];
            if (!phrase.matches(".*\\d.*") && !phrase.trim().isEmpty()) {
                int count = phraseFrequencies.getOrDefault(phrase, 0);
                score += Math.log((count + 1.0) / (totalPhraseCount + phraseFrequencies.size() + 1.0)); // Laplace smoothing
            }
        }

        return score;
    }

    private int getTotalWordCount(Map<String, Integer> wordFrequencies) {
        int total = 0;

        for (int count : wordFrequencies.values()) {
            total += count;
        }

        return total;
    }

    private int getTotalPhraseCount(Map<String, Integer> phraseFrequencies) {
        int total = 0;

        for (int count : phraseFrequencies.values()) {
            total += count;
        }

        return total;
    }

    private Map<String, Integer> calculateWordFrequencies(String email) {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        String[] words = email.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split(" ");
        for (String word : words) {
            int count = wordFrequencies.getOrDefault(word, 0);
            wordFrequencies.put(word, count + 1);
        }
        return wordFrequencies;
    }

    private double calculateEuclideanDistance(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        Set<String> allWords = new HashSet<>(vector1.keySet());
        allWords.addAll(vector2.keySet());

        double sum = 0.0;
        int commonWords = 0;

        for (String word : allWords) {
            int count1 = vector1.getOrDefault(word, 0);
            int count2 = vector2.getOrDefault(word, 0);
            sum += Math.pow(count1 - count2, 2);
            commonWords++;
        }

        if (commonWords == 0) {
            return Double.NaN;
        }

        return Math.sqrt(sum);
    }

    public void calculateEuclideanDistance(List<String> testEmails, List<Integer> testLabels) {
        int numTestSpam = 0;
        int numTestHam = 0;

        double totalSpamDistance = 0.0;
        double totalHamDistance = 0.0;

        for (int i = 0; i < testEmails.size(); i++) {
            String testEmail = testEmails.get(i);
            Map<String, Integer> testVector = calculateWordFrequencies(testEmail);

            double spamDistance = calculateEuclideanDistance(testVector, spamWordFrequencies);
            double hamDistance = calculateEuclideanDistance(testVector, hamWordFrequencies);

            if (!Double.isNaN(spamDistance)) {
                totalSpamDistance += spamDistance;
                numTestSpam++;
            }

            if (!Double.isNaN(hamDistance)) {
                totalHamDistance += hamDistance;
                numTestHam++;
            }
        }

        double averageSpamDistance = totalSpamDistance / numTestSpam;
        double averageHamDistance = totalHamDistance / numTestHam;

        System.out.println("\nAverage Euclidean Distance for Spam emails: " + Math.round(averageSpamDistance));
        System.out.println("Average Euclidean Distance for Ham emails: " + Math.round(averageHamDistance));
    }

    public void createPredictionsFile(List<String> testEmails) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PREDICTIONS_FILE_PATH))) {
            for (int i = 0; i < testEmails.size(); i++) {
                String testEmail = testEmails.get(i);
                String result = classify(testEmail);
                writer.println("Email " + (i + 1) + ": " + result);
            }
            System.out.println("See predictions.txt for Test Set Output.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
