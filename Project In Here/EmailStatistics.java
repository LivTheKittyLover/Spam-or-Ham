import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.*;


public class EmailStatistics {

    public void printStatsHelper(Collection<Integer> values) {
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




