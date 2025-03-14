package GUI;

import logic.SelectionPolicy;
import logic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimulationFrame {
    private JTextField[] nameFields;
    private JLabel[] labels;
    private JPanel mainPanel, inputPanel, buttonPanel;
    private JScrollPane scrollPane;
    private JButton startButton, resetButton;
    private JTextArea outputTextArea;
    private JComboBox<String> selectionPolicy;
    private final String[] strategies = {"Shortest Time", "Shortest Queue"};
    private JFrame frame;
    private SimulationManager manager;

    public SimulationFrame() {
        this.manager = new SimulationManager(10, 4, 30, 2, 4, 1, 5, SelectionPolicy.TIMESTRATEGY) {
            @Override
            public void log(String s) {
                super.log(s);
                outputTextArea.append(s);
                outputTextArea.append("\n");
                System.out.println(s);
            }
        };

        frame = new JFrame("Queue management system");
        frame.setTitle("Queue Management System");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xfff68f));
        inputPanel = new JPanel(new GridLayout(7, 1));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(0xfff68f));
        nameFields = new JTextField[7];
        labels = new JLabel[8];
        labels[0] = new JLabel("Number of Clients: ");
        labels[1] = new JLabel("Number of Queues: ");
        labels[2] = new JLabel("Maximum Simulation Time: ");
        labels[3] = new JLabel("Minimum Arrival Time: ");
        labels[4] = new JLabel("Maximum Arrival Time: ");
        labels[5] = new JLabel("Minimum Service Time: ");
        labels[6] = new JLabel("Maximum Service Time: ");
        labels[7] = new JLabel("Select the desired strategy: ");
        selectionPolicy = new JComboBox<String>(strategies);
        selectionPolicy.setVisible(true);
        for (int i = 0; i < 7; i++) {
            inputPanel.add(labels[i]);
            JTextField nameField = new JTextField(20);
            nameFields[i] = nameField;
            inputPanel.add(nameField);
        }
        nameFields[0].setText("6");
        nameFields[1].setText("2");
        nameFields[2].setText("20");
        nameFields[3].setText("1");
        nameFields[4].setText("4");
        nameFields[5].setText("1");
        nameFields[6].setText("4");
        inputPanel.add(labels[7]);
        inputPanel.add(selectionPolicy);
        startButton = new JButton("Start Simulation");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputTextArea.setText("");
                try {
                    manager.setMinArrivalTime(Integer.parseInt(nameFields[3].getText()));
                    manager.setMaxArrivalTime(Integer.parseInt(nameFields[4].getText()));

                    manager.setMinServiceTime(Integer.parseInt(nameFields[5].getText()));
                    manager.setMaxServiceTime(Integer.parseInt(nameFields[6].getText()));

                    manager.setNrClients(Integer.parseInt(nameFields[0].getText()));
                    manager.setNrServers(Integer.parseInt(nameFields[1].getText()));

                    manager.setMaxSimulationTime(Integer.parseInt(nameFields[2].getText()));

                    manager.setSelectionPolicy(selectionPolicy.getSelectedItem().equals(strategies[0]) ? SelectionPolicy.TIMESTRATEGY : SelectionPolicy.SHORTESTQUEUESTRATEGY);

                    manager.simulationSetUp();
                    manager.startSim();
                }
                catch (NumberFormatException err) {
                    outputTextArea.setText("All entries must be positive integers!");
                }
            }
        });
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 7; i ++)
                    nameFields[i].setText("");
                outputTextArea.setText("");
            }
        });
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0xfff68f));
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        scrollPane = new JScrollPane(outputTextArea);
        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
