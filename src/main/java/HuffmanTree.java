import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {
    private HuffmanNode root;
    private Map<Character, String> huffmanCode;
    private int originalSize;
    private int compressedSize;

    public HuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        for (var entry : frequencyMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            int sum = left.frequency + right.frequency;
            pq.add(new HuffmanNode(sum, left, right));
        }

        root = pq.poll();
        huffmanCode = new HashMap<>();
        buildHuffmanCode(root, "");
    }

    private void buildHuffmanCode(HuffmanNode node, String code) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            huffmanCode.put(node.character, code);
        }
        buildHuffmanCode(node.left, code + '0');
        buildHuffmanCode(node.right, code + '1');
    }

    public Map<Character, String> getHuffmanCode() {
        return huffmanCode;
    }

    public HuffmanNode getRoot() {
        return root;
    }

    public String encode(String text) {
        StringBuilder sb = new StringBuilder();
        originalSize = text.length() * 8;
        for (char c : text.toCharArray()) {
            sb.append(huffmanCode.get(c));
        }
        compressedSize = sb.length();
        return sb.toString();
    }

    public String decode(String encodedText) {
        StringBuilder sb = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : encodedText.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.left == null && current.right == null) {
                sb.append(current.character);
                current = root;
            }
        }
        return sb.toString();
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public int getCompressedSize() {
        return compressedSize;
    }

    public double getCompressionEfficiency() {
        return ((double) compressedSize / originalSize) * 100;
    }
}
