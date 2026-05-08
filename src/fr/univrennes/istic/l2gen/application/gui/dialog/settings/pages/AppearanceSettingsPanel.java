package fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.AbstractSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.SettingsRowPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.SettingsSectionPanel;

public final class AppearanceSettingsPanel extends AbstractSettingsPanel {

        public static final int THEME_LIGHT = 0;
        public static final int THEME_DARK = 1;
        public static final int THEME_SYSTEM = 2;
        public static final int THEME_AUTO = 3;

        private final JComboBox<String> themeComboBox;
        private final JSlider themeStartHourSlider;
        private final JSlider themeEndHourSlider;

        private final JCheckBox useFlatLafCheckBox;
        private final JSlider fontSizeSlider;
        private final JComboBox<String> fontFamilyComboBox;

        public AppearanceSettingsPanel() {
                themeComboBox = new JComboBox<>(new String[] {
                                Lang.get("settings.appearance.theme.light"),
                                Lang.get("settings.appearance.theme.dark"),
                                Lang.get("settings.appearance.theme.system"),
                                Lang.get("settings.appearance.theme.auto")
                });
                themeComboBox.setSelectedIndex(Config.getInt("settings.appearance.theme", 3));

                themeStartHourSlider = new JSlider(0, 24, 18);
                themeStartHourSlider.setMajorTickSpacing(2);
                themeStartHourSlider.setMinorTickSpacing(1);
                themeStartHourSlider.setLabelTable(themeStartHourSlider.createStandardLabels(4));
                themeStartHourSlider.setPaintTicks(true);
                themeStartHourSlider.setPaintLabels(true);
                themeStartHourSlider.setSnapToTicks(true);
                themeStartHourSlider.setValue(Config.getInt("settings.appearance.auto_start", 18));
                themeStartHourSlider.setEnabled(themeComboBox.getSelectedIndex() == AppearanceSettingsPanel.THEME_AUTO);

                themeEndHourSlider = new JSlider(0, 24, 6);
                themeEndHourSlider.setMajorTickSpacing(2);
                themeEndHourSlider.setMinorTickSpacing(1);
                themeEndHourSlider.setLabelTable(themeEndHourSlider.createStandardLabels(4));
                themeEndHourSlider.setPaintTicks(true);
                themeEndHourSlider.setPaintLabels(true);
                themeEndHourSlider.setSnapToTicks(true);
                themeEndHourSlider.setValue(Config.getInt("settings.appearance.auto_end", 6));
                themeEndHourSlider.setEnabled(themeComboBox.getSelectedIndex() == AppearanceSettingsPanel.THEME_AUTO);

                themeComboBox.addActionListener(e -> {
                        String selected = (String) themeComboBox.getSelectedItem();
                        boolean auto = selected.equals(Lang.get("settings.appearance.theme.auto"));
                        themeStartHourSlider.setEnabled(auto);
                        themeEndHourSlider.setEnabled(auto);
                });

                useFlatLafCheckBox = new JCheckBox();
                useFlatLafCheckBox.setSelected(Config.getBoolean("settings.appearance.use_flatlaf", true));
                useFlatLafCheckBox.addActionListener(e -> {
                        boolean selected = useFlatLafCheckBox.isSelected();
                        themeComboBox.setEnabled(selected);
                        if (!selected) {
                                themeStartHourSlider.setEnabled(false);
                                themeEndHourSlider.setEnabled(false);
                        } else {
                                String selectedTheme = (String) themeComboBox.getSelectedItem();
                                boolean auto = selectedTheme.equals(Lang.get("settings.appearance.theme.auto"));
                                themeStartHourSlider.setEnabled(auto);
                                themeEndHourSlider.setEnabled(auto);
                        }
                });

                fontSizeSlider = new JSlider(8, 24, 12);
                fontSizeSlider.setMajorTickSpacing(2);
                fontSizeSlider.setMinorTickSpacing(1);
                fontSizeSlider.setLabelTable(fontSizeSlider.createStandardLabels(2));
                fontSizeSlider.setPaintTicks(true);
                fontSizeSlider.setPaintLabels(true);
                fontSizeSlider.setSnapToTicks(true);
                fontSizeSlider.setValue(Config.getInt("settings.appearance.font_size", 12));

                List<String> fontFamilies = new ArrayList<>();
                fontFamilies.add(Lang.get("settings.appearance.ui.default_font"));
                for (String font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
                        fontFamilies.add(font);
                }
                fontFamilyComboBox = new JComboBox<>(fontFamilies.toArray(new String[0]));
                fontFamilyComboBox.setSelectedItem(Config.get("settings.appearance.font_family",
                                Lang.get("settings.appearance.ui.default_font")));

                SettingsSectionPanel themeSection = new SettingsSectionPanel(
                                Lang.get("settings.appearance.section.theme"));
                themeSection.addRow(new SettingsRowPanel(Lang.get("settings.appearance.theme"), themeComboBox));
                themeSection.addRow(new SettingsRowPanel(Lang.get("settings.appearance.auto.start"),
                                themeStartHourSlider));
                themeSection.addRow(new SettingsRowPanel(Lang.get("settings.appearance.auto.end"), themeEndHourSlider));

                SettingsSectionPanel uiSection = new SettingsSectionPanel(Lang.get("settings.appearance.section.ui"));
                uiSection.addRow(new SettingsRowPanel(Lang.get("settings.appearance.ui.use_flatlaf"),
                                useFlatLafCheckBox));
                uiSection.addRow(new SettingsRowPanel(Lang.get("settings.appearance.ui.font_size"), fontSizeSlider));
                uiSection.addRow(new SettingsRowPanel(Lang.get("settings.appearance.ui.font_family"),
                                fontFamilyComboBox));

                addSection(themeSection);
                addSection(uiSection);
        }

        @Override
        public boolean applySettings() {
                boolean changed = false;
                if (Config.getBoolean("settings.appearance.use_flatlaf", true) != useFlatLafCheckBox
                                .isSelected()) {
                        changed = true;
                }
                if (Config.getInt("settings.appearance.theme", 3) != themeComboBox.getSelectedIndex()) {
                        changed = true;
                }
                if (Config.getInt("settings.appearance.auto_start", 18) != themeStartHourSlider.getValue()) {
                        changed = true;
                }
                if (Config.getInt("settings.appearance.auto_end", 6) != themeEndHourSlider.getValue()) {
                        changed = true;
                }
                if (Config.getInt("settings.appearance.font_size", 12) != fontSizeSlider.getValue()) {
                        changed = true;
                }
                if (!Config.get("settings.appearance.font_family",
                                Lang.get("settings.appearance.ui.default_font"))
                                .equals(fontFamilyComboBox.getSelectedItem())) {
                        changed = true;
                }

                Config.putInt("settings.appearance.theme", themeComboBox.getSelectedIndex());
                Config.putInt("settings.appearance.auto_start", themeStartHourSlider.getValue());
                Config.putInt("settings.appearance.auto_end", themeEndHourSlider.getValue());

                Config.putBoolean("settings.appearance.use_flatlaf", useFlatLafCheckBox.isSelected());

                Config.putInt("settings.appearance.font_size", fontSizeSlider.getValue());
                Config.put("settings.appearance.font_family", (String) fontFamilyComboBox.getSelectedItem());

                return changed;
        }

        @Override
        public boolean requiresRestart() {
                return true;
        }
}
