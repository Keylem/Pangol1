package fr.univrennes.istic.l2gen.application.gui;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import java.awt.Desktop;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.TaskStatus;
import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.config.Log;
import fr.univrennes.istic.l2gen.application.core.services.FileService;
import fr.univrennes.istic.l2gen.application.core.services.TableService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataTableWorkerStatus;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.gui.dialog.filter.FilterDialog;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;

public final class GUIController extends CoreController {
    private static final GUIController instance = new GUIController();

    private MainView mainView;
    private GUIApp app;

    private DataTable currentTable;
    private int loadingIndex = 0;

    public static GUIController getInstance() {
        return instance;
    }

    private GUIController() {
    }

    @Override
    public void onStart() {
        setStatus(Lang.get("status.ready"));

        /// REMOVE THIS LATER ARTIFICIAL DELAY
        if (!Log.DEBUG_MODE) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        ///

        mainView.ready();
        openDefaultTable();
    }

    @Override
    public void onStop() {
        setStatus(Lang.get("status.shutting_down"));
        if (currentTable != null) {
            currentTable.close();
        }

        TableService.saveRecents();
    }

    @Override
    public void onException(Exception e) {
        Log.debug("An exception occurred", e);

        Throwable rootCause = e.getCause() != null ? e.getCause() : e;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    mainView,
                    rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage(),
                    Lang.get("error.exception"),
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public String addTask(String name, TaskStatus status) {
        if (loadingIndex == 0) {
            enableLoading();
        }
        return mainView.getBottomBar().addTask(name, status);
    }

    @Override
    public void updateTask(String taskId, String name, TaskStatus status) {
        mainView.getBottomBar().updateTask(taskId, name, status);
        if (status == TaskStatus.SUCCESS) {
            disableLoading();
        }
    }

    @Override
    public void updateTaskStatus(String taskId, TaskStatus status) {
        mainView.getBottomBar().updateTaskStatus(taskId, status);
        if (status == TaskStatus.SUCCESS) {
            disableLoading();
        }
    }

    private void openDefaultTable() {
        File targetDir = FileService.getAppDataDir();

        /// REMOVE THIS LATER
        String stableURI = "https://www.data.gouv.fr/api/1/datasets/r/99a26050-b94f-4ffc-9eb0-73ed28a895d1";
        ///

        String defaultTable = Config.get().get("settings.startup.default_table_source", stableURI);
        URI parsedDefaultTableUri = null;
        File parsedDefaultTableFile = null;

        try {
            if (defaultTable.startsWith("http://") || defaultTable.startsWith("https://")) {
                parsedDefaultTableUri = URI.create(defaultTable);
            } else {
                parsedDefaultTableFile = new File(defaultTable);
                if (!parsedDefaultTableFile.exists()) {
                    parsedDefaultTableFile = null;
                    parsedDefaultTableUri = URI.create(stableURI);
                }
            }
        } catch (Exception e) {
            try {
                parsedDefaultTableFile = new File(defaultTable);
                parsedDefaultTableUri = null;
            } catch (Exception ex) {
                parsedDefaultTableFile = null;
                parsedDefaultTableUri = URI.create(stableURI);
            }
        }

        final URI defaultTableUri = parsedDefaultTableUri;
        final File defaultTableFile = parsedDefaultTableFile;

        enableLoading();
        if (defaultTableUri != null) {
            setStatus(Lang.get("status.loading_url", defaultTableUri.toString()));
        } else {
            setStatus(Lang.get("status.loading_file", defaultTableFile.getName()));
        }

        new SwingWorker<DataTable, Void>() {
            @Override
            protected DataTable doInBackground() throws Exception {
                List<DataTable> tables = defaultTableUri != null
                        ? TableService.load(defaultTableUri, targetDir)
                        : TableService.load(defaultTableFile, targetDir);
                return tables.isEmpty() ? null : tables.get(0);
            }

            @Override
            protected void done() {
                try {
                    DataTable table = get();
                    if (table != null) {
                        Config.get().put("settings.startup.default_table_source", table.getPath().getAbsolutePath());
                        setTable(table);
                    }
                } catch (Exception e) {
                    onException(e);
                } finally {
                    setStatus(Lang.get("status.ready"));
                    disableLoading();
                }
            }
        }.execute();

    }

    public void setMainView(MainView frame) {
        mainView = frame;
    }

    public void setApp(GUIApp app) {
        this.app = app;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void enableLoading() {
        loadingIndex++;
        mainView.getBottomBar().setLoading(true);
    }

    public void disableLoading() {
        if (loadingIndex > 0) {
            loadingIndex--;
        }
        if (loadingIndex == 0) {
            mainView.getBottomBar().setLoading(false);
        }
    }

    public void setStatus(String status) {
        mainView.getBottomBar().setStatus(status);
    }

    public Optional<DataTable> getTable() {
        return Optional.ofNullable(currentTable);
    }

    public void setTable(DataTable table) {
        enableLoading();
        if (table == null) {
            Log.debug("Attempted to set null table");
            disableLoading();
            return;
        }

        if (currentTable != null) {
            currentTable.close();
        }

        if (table.isClosed()) {
            if (!table.open()) {
                Log.debug("Failed to open table: " + table.getAlias());
                disableLoading();
                return;
            }
        }

        currentTable = table;
        mainView.getTablePanel().open(table);
        mainView.getTablePanel().refresh();
        mainView.getReportPanel().refresh();

        SwingUtilities.invokeLater(() -> {
            mainView.getBottomBar().setTableInfo(
                    table.getAlias(),
                    (int) table.getRowCount(),
                    (int) table.getColumnCount());

            setStatus(Lang.get("status.opening_table", table.getAlias()));
            disableLoading();
        });

    }

    public void onCloseTable() {
        if (currentTable != null) {
            setStatus(Lang.get("status.closing_table",
                    currentTable != null ? currentTable.getAlias() : Lang.get("error.number_na")));

            currentTable.close();
            currentTable = null;
        }

        mainView.getTablePanel().close();
        mainView.getReportPanel().getSettingView().getDataSettingsPanel().refresh();
        mainView.getBottomBar().setTableInfo("", 0, 0);
        mainView.getBottomBar().clearColumnStats();
    }

    public void onOpenFilterDialog() {
        if (currentTable == null) {
            return;
        }

        List<Filter> filters = FilterDialog.show(mainView, currentTable);
        currentTable.clearFilters();
        currentTable.addFilters(filters);
    }

    public void onOpenFileDialog() {
        SystemFileChooser fileChooser = new SystemFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(SystemFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(mainView) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] selectedFiles = fileChooser.getSelectedFiles();
        boolean hasZip = false;
        for (File file : selectedFiles) {
            if (FileService.getExtension(file).equalsIgnoreCase("zip")) {
                hasZip = true;
                break;
            }
        }

        final File targetDir;
        if (hasZip) {
            SystemFileChooser dirChooser = new SystemFileChooser();
            dirChooser.setMultiSelectionEnabled(false);
            dirChooser.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
            dirChooser.setDialogTitle(Lang.get("dialog.select_target_dir"));

            if (dirChooser.showOpenDialog(mainView) != SystemFileChooser.APPROVE_OPTION) {
                return;
            }
            targetDir = dirChooser.getSelectedFile();
        } else {
            targetDir = null;
        }

        enableLoading();
        if (selectedFiles.length == 1) {
            setStatus(Lang.get("status.loading_file", selectedFiles[0].getName()));
        } else {
            setStatus(Lang.get("status.loading_files", selectedFiles.length));
        }

        new SwingWorker<List<DataTable>, DataTableWorkerStatus>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {
                List<DataTable> loadedTables = new ArrayList<>();
                long startTime = System.currentTimeMillis();

                for (File file : selectedFiles) {
                    if (FileService.getExtension(file).equalsIgnoreCase("zip")) {
                        loadedTables.addAll(TableService.load(file, targetDir));
                        continue;
                    }

                    List<DataTable> loadedFiles = TableService.load(file, file.getParentFile());
                    if (loadedFiles.isEmpty()) {
                        continue;
                    }

                    TableService.addRecent(file);
                    loadedTables.addAll(loadedFiles);
                }

                long elapsed = System.currentTimeMillis() - startTime;
                publish(new DataTableWorkerStatus(loadedTables.size(), selectedFiles.length, elapsed));
                return loadedTables;
            }

            @Override
            protected void process(List<DataTableWorkerStatus> chunks) {
                DataTableWorkerStatus status = chunks.get(chunks.size() - 1);
                if (status.totalCount() == 1) {
                    setStatus(Lang.get("status.loaded_file", status.loadedCount(), status.elapsed()));
                } else {
                    setStatus(Lang.get("status.loaded_files", status.loadedCount(), status.totalCount(),
                            status.elapsed()));
                }
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> loadedTables = get();
                    if (loadedTables.size() == 1) {
                        setTable(loadedTables.get(0));
                    } else {
                        getMainView().getTablePanel().refresh();
                    }
                } catch (Exception e) {
                    onException(e);
                } finally {
                    disableLoading();
                }
            }
        }.execute();
    }

