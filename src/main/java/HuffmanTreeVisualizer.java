import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HuffmanTreeVisualizer extends Application {
    private HuffmanTree huffmanTree;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private Pane treePane;
    private Label originalSizeLabel;
    private Label compressedSizeLabel;
    private Label efficiencyLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Визуализация алгоритма Хаффмана");

        // Input Area
        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Введите текст здесь...");

        // Output Area
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);

        // Tree Pane
        treePane = new Pane();
        treePane.setPrefHeight(400);

        // Labels for sizes and efficiency
        originalSizeLabel = new Label("Исходный размер: 0 бит");
        compressedSizeLabel = new Label("Сжатый размер: 0 бит");
        efficiencyLabel = new Label("Эффективность сжатия: 0%");

        // Load File Button
        Button loadFileButton = new Button("Импортировать из файла");
        loadFileButton.setOnAction(e -> loadFile(primaryStage));

        // Build Tree Button
        Button buildTreeButton = new Button("Построить дерево Хаффмана");
        buildTreeButton.setOnAction(e -> buildAndVisualizeTree());

        // Compress File Button
        Button compressFileButton = new Button("Сжать файл");
        compressFileButton.setOnAction(e -> compressFile(primaryStage));

        // Decompress File Button
        Button decompressFileButton = new Button("Расжать файл");
        decompressFileButton.setOnAction(e -> decompressFile(primaryStage));

        // Layout
        GridPane inputPane = new GridPane();
        inputPane.setPadding(new Insets(10));
        inputPane.setHgap(10);
        inputPane.setVgap(10);
        inputPane.add(new Label("Введите текст:"), 0, 0);
        inputPane.add(inputTextArea, 0, 1, 2, 1);
        inputPane.add(loadFileButton, 0, 2);
        inputPane.add(buildTreeButton, 1, 2);
        inputPane.add(compressFileButton, 0, 3);
        inputPane.add(decompressFileButton, 1, 3);
        inputPane.add(originalSizeLabel, 0, 4);
        inputPane.add(compressedSizeLabel, 1, 4);
        inputPane.add(efficiencyLabel, 0, 5, 2, 1);

        BorderPane root = new BorderPane();
        root.setTop(inputPane);
        root.setCenter(treePane);
        root.setBottom(outputTextArea);
        BorderPane.setMargin(outputTextArea, new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть текстовый файл");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            inputTextArea.setText(readFile(file));
        }
    }

    private String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void buildAndVisualizeTree() {
        String text = inputTextArea.getText().trim();
        if (text.isEmpty()) {
            showAlert("Ошибка ввода", "Пожалуйста, введите текст или загрузите файл");
            return;
        }

        // Calculate frequency of each character
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        // Build Huffman Tree
        huffmanTree = new HuffmanTree(frequencyMap);

        // Encode and Decode the text
        String encodedText = huffmanTree.encode(text);
        String decodedText = huffmanTree.decode(encodedText);

        outputTextArea.setText("Закодированный текст: " + encodedText + "\nРаскодированный текст: " + decodedText);

        // Display sizes and efficiency
        originalSizeLabel.setText("Исходный размер: " + huffmanTree.getOriginalSize() + " бит");
        compressedSizeLabel.setText("Сжатый размер: " + huffmanTree.getCompressedSize() + " бит");
        efficiencyLabel.setText("Эффективность сжатия: " + String.format("%.2f", huffmanTree.getCompressionEfficiency()) + "%");

        // Visualize the Huffman Tree
        treePane.getChildren().clear();
        drawTree(treePane, huffmanTree.getRoot(), 400, 20, 200);
    }

    private void drawTree(Pane pane, HuffmanNode root, double x, double y, double hGap) {
        if (root == null) return;

        if (root.left != null) {
            Line leftLine = new Line(x, y, x - hGap, y + 50);
            pane.getChildren().add(leftLine);
            drawTree(pane, root.left, x - hGap, y + 50, hGap / 2);
        }

        if (root.right != null) {
            Line rightLine = new Line(x, y, x + hGap, y + 50);
            pane.getChildren().add(rightLine);
            drawTree(pane, root.right, x + hGap, y + 50, hGap / 2);
        }

        Rectangle rect = new Rectangle(x - 15, y - 15, 30, 30);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        Text text = new Text(x - 10, y + 5, (root.character == '\0' ? "*" : root.character) + ":" + root.frequency);
        pane.getChildren().addAll(rect, text);
    }

    private void compressFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить сжатый файл");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null && huffmanTree != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(huffmanTree.encode(inputTextArea.getText().trim()));
                showAlert("Успех", "Файл сжат и успешно сохранен");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось сохранить сжатый файл");
            }
        }
    }

    private void decompressFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть сжатый файл");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && huffmanTree != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder encodedText = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    encodedText.append(line);
                }
                outputTextArea.setText("Раскодированный текст: " + huffmanTree.decode(encodedText.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось прочитать сжатый файл");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
