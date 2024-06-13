import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HuffmanCoding {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter '1' to input text string, '2' to input file path:");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String text = "";
        if (choice == 1) {
            System.out.println("Enter the text string:");
            text = scanner.nextLine();
        } else if (choice == 2) {
            System.out.println("Enter the file path:");
            String filePath = scanner.nextLine();
            text = readFile(filePath);
        } else {
            System.out.println("Invalid choice!");
            System.exit(1);
        }

        // Calculate frequency of each character
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        // Build Huffman Tree
        HuffmanTree huffmanTree = new HuffmanTree(frequencyMap);

        // Encode the text
        String encodedText = huffmanTree.encode(text);
        System.out.println("Encoded Text: " + encodedText);

        // Decode the encoded text
        String decodedText = huffmanTree.decode(encodedText);
        System.out.println("Decoded Text: " + decodedText);
    }

    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return content.toString();
    }
}
