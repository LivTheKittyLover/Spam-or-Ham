import java.util.*;

public class EuclideanDistanceCalculator {

    public double calculateEuclideanDistance(Map<String, Integer> vector1, Map<String, Integer> vector2) {
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

    public void calculateEuclideanDistance(List<String> testEmails, List<Integer> testLabels,
                                           Map<String, Integer> spamWordFrequencies,
                                           Map<String, Integer> hamWordFrequencies) {
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

    private Map<String, Integer> calculateWordFrequencies(String email) {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        String[] words = email.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split(" ");
        for (String word : words) {
            int count = wordFrequencies.getOrDefault(word, 0);
            wordFrequencies.put(word, count + 1);
        }
        return wordFrequencies;
    }
}

