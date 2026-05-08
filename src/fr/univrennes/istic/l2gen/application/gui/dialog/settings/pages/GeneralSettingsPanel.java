package fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.AbstractSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.SettingsRowPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.SettingsSectionPanel;

public final class GeneralSettingsPanel extends AbstractSettingsPanel {
        public static final int SOURCE_NONE = 0;
        public static final int SOURCE_FILE = 1;
        public static final int SOURCE_URL = 2;

        private final JComboBox<String> languageComboBox;

        private final JCheckBox showWelcomeScreenCheckBox;
        private final JCheckBox checkUpdatesCheckBox;
        private final JCheckBox reopenLastTablesCheckBox;
        private final JComboBox<String> defaultTableSourceComboBox;

        private final JButton defaultTableSourceFileButton;
        private final JTextField defaultTableSourceTextField;

        private final JCheckBox confirmOnCloseCheckBox;
        private final JCheckBox confirmOnTableCloseCheckBox;

        public GeneralSettingsPanel() {
                SettingsSectionPanel localizationSection = new SettingsSectionPanel(
                                Lang.get("settings.general.section.localization"));

                List<String> languagesBox = new ArrayList<>();
                for (String lang : Lang.getSupportedLanguages()) {
                        Locale local = Locale.forLanguageTag(lang);

                        String displayName = local.getDisplayLanguage(local);
                        displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

                        languagesBox.add(displayName + " (" + local.getLanguage().toUpperCase() + ")");
                }

                languageComboBox = new JComboBox<>(languagesBox.toArray(new String[0]));
                languageComboBox.setSelectedIndex(
                                Lang.getSupportedLanguages().stream().toList().indexOf(Lang.getLocale().getLanguage()));

                localizationSection
                                .addRow(new SettingsRowPanel(Lang.get("settings.general.language"), languageComboBox));

                SettingsSectionPanel startupSection = new SettingsSectionPanel(
                                Lang.get("settings.general.section.startup"));

                showWelcomeScreenCheckBox = new JCheckBox();
                showWelcomeScreenCheckBox.setSelected(Config.getBoolean("settings.startup.show_welcome", true));
                startupSection.addRow(new SettingsRowPanel(Lang.get("settings.general.startup.show_welcome"),
                                showWelcomeScreenCheckBox));

                checkUpdatesCheckBox = new JCheckBox();
                checkUpdatesCheckBox.setSelected(Config.getBoolean("settings.startup.check_updates", true));
                startupSection.addRow(new SettingsRowPanel(Lang.get("settings.general.startup.check_updates"),
                                checkUpdatesCheckBox));

                reopenLastTablesCheckBox = new JCheckBox();
                reopenLastTablesCheckBox
                                .setSelected(Config.getBoolean("settings.startup.reopen_tables", false));
                startupSection.addRow(new SettingsRowPanel(Lang.get("settings.general.startup.reopen_table"),
                                reopenLastTablesCheckBox));

                defaultTableSourceComboBox = new JComboBox<>(
                                new String[] {
                                                Lang.get("settings.general.startup.default.none"),
                                                Lang.get("settings.general.startup.default.from_file"),
                                                Lang.get("settings.general.startup.default.from_url")
                                });

                defaultTableSourceTextField = new JTextField();
                defaultTableSourceTextField.setPreferredSize(
                                new Dimension(SettingsRowPanel.LABEL_WIDTH, SettingsRowPanel.ROW_HEIGHT - 8));
                defaultTableSourceTextField.setVisible(false);

                defaultTableSourceFileButton = new JButton(Lang.get("settings.general.startup.default.file_chooser"));
                defaultTableSourceFileButton.setVisible(false);

                defaultTableSourceFileButton.addActionListener(e -> {
                        SystemFileChooser fileChooser = new SystemFileChooser();
                        fileChooser.setFileSelectionMode(SystemFileChooser.FILES_ONLY);

                        if (fileChooser.showOpenDialog(GUIController.getInstance()
                                        .getMainView()) != SystemFileChooser.APPROVE_OPTION) {
                                return;
                        }

                        defaultTableSourceTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                });

                defaultTableSourceComboBox.addActionListener(e -> {
                        if (defaultTableSourceComboBox.getSelectedIndex() == GeneralSettingsPanel.SOURCE_NONE) {
                                defaultTableSourceTextField.setVisible(false);
                                defaultTableSourceFileButton.setVisible(false);
                        } else if (defaultTableSourceComboBox.getSelectedIndex() == GeneralSettingsPanel.SOURCE_FILE) {
                                defaultTableSourceTextField.setVisible(true);
                                defaultTableSourceTextField.setEnabled(false);

                                defaultTableSourceFileButton.setVisible(true);
                        } else {
                                defaultTableSourceTextField.setVisible(true);
                                defaultTableSourceTextField.setEnabled(true);

                                defaultTableSourceFileButton.setVisible(false);
                        }
                        defaultTableSourceTextField.setText("");
                });

                String defaultTableSource = Config.get("settings.startup.default_table_source", "");
                File defaultTableSourceFile = new File(defaultTableSource);
                if (defaultTableSource.isEmpty()) {
                        defaultTableSourceComboBox.setSelectedIndex(GeneralSettingsPanel.SOURCE_NONE);
                        defaultTableSourceTextField.setText("");
                } else if (defaultTableSourceFile.exists() && defaultTableSourceFile.isFile()) {
                        defaultTableSourceComboBox.setSelectedIndex(GeneralSettingsPanel.SOURCE_FILE);
                        defaultTableSourceTextField.setText(defaultTableSource);
                        defaultTableSourceTextField.setVisible(true);
                        defaultTableSourceTextField.setEnabled(false);

                        defaultTableSourceFileButton.setVisible(true);
                } else {
                        defaultTableSourceComboBox.setSelectedIndex(GeneralSettingsPanel.SOURCE_URL);
                        defaultTableSourceTextField.setText(defaultTableSource);
                        defaultTableSourceTextField.setVisible(true);
                        defaultTableSourceFileButton.setVisible(false);
                }

                startupSection.addRow(new SettingsRowPanel(Lang.get("settings.general.startup.default_table"),
                                defaultTableSourceComboBox));
                startupSection.addRow(new SettingsRowPanel(null, defaultTableSourceTextField,
                                defaultTableSourceFileButton));

                SettingsSectionPanel closingSection = new SettingsSectionPanel(
                                Lang.get("settings.general.section.closing"));

                confirmOnCloseCheckBox = new JCheckBox();
                confirmOnCloseCheckBox.setSelected(
                                Config.getBoolean("settings.closing.confirm_on_close", false));
                closingSection.addRow(new SettingsRowPanel(Lang.get("settings.general.closing.app_close"),
                                confirmOnCloseCheckBox));

                confirmOnTableCloseCheckBox = new JCheckBox();
                confirmOnTableCloseCheckBox.setSelected(
                                Config.getBoolean("settings.closing.confirm_on_table_close", false));
                closingSection.addRow(new SettingsRowPanel(Lang.get("settings.general.closing.table_close"),
                                confirmOnTableCloseCheckBox));

                addSection(localizationSection);
                addSection(startupSection);
                addSection(closingSection);
        }

