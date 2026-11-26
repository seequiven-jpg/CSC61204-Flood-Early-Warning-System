package com.group.floodwarning.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

abstract class AuthForm extends JFrame {
    private static final Color BACKGROUND = new Color(0xe9e9e9);
    private final JPanel form = new JPanel();
    private final JLabel feedback = new JLabel(" ", SwingConstants.CENTER);
    private final List<JComponent> controls = new ArrayList<>();

    AuthForm(String title) {
        super(title + " | Flood Warning App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 560));

        JPanel columns = new JPanel(new GridLayout(1, 2));
        columns.setBackground(BACKGROUND);
        setContentPane(columns);

        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(24, 32, 24, 32));
        left.add(form);
        columns.add(left);

        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 29f));
        addCentered(heading, 22);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0x999999)));
        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));
        JLabel appName = new JLabel("FLOOD WARNING APP");
        appName.setFont(appName.getFont().deriveFont(Font.BOLD, 31f));
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        branding.add(appName);
        branding.add(Box.createVerticalStrut(14));
        JLabel description = new JLabel("Community Flood Early Warning System");
        description.setFont(description.getFont().deriveFont(16f));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        branding.add(description);
        branding.add(Box.createVerticalStrut(22));
        JComponent symbol = new FloodSymbol();
        symbol.setAlignmentX(Component.CENTER_ALIGNMENT);
        symbol.setMaximumSize(symbol.getPreferredSize());
        branding.add(symbol);
        right.add(branding);
        columns.add(right);
    }

    final void field(String label, JTextField input) {
        JLabel caption = new JLabel(label);
        caption.setLabelFor(input);
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setOpaque(false);
        group.add(caption, BorderLayout.NORTH);
        input.setPreferredSize(new Dimension(342, 32));
        group.add(input, BorderLayout.CENTER);
        controls.add(input);
        addCentered(group, 12);
    }

    final void submit(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.setPreferredSize(new Dimension(342, 35));
        button.addActionListener(event -> action.run());
        controls.add(button);
        addCentered(button, 8);
        getRootPane().setDefaultButton(button);

        feedback.setPreferredSize(new Dimension(342, 42));
        feedback.setMaximumSize(feedback.getPreferredSize());
        feedback.getAccessibleContext().setAccessibleName("Status");
        addCentered(feedback, 4);
    }

    final void link(String label, Runnable action) {
        JButton button = new JButton("<html><u>" + label + "</u></html>");
        button.setForeground(new Color(0x1747ad));
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(4, 6, 4, 6));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.getAccessibleContext().setAccessibleName(label);
        button.addActionListener(event -> action.run());
        controls.add(button);
        addCentered(button, 3);
    }

    final void showFeedback(String message, boolean error) {
        feedback.setForeground(error ? new Color(0x9c2222) : new Color(0x25542e));
        feedback.setText("<html><div style='text-align:center; width:235px'>" + message + "</div></html>");
    }

    final <T> void runTask(Callable<T> task, Consumer<T> success) {
        controls.forEach(control -> control.setEnabled(false));
        showFeedback("Processing...", false);
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                controls.forEach(control -> control.setEnabled(true));
                try {
                    success.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showFeedback("The operation was interrupted. Please try again.", true);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof IllegalArgumentException) {
                        showFeedback(cause.getMessage(), true);
                    } else if (cause instanceof IOException) {
                        showFeedback("Could not read or save account data. Please try again.", true);
                    } else {
                        showFeedback("Something went wrong. Please try again.", true);
                    }
                }
            }
        }.execute();
    }

    final void open() {
        pack();
        setSize(Math.max(getWidth(), 920), Math.max(getHeight(), 580));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addCentered(JComponent component, int gap) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(component);
        form.add(Box.createVerticalStrut(gap));
    }

    private static final class FloodSymbol extends JComponent {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(142, 95);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D drawing = (Graphics2D) graphics.create();
            try {
                drawing.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawing.setColor(new Color(0x262626));
                drawing.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                drawing.drawPolygon(new int[]{71, 43, 99}, new int[]{6, 55, 55}, 3);
                drawing.drawLine(71, 24, 71, 38);
                drawing.fillOval(69, 44, 4, 4);
                for (int y : new int[]{68, 82}) {
                    java.awt.geom.Path2D wave = new java.awt.geom.Path2D.Double();
                    wave.moveTo(16, y);
                    for (int x = 16; x < 112; x += 28) {
                        wave.quadTo(x + 7, y - 8, x + 14, y);
                        wave.quadTo(x + 21, y + 8, x + 28, y);
                    }
                    drawing.draw(wave);
                }
            } finally {
                drawing.dispose();
            }
        }
    }
}