    public void onOpenUrlDialog() {
        String input = JOptionPane.showInputDialog(mainView, "URL:");
        if (input == null || input.isBlank()) {
            return;
        }

        URI uri;
        try {
            uri = URI.create(input.trim());
        } catch (Exception e) {
            onException(e);
            return;
        }

        SystemFileChooser dirChooser = new SystemFileChooser();
        dirChooser.setMultiSelectionEnabled(false);
        dirChooser.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle(Lang.get("dialog.select_target_dir"));

        if (dirChooser.showOpenDialog(mainView) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }
        File targetDir = dirChooser.getSelectedFile();

        enableLoading();
        setStatus(Lang.get("status.loading_url", input));

        new SwingWorker<List<DataTable>, DataTableWorkerStatus>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {
                long startTime = System.currentTimeMillis();
                List<DataTable> loadedTables = TableService.load(uri, targetDir);
                long elapsed = System.currentTimeMillis() - startTime;

                publish(new DataTableWorkerStatus(loadedTables.size(), 1, elapsed));
                return loadedTables;
            }

            @Override
            protected void process(List<DataTableWorkerStatus> chunks) {
                DataTableWorkerStatus status = chunks.get(chunks.size() - 1);
                if (status.totalCount() == 1) {
                    setStatus(Lang.get("status.loaded_single_url", input, status.elapsed()));
                } else {
                    setStatus(Lang.get("status.loaded_url", status.loadedCount(), input, status.elapsed()));
                }
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> loadedTables = get();
                    if (loadedTables.size() == 1) {
                        setTable(loadedTables.get(0));
                    } else {
                        getMainView().getTablePanel().refresh();
                    }
                } catch (Exception e) {
                    onException(e);
                } finally {
                    disableLoading();
                }
            }
        }.execute();
    }

    public void onOpenDocDialog() {
        // TODO: open from resources/doc/MANUAL.pdf
        try {
            URI manualUri = getClass().getResource("/doc/MANUAL.pdf").toURI();
            Desktop.getDesktop().browse(manualUri);
        } catch (Exception e) {
            onException(e);
        }

    }

    public void onOpenAboutDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append(Lang.get("about.description")).append("\n");
        sb.append(Lang.get("about.version")).append("\n");
        sb.append(Lang.get("about.developed_by")).append("\n");
        sb.append(" - Jules Garcia (@jules1univ)\n");
        sb.append(" - Paul Gallon (@MarcoPaulot)\n");
        sb.append(" - Elouan Barbier (@Marsu2)\n");
        sb.append(" - Noé Berthelier (@nberthelier)\n");
        sb.append(" - Briac Boitel (@bboitel)\n");
        sb.append(" - Kerem Eylem (@Keylem)\n");
        sb.append(" - Basile Guemene (@Astala-Boom)\n");
        sb.append("\n");
        sb.append(Lang.get("about.license"));

        JOptionPane.showMessageDialog(mainView, sb.toString(), Lang.get("about.title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onLanguageChange(Locale locale) {
        if (locale.equals(Lang.getLocale())) {
            return;
        }

        Lang.setLocale(locale);
        Config.get().put("settings.general.language", locale.toLanguageTag());
        this.restart();
    }

    public void restart() {
        this.app.restart();
    }
}