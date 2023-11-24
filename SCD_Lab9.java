package scd_lab9;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class WordCounter {
    public Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();

    public void processLine(String line) {
        String[] words = line.split("\\s+");
        for (String word : words) {
            wordCountMap.merge(word, 1, Integer::sum);
        }
    }

    public Map<String, Integer> getWordCountMap() {
        return wordCountMap;
    }
}

class FileProcessor implements Runnable {
    public String fileName;
    public WordCounter wordCounter;

    public FileProcessor(String fileName, WordCounter wordCounter) {
        this.fileName = fileName;
        this.wordCounter = wordCounter;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordCounter.processLine(line);
            }
        } catch(IOException e) {
        }
    }
}

public class SCD_Lab9 {
    public static void main(String[] args) {
        String fileName = "your_text_file.txt";
        int numThreads = 4;

        WordCounter wordCounter = new WordCounter();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try {
            for (int i = 0; i < numThreads; i++) {
                executor.execute(new FileProcessor(fileName, wordCounter));
            }
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            System.out.println("Final Word Count:");
            wordCounter.getWordCountMap().forEach((word, count) -> System.out.println(word + ": " + count));
        } catch (InterruptedException e) {
        }
    }
}
