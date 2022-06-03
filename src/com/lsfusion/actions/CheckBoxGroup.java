package com.lsfusion.actions;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBScrollPane;
import org.jdesktop.swingx.VerticalLayout;

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

        JPanel content = new JPanel(new VerticalLayout());

        for (String moduleName : moduleNames) {
            JCheckBox cb = new JCheckBox(moduleName);
            cb.setOpaque(false);
            checkBoxes.add(cb);
            if (includedModules.isEmpty() || includedModules.contains(moduleName)) {
                cb.setSelected(true);
            }
            content.add(cb);
        }

        JBScrollPane scrollPane = new JBScrollPane(content);
        scrollPane.setPreferredSize(new Dimension(100, 200));
        add(scrollPane);
    }

    public List<String> getIncludedModules() {
        return checkBoxes.stream().filter(AbstractButton::isSelected).map(AbstractButton::getText).collect(Collectors.toList());
    }
}