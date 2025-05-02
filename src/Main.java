import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        ResponseBuilder rb = new ResponseBuilder();

        // Construct GUI
        JFrame frame = new JFrame("Anilist Getter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750,600);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        frame.add(mainPanel);

        JPanel searchPanel = new JPanel();
        JLabel textLabel = new JLabel("Username:");
        JTextField inputField = new JTextField(20);
        JButton okButton = new JButton("OK");
        searchPanel.add(textLabel);
        searchPanel.add(inputField);
        searchPanel.add(okButton);

        JPanel searchPanelBox = new JPanel();
        searchPanelBox.setLayout(new BoxLayout(searchPanelBox, BoxLayout.Y_AXIS));
        JLabel statusLabel = new JLabel("Ready");
        searchPanelBox.add(searchPanel);
        searchPanelBox.add(statusLabel);
        mainPanel.add(searchPanelBox);

        JPanel outputPanel = new JPanel();
        JTextArea outputText  = new JTextArea(30, 60);
        outputText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputText,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputPanel.add(scrollPane);
        mainPanel.add(outputPanel);

        frame.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(e -> {
            outputText.setText("");
            String username = inputField.getText();
            if (!username.equals("")) {
                statusLabel.setText("Searching...");
                AtomicReference<String> animeList = new AtomicReference<>("");
                new Thread(() -> {
                    try {
                        animeList.set(rb.executeResponse(username));
                        statusLabel.setText("Success!");
                        outputText.setText(animeList.get());
                    } catch (RuntimeException exc) {
                        statusLabel.setText("Invalid Username.");
                    }
                }).start();

                inputField.setText("");
            }
        });

        frame.setVisible(true);
    }
}