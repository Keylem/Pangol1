package fr.univrennes.istic.l2gen.application.gui.dialog.filter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fr.univrennes.istic.l2gen.application.core.config.Ico;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.core.filter.FilterCondition;
import fr.univrennes.istic.l2gen.application.core.filter.FilterLogic;
import fr.univrennes.istic.l2gen.application.core.filter.FilterOperator;
import fr.univrennes.istic.l2gen.application.core.services.stats.StatisticService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;
import fr.univrennes.istic.l2gen.application.gui.dialog.DialogBase;

import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FilterDialog extends JDialog {

    private static final String CARD_STRING = "STRING";
    private static final String CARD_CATEGORY = "CATEGORY";
    private static final String CARD_NUMERIC = "NUMERIC";
    private static final String CARD_DATE = "DATE";
    private static final String CARD_BOOLEAN = "BOOLEAN";
    private static final String CARD_UNSUPPORTED = "UNSUPPORTED";

    private final DataTable table;

    private final List<String> columnNames;

    private final List<Filter> result = new ArrayList<>();
    private final JPanel conditionsPanel = new JPanel();
    private final List<FilterRow> conditionRows = new ArrayList<>();

    private JComboBox<String> columnComboBox;
    private JComboBox<String> filterTypeComboBox;
    private JComboBox<String> sortDirectionComboBox;
    private JComboBox<String> logicOperatorComboBox;
    private JPanel conditionOptionsPanel;

    private JPanel searchInputsPanel;
    private JTextField searchTextField;
    private JComboBox<String> searchSelectComboBox;
    private JSpinner searchNumericSpinner;
    private JSpinner searchDateSpinner;
    private JComboBox<String> searchBooleanComboBox;

    private JPanel rangeInputsPanel;
    private JSpinner rangeLengthMinSpinner;
    private JSpinner rangeLengthMaxSpinner;
    private JSpinner rangeNumericMinSpinner;
    private JSpinner rangeNumericMaxSpinner;
    private JSpinner rangeDateMinSpinner;
    private JSpinner rangeDateMaxSpinner;

    private JPanel topNInputsPanel;
    private JSpinner topNLengthSpinner;
    private JSpinner topNNumericSpinner;
    private JSpinner topNDateSpinner;

    private JPanel bottomNInputsPanel;
    private JSpinner bottomNLengthSpinner;
    private JSpinner bottomNNumericSpinner;
    private JSpinner bottomNDateSpinner;

    private FilterDialog(Frame parent, DataTable table) {
        super(parent, Lang.get("filter.title"), true);

        this.table = table;
        this.columnNames = table.getColumnNames();
        build();
        pack();

        setSize(DialogBase.WIDTH, DialogBase.HEIGHT);
        setMinimumSize(new Dimension(DialogBase.WIDTH, DialogBase.HEIGHT));
        setLocationRelativeTo(parent);
        setResizable(true);
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        root.add(buildFilterConfigPanel(), BorderLayout.NORTH);
        root.add(buildConditionsListPanel(), BorderLayout.CENTER);
        root.add(buildButtonBar(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildFilterConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(4, 0, 4, 10);
        labelConstraints.gridx = 0;

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(4, 0, 4, 0);
        fieldConstraints.gridx = 1;

        labelConstraints.gridy = 0;
        fieldConstraints.gridy = 0;
        panel.add(new JLabel(Lang.get("filter.column")), labelConstraints);
        columnComboBox = new JComboBox<>(columnNames.toArray(new String[0]));
        panel.add(columnComboBox, fieldConstraints);

        labelConstraints.gridy = 1;
        fieldConstraints.gridy = 1;
        panel.add(new JLabel(Lang.get("filter.type")), labelConstraints);

        String[] types = new String[FilterType.values().length];
        for (int i = 0; i < FilterType.values().length; i++) {
            types[i] = Lang.get("filter.type." + FilterType.values()[i].name().toLowerCase());
        }
        filterTypeComboBox = new JComboBox<>(types);
        panel.add(filterTypeComboBox, fieldConstraints);

        labelConstraints.gridy = 2;
        fieldConstraints.gridy = 2;
        panel.add(new JLabel(Lang.get("filter.logic_operator")), labelConstraints);
        logicOperatorComboBox = new JComboBox<>(
                new String[] { Lang.get("filter.logic_operator.and"), Lang.get("filter.logic_operator.or") });
        panel.add(logicOperatorComboBox, fieldConstraints);

        conditionOptionsPanel = new JPanel(new CardLayout());
        conditionOptionsPanel.add(buildSearchPanel(), FilterType.SEARCH.name());
        conditionOptionsPanel.add(buildRangePanel(), FilterType.RANGE.name());
        conditionOptionsPanel.add(buildTopNPanel(), FilterType.TOP_N.name());
        conditionOptionsPanel.add(buildBottomNPanel(), FilterType.BOTTOM_N.name());
        conditionOptionsPanel.add(new JPanel(), FilterType.SHOW_EMPTY.name());
        conditionOptionsPanel.add(new JPanel(), FilterType.HIDE_EMPTY.name());
        conditionOptionsPanel.add(buildSortPanel(), FilterType.SORT.name());

        labelConstraints.gridy = 3;
        labelConstraints.anchor = GridBagConstraints.NORTHWEST;
        fieldConstraints.gridy = 3;
        panel.add(new JLabel(Lang.get("filter.parameters")), labelConstraints);
        panel.add(conditionOptionsPanel, fieldConstraints);

        filterTypeComboBox.addActionListener(event -> refreshConditionPanel());
        columnComboBox.addActionListener(event -> refreshConditionPanel());
        refreshConditionPanel();

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        JButton addFilterButton = new JButton(Ico.get("icons/add.svg"));
        addFilterButton.addActionListener(event -> addCurrentFilter());
        addButtonPanel.add(addFilterButton);

        GridBagConstraints fullRowConstraints = new GridBagConstraints();
        fullRowConstraints.gridx = 0;
        fullRowConstraints.gridy = 4;
        fullRowConstraints.gridwidth = 2;
        fullRowConstraints.fill = GridBagConstraints.HORIZONTAL;
        fullRowConstraints.insets = new Insets(4, 0, 0, 0);
        panel.add(addButtonPanel, fullRowConstraints);

        return panel;
    }

    private JPanel buildSearchPanel() {
        searchInputsPanel = new JPanel(new CardLayout());

        searchTextField = new JTextField();
        searchInputsPanel.add(buildLabeledInputPanel(Lang.get("filter.search_term"), searchTextField), CARD_STRING);

        searchSelectComboBox = new JComboBox<>();
        searchInputsPanel.add(buildLabeledInputPanel(Lang.get("filter.search_category"), searchSelectComboBox),
                CARD_CATEGORY);

        searchNumericSpinner = createDoubleSpinner();
        searchInputsPanel.add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.double"), searchNumericSpinner),
                CARD_NUMERIC);

        searchDateSpinner = createDateSpinner();
        searchInputsPanel.add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.date"), searchDateSpinner),
                CARD_DATE);

        searchBooleanComboBox = new JComboBox<>(new String[] { "true", "false" });
        searchInputsPanel.add(buildLabeledInputPanel(Lang.get("filter.search_term"), searchBooleanComboBox),
                CARD_BOOLEAN);

        return searchInputsPanel;
    }

    private JPanel buildRangePanel() {
        rangeInputsPanel = new JPanel(new CardLayout());

        rangeLengthMinSpinner = createIntegerSpinner();
        rangeLengthMaxSpinner = createIntegerSpinner();
        rangeInputsPanel.add(buildRangeInputPanel(rangeLengthMinSpinner, rangeLengthMaxSpinner), CARD_STRING);

        rangeNumericMinSpinner = createDoubleSpinner();
        rangeNumericMaxSpinner = createDoubleSpinner();
        rangeInputsPanel.add(buildRangeInputPanel(rangeNumericMinSpinner, rangeNumericMaxSpinner), CARD_NUMERIC);

        rangeDateMinSpinner = createDateSpinner();
        rangeDateMaxSpinner = createDateSpinner();
        rangeInputsPanel.add(buildRangeInputPanel(rangeDateMinSpinner, rangeDateMaxSpinner), CARD_DATE);

        rangeInputsPanel.add(new JPanel(), CARD_UNSUPPORTED);

        return rangeInputsPanel;
    }

    private JPanel buildTopNPanel() {
        topNInputsPanel = new JPanel(new CardLayout());

        topNLengthSpinner = createIntegerSpinner();
        topNInputsPanel.add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.length"), topNLengthSpinner),
                CARD_STRING);

        topNNumericSpinner = createDoubleSpinner();
        topNInputsPanel.add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.double"), topNNumericSpinner),
                CARD_NUMERIC);

        topNDateSpinner = createDateSpinner();
        topNInputsPanel.add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.date"), topNDateSpinner),
                CARD_DATE);

        topNInputsPanel.add(new JPanel(), CARD_UNSUPPORTED);
        return topNInputsPanel;
    }

    private JPanel buildBottomNPanel() {
        bottomNInputsPanel = new JPanel(new CardLayout());

        bottomNLengthSpinner = createIntegerSpinner();
        bottomNInputsPanel
                .add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.length"), bottomNLengthSpinner),
                        CARD_STRING);

        bottomNNumericSpinner = createDoubleSpinner();
        bottomNInputsPanel
                .add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.double"), bottomNNumericSpinner),
                        CARD_NUMERIC);

        bottomNDateSpinner = createDateSpinner();
        bottomNInputsPanel
                .add(buildLabeledInputPanel(Lang.get("tablecolumnmenu.filter.date"), bottomNDateSpinner), CARD_DATE);

        bottomNInputsPanel.add(new JPanel(), CARD_UNSUPPORTED);
        return bottomNInputsPanel;
    }

    private JPanel buildSortPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        sortDirectionComboBox = new JComboBox<>(
                new String[] { Lang.get("filter.sort.ascending"), Lang.get("filter.sort.descending") });
        panel.add(new JLabel(Lang.get("filter.sort.direction")), BorderLayout.WEST);
        panel.add(sortDirectionComboBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLabeledInputPanel(String label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRangeInputPanel(JComponent minComponent, JComponent maxComponent) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 6, 0));
        panel.add(new JLabel(Lang.get("filter.min")));
        panel.add(minComponent);
        panel.add(new JLabel(Lang.get("filter.max")));
        panel.add(maxComponent);
        return panel;
    }

    private JSpinner createIntegerSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, null, null, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0");
        spinner.setEditor(editor);
        editor.getTextField().setColumns(12);
        return spinner;
    }

    private JSpinner createDoubleSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.0, null, null, 0.1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0.###");
        spinner.setEditor(editor);
        editor.getTextField().setColumns(12);
        return spinner;
    }

    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd HH:mm:ss");
        spinner.setEditor(editor);
        editor.getTextField().setColumns(20);
        return spinner;
    }

    private void refreshConditionPanel() {
        FilterType selectedType = getSelectedFilterType();
        if (selectedType == null) {
            return;
        }

        CardLayout optionsLayout = (CardLayout) conditionOptionsPanel.getLayout();
        optionsLayout.show(conditionOptionsPanel, selectedType.name());
        logicOperatorComboBox.setEnabled(selectedType != FilterType.SORT);

        DataType columnType = getSelectedColumnType();
        showCard(searchInputsPanel, getSearchCard(columnType));
        showCard(rangeInputsPanel, getRangeCard(columnType));
        showCard(topNInputsPanel, getNCard(columnType));
        showCard(bottomNInputsPanel, getNCard(columnType));
    }

    private void showCard(JPanel panel, String cardName) {
        CardLayout layout = (CardLayout) panel.getLayout();
        layout.show(panel, cardName);
    }

    private FilterType getSelectedFilterType() {
        int selectedIndex = filterTypeComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= FilterType.values().length) {
            return null;
        }
        return FilterType.values()[selectedIndex];
    }

    private DataType getSelectedColumnType() {
        return table.getColumnType(columnComboBox.getSelectedIndex());
    }

    private String getSearchCard(DataType columnType) {
        return switch (columnType) {
            case INTEGER, DOUBLE -> CARD_NUMERIC;
            case DATE -> CARD_DATE;
            case BOOLEAN -> CARD_BOOLEAN;
            case EMPTY -> CARD_STRING;
            case STRING -> {
                boolean hasCategories = StatisticService.hasColumnCategories(table, columnComboBox.getSelectedIndex());
                if (hasCategories) {

                    List<String> categories = StatisticService.getColumnCategories(table,
                            columnComboBox.getSelectedIndex());
                    searchSelectComboBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));

                    yield CARD_CATEGORY;
                } else {
                    yield CARD_STRING;
                }
            }
        };
    }

    private String getRangeCard(DataType columnType) {
        return switch (columnType) {
            case STRING, EMPTY -> CARD_STRING;
            case INTEGER, DOUBLE -> CARD_NUMERIC;
            case DATE -> CARD_DATE;
            case BOOLEAN -> CARD_UNSUPPORTED;
        };
    }

    private String getNCard(DataType columnType) {
        return switch (columnType) {
            case STRING, EMPTY -> CARD_STRING;
            case INTEGER, DOUBLE -> CARD_NUMERIC;
            case DATE -> CARD_DATE;
            case BOOLEAN -> CARD_UNSUPPORTED;
        };
    }

    private JPanel buildConditionsListPanel() {
        conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(conditionsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                null,
                Lang.get("filter.added_conditions"),
                TitledBorder.LEFT,
                TitledBorder.TOP));
        scrollPane.setPreferredSize(new Dimension(0, 180));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildButtonBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton cancelButton = new JButton(Lang.get("filter.cancel"));
        cancelButton.addActionListener(event -> {
            result.clear();
            dispose();
        });

        JButton confirmButton = new JButton(Lang.get("filter.confirm"));
        confirmButton.addActionListener(event -> dispose());

        panel.add(cancelButton);
        panel.add(confirmButton);
        return panel;
    }

    private void addCurrentFilter() {
        FilterType selectedType = getSelectedFilterType();

        int selectedColumnIndex = columnComboBox.getSelectedIndex();
        String selectedColumnName = columnNames.get(selectedColumnIndex);
        DataType selectedColumnType = getSelectedColumnType();
        FilterLogic selectedLogic = logicOperatorComboBox.getSelectedIndex() == 0 ? FilterLogic.AND : FilterLogic.OR;

        if (selectedType == null)
            return;

        Filter filter = null;
        String description = null;

        try {
            switch (selectedType) {
                case SEARCH -> {
                    switch (selectedColumnType) {
                        case INTEGER, DOUBLE -> {
                            double numericValue = readDoubleSpinner(searchNumericSpinner);
                            filter = Filter.search(selectedColumnIndex, numericValue);
                            description = selectedColumnName + " = " + numericValue;
                        }
                        case DATE -> {
                            Timestamp dateValue = readTimestampSpinner(searchDateSpinner);
                            filter = Filter.search(selectedColumnIndex, dateValue);
                            description = selectedColumnName + " = " + dateValue;
                        }
                        case BOOLEAN -> {
                            boolean value = searchBooleanComboBox.getSelectedIndex() == 0;
                            filter = new Filter(selectedColumnIndex);
                            filter.add(new FilterCondition(FilterOperator.EQUAL, String.valueOf(value)));
                            description = selectedColumnName + " = " + value;
                        }
                        case STRING, EMPTY -> {
                            if (searchSelectComboBox.getItemCount() > 0) {
                                String category = (String) searchSelectComboBox.getSelectedItem();
                                filter = Filter.equals(selectedColumnIndex, category);
                                description = selectedColumnName + " = \"" + category + "\"";
                            } else {
                                String searchTerm = searchTextField.getText().trim();
                                if (searchTerm.isEmpty()) {
                                    showValidationError(Lang.get("filter.validation.search_term"));
                                    return;
                                }
                                filter = Filter.search(selectedColumnIndex, searchTerm);
                                description = selectedColumnName + " LIKE \"" + searchTerm + "\"";
                            }
                        }
                    }
                }
                case RANGE -> {
                    switch (selectedColumnType) {
                        case STRING, EMPTY -> {
                            int minLength = readIntegerSpinner(rangeLengthMinSpinner);
                            int maxLength = readIntegerSpinner(rangeLengthMaxSpinner);
                            maxLength = Math.max(maxLength, minLength);
                            minLength = Math.min(minLength, maxLength);
                            filter = Filter.byRange(selectedColumnIndex, minLength, maxLength);
                            description = "LENGTH(" + selectedColumnName + ") BETWEEN " + minLength + " AND "
                                    + maxLength;
                        }
                        case INTEGER, DOUBLE -> {
                            double minValue = readDoubleSpinner(rangeNumericMinSpinner);
                            double maxValue = readDoubleSpinner(rangeNumericMaxSpinner);
                            maxValue = Math.max(maxValue, minValue);
                            minValue = Math.min(minValue, maxValue);
                            filter = Filter.byRange(selectedColumnIndex, minValue, maxValue);
                            description = selectedColumnName + " BETWEEN " + minValue + " AND " + maxValue;
                        }
                        case DATE -> {
                            Timestamp minDate = readTimestampSpinner(rangeDateMinSpinner);
                            Timestamp maxDate = readTimestampSpinner(rangeDateMaxSpinner);
                            if (maxDate.before(minDate)) {
                                Timestamp temp = minDate;
                                minDate = maxDate;
                                maxDate = temp;
                            }
                            filter = Filter.byRange(selectedColumnIndex, minDate, maxDate);
                            description = selectedColumnName + " BETWEEN " + minDate + " AND " + maxDate;
                        }
                        case BOOLEAN -> {
                            return;
                        }
                    }
                }
                case TOP_N -> {
                    switch (selectedColumnType) {
                        case STRING, EMPTY -> {
                            int length = readIntegerSpinner(topNLengthSpinner);
                            if (length <= 0) {
                                showValidationError(Lang.get("filter.validation.invalid_number"));
                                return;
                            }
                            filter = Filter.topN(selectedColumnIndex, length);
                            description = selectedColumnName + " > " + length;
                        }
                        case INTEGER, DOUBLE -> {
                            double value = readDoubleSpinner(topNNumericSpinner);
                            filter = Filter.topN(selectedColumnIndex, value);
                            description = selectedColumnName + " > " + value;
                        }
                        case DATE -> {
                            Timestamp value = readTimestampSpinner(topNDateSpinner);
                            filter = Filter.topN(selectedColumnIndex, value);
                            description = selectedColumnName + " > " + value;
                        }
                        case BOOLEAN -> {
                            return;
                        }
                    }
                }
                case BOTTOM_N -> {
                    switch (selectedColumnType) {
                        case STRING, EMPTY -> {
                            int length = readIntegerSpinner(bottomNLengthSpinner);
                            if (length <= 0) {
                                showValidationError(Lang.get("filter.validation.invalid_number"));
                                return;
                            }
                            filter = Filter.bottomN(selectedColumnIndex, length);
                            description = selectedColumnName + " < " + length;
                        }
                        case INTEGER, DOUBLE -> {
                            double value = readDoubleSpinner(bottomNNumericSpinner);
                            filter = Filter.bottomN(selectedColumnIndex, value);
                            description = selectedColumnName + " < " + value;
                        }
                        case DATE -> {
                            Timestamp value = readTimestampSpinner(bottomNDateSpinner);
                            filter = Filter.bottomN(selectedColumnIndex, value);
                            description = selectedColumnName + " < " + value;
                        }
                        case BOOLEAN -> {
                            return;
                        }
                    }
                }
                case SHOW_EMPTY -> {
                    filter = Filter.showEmpty(selectedColumnIndex);
                    description = selectedColumnName + " IS NULL";
                }
                case HIDE_EMPTY -> {
                    filter = Filter.hideEmpty(selectedColumnIndex);
                    description = selectedColumnName + " IS NOT NULL";
                }
                case SORT -> {
                    boolean ascending = sortDirectionComboBox.getSelectedIndex() == 0;
                    filter = Filter.sort(selectedColumnIndex, ascending);
                    description = "ORDER BY " + selectedColumnName + (ascending ? " ASC" : " DESC");
                }
            }
        } catch (ParseException ex) {
            showValidationError(Lang.get("filter.validation.invalid_number"));
            return;
        }

        if (filter == null)
            return;

        if (selectedType != FilterType.SORT) {
            filter.setOperator(selectedLogic);
        }

        result.add(filter);
        addConditionRow(description, filter);
    }

    private void addConditionRow(String description, Filter filter) {
        FilterRow row = new FilterRow(this, description, filter);
        conditionRows.add(row);
        conditionsPanel.add(row.getPanel());
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    public void removeConditionRow(FilterRow row) {
        result.remove(row.getFilter());
        conditionRows.remove(row);
        conditionsPanel.remove(row.getPanel());
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    private int readIntegerSpinner(JSpinner spinner) throws ParseException {
        spinner.commitEdit();
        Object value = spinner.getValue();
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new ParseException("Invalid integer", 0);
    }

    private double readDoubleSpinner(JSpinner spinner) throws ParseException {
        spinner.commitEdit();
        Object value = spinner.getValue();
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        throw new ParseException("Invalid number", 0);
    }

    private Timestamp readTimestampSpinner(JSpinner spinner) throws ParseException {
        spinner.commitEdit();
        Object value = spinner.getValue();
        if (value instanceof Date date) {
            return new Timestamp(date.getTime());
        }
        throw new ParseException("Invalid date", 0);
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, Lang.get("filter.validation.error"), JOptionPane.WARNING_MESSAGE);
    }

    public List<Filter> getResult() {
        return result;
    }

    public static List<Filter> show(Frame parent, DataTable table) {
        FilterDialog dialog = new FilterDialog(parent, table);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}