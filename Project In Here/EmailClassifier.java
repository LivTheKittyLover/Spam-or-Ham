import java.util.*;

public class EmailClassifier {

    private Map<String, Integer> spamWordFrequencies;
    private Map<String, Integer> hamWordFrequencies;
    private Map<String, Integer> spamPhraseFrequencies;
    private Map<String, Integer> hamPhraseFrequencies;

    public EmailClassifier() {
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
        printStatsHelper(labels);
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
}
