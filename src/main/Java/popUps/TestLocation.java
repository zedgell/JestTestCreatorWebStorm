package popUps;

import Actions.CreateTest;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestLocation {
    public static void createUI(final JFrame frame){
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);


        JButton button = new JButton("Select File Location");
        final JLabel label = new JLabel();
        if(CreateTest.testFolder != null){
            label.setText(CreateTest.testFolder.getPath());
        } else {
            label.setText(CreateTest.projectLocation.getPath());
        }

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if(CreateTest.testFolder != null){
                fileChooser.setCurrentDirectory(CreateTest.testFolder);
            } else {
                fileChooser.setCurrentDirectory(new File(CreateTest.projectLocation.getPath()));
            }
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(frame);
            if(option == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                label.setText("Folder Selected: " + file.getPath());
                CreateTest.testFolder = file;
                CreateTest.getRelativeFilePath();
            }else{
                label.setText("Open command canceled");
            }
        });

        panel.add(button);
        panel.add(label);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }
    public static void createWindow() {
        JFrame frame = new JFrame("Swing Tester");
        createUI(frame);
        frame.setSize(560, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        JButton ok = new JButton("ok");
        ok.addActionListener(e -> {
            System.out.println("keep running");
            try {
                CreateTest.writeTestFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            frame.setVisible(false);
            frame.dispose();
        });
        JButton close = new JButton("close");
        close.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
        });
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
        pane.add(ok);
        pane.add(Box.createHorizontalGlue());
        pane.add(close);
        frame.add(pane, BorderLayout.SOUTH);
    }
}
