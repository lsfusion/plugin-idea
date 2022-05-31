package com.lsfusion.actions;

import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckBoxGroup extends JPanel {

    private final List<JCheckBox> checkBoxes;

    public CheckBoxGroup(Module[] modules, List<String> includedModules) {
        List<String> moduleNames = Arrays.stream(modules).map(Module::getName)
                .sorted((o1, o2) -> includedModules.contains(o1) ? (includedModules.contains(o2) ? o1.compareTo(o2) : -1) : includedModules.contains(o2) ? 1 : o1.compareTo(o2))
                .collect(Collectors.toList());
        checkBoxes = new ArrayList<>(moduleNames.size());
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));

        JButton selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(e -> {
            for (JCheckBox cb : checkBoxes) {
                cb.setSelected(true);
            }
        });
        header.add(selectAllButton);

        JButton selectNoneButton = new JButton("Select None");
        selectNoneButton.addActionListener(e -> {
            for (JCheckBox cb : checkBoxes) {
                cb.setSelected(false);
            }
        });
        header.add(selectNoneButton);

        add(header, BorderLayout.NORTH);

        JPanel content = new ScrollablePane(new GridBagLayout());
        content.setBackground(UIManager.getColor("List.background"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;

        for (String moduleName : moduleNames) {
            JCheckBox cb = new JCheckBox(moduleName);
            cb.setOpaque(false);
            checkBoxes.add(cb);
            if (includedModules.isEmpty() || includedModules.contains(moduleName)) {
                cb.setSelected(true);
            }
            content.add(cb, gbc);
        }

        add(new JScrollPane(content));
    }

    public List<String> getIncludedModules() {
        return checkBoxes.stream().filter(AbstractButton::isSelected).map(AbstractButton::getText).collect(Collectors.toList());
    }

    private static class ScrollablePane extends JPanel implements Scrollable {

        public ScrollablePane(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(100, 200);
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            boolean track = false;
            Container parent = getParent();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                track = vp.getWidth() > getPreferredSize().width;
            }
            return track;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            boolean track = false;
            Container parent = getParent();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                track = vp.getHeight() > getPreferredSize().height;
            }
            return track;
        }

    }
}