        @Override
        public boolean applySettings() {
                boolean changed = false;
                if (Lang.getSupportedLanguages().stream().toList()
                                .indexOf(Lang.getLocale().getLanguage()) != languageComboBox
                                                .getSelectedIndex()) {
                        changed = true;
                }
                if (Config.getBoolean("settings.startup.show_welcome", true) != showWelcomeScreenCheckBox
                                .isSelected()) {
                        changed = true;
                }
                if (Config.getBoolean("settings.startup.check_updates", true) != checkUpdatesCheckBox
                                .isSelected()) {
                        changed = true;
                }
                if (Config.getBoolean("settings.startup.reopen_tables", false) != reopenLastTablesCheckBox
                                .isSelected()) {
                        changed = true;
                }
                String defaultTableSource = Config.get("settings.startup.default_table_source", "");
                if (defaultTableSourceComboBox.getSelectedIndex() == 0) {
                        if (!defaultTableSource.isEmpty()) {
                                changed = true;
                        }
                } else if (defaultTableSourceComboBox.getSelectedIndex() == 1) {
                        if (!defaultTableSource.equals(defaultTableSourceTextField.getText())) {
                                changed = true;
                        }
                } else {
                        if (!defaultTableSource.equals(defaultTableSourceTextField.getText())) {
                                changed = true;
                        }
                }
                if (Config.getBoolean("settings.closing.confirm_on_close", false) != confirmOnCloseCheckBox
                                .isSelected()) {
                        changed = true;
                }
                if (Config.getBoolean("settings.closing.confirm_on_table_close",
                                false) != confirmOnTableCloseCheckBox.isSelected()) {
                        changed = true;
                }

                int selectedLanguageIndex = languageComboBox.getSelectedIndex();
                if (selectedLanguageIndex >= 0) {
                        String selectedLanguage = Lang.getSupportedLanguages().stream().toList()
                                        .get(selectedLanguageIndex);
                        GUIController.getInstance().onLanguageChange(Locale.forLanguageTag(selectedLanguage));
                }

                Config.putBoolean("settings.startup.show_welcome", showWelcomeScreenCheckBox.isSelected());
                Config.putBoolean("settings.startup.check_update", checkUpdatesCheckBox.isSelected());
                Config.putBoolean("settings.startup.reopen_tables", reopenLastTablesCheckBox.isSelected());

                Config.put("settings.startup.default_table_source", defaultTableSourceTextField.getText());

                Config.putBoolean("settings.closing.confirm_on_close", confirmOnCloseCheckBox.isSelected());
                Config.putBoolean("settings.closing.confirm_on_table_close",
                                confirmOnTableCloseCheckBox.isSelected());

                return changed;
        }

        @Override
        public boolean requiresRestart() {
                return false;
        }
}
