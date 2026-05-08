package fr.univrennes.istic.l2gen.application.core.services.stats;

import fr.univrennes.istic.l2gen.application.Pangol1;
import fr.univrennes.istic.l2gen.application.core.TaskStatus;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.config.Log;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;

import org.duckdb.DuckDBConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class StatisticService {

    private StatisticService() {
    }

    private static String formatDouble(double value) {
        return String.format("%,.2f", value);
    }

    public static boolean hasCategories(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isCategorical()) {
            return false;
        }
        String taskId = Pangol1.getController().addTask(
                Lang.get("task.stats.count_categories", table.getColumnName(columnIndex)),
                TaskStatus.PENDING);
        String query = String.format(
                "SELECT COUNT(DISTINCT %s) FROM %s WHERE %s IS NOT NULL",
                table.getSQLColumnName(columnIndex),
                table.getSQLName(),
                table.getSQLColumnName(columnIndex));

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {

            Pangol1.getController().updateTaskStatus(taskId, TaskStatus.RUNNING);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                int distinctCount = resultSet.getInt(1);
                Pangol1.getController().updateTaskStatus(taskId, TaskStatus.SUCCESS);
                return distinctCount > 0 && distinctCount < table.getRowCount();
            }

        } catch (Exception e) {
            Pangol1.getController().updateTaskStatus(taskId, TaskStatus.FAILED);
            Log.debug("Failed to execute query: " + query, e);
        }

        Pangol1.getController().updateTaskStatus(taskId, TaskStatus.SUCCESS);
        return false;

    }

    public static List<String> getCategories(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isCategorical()) {
            return List.of();
        }

        String taskId = Pangol1.getController().addTask(
                Lang.get("task.stats.fetch_categories", table.getColumnName(columnIndex)),
                TaskStatus.PENDING);
        String query = String.format(
                "SELECT DISTINCT %s FROM %s WHERE %s IS NOT NULL ORDER BY %s",
                table.getSQLColumnName(columnIndex),
                table.getSQLName(),
                table.getSQLColumnName(columnIndex),
                table.getSQLColumnName(columnIndex));

        List<String> categories = new ArrayList<>();
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {
            Pangol1.getController().updateTaskStatus(taskId, TaskStatus.RUNNING);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String value = resultSet.getString(1);
                if (!resultSet.wasNull()) {
                    categories.add(value);
                }
            }

            Pangol1.getController().updateTaskStatus(taskId, TaskStatus.SUCCESS);
        } catch (Exception e) {
            Pangol1.getController().updateTaskStatus(taskId, TaskStatus.FAILED);
            Log.debug("Failed to execute query: " + query, e);
        }
        return categories;
    }

    public static OptionalDouble getMinAsDouble(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric()) {
            return OptionalDouble.empty();
        }
        String query = String.format(
                "SELECT MIN(%s) FROM %s WHERE %s IS NOT NULL",
                table.getSQLColumnName(columnIndex),
                table.getSQLName(),
                table.getSQLColumnName(columnIndex));

        return executeDoubleQuery(query);
    }

    public static OptionalDouble getMaxAsDouble(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric()) {
            return OptionalDouble.empty();
        }
        String query = String.format(
                "SELECT MAX(%s) FROM %s WHERE %s IS NOT NULL",
                table.getSQLColumnName(columnIndex),
                table.getSQLName(),
                table.getSQLColumnName(columnIndex));

        return executeDoubleQuery(query);
    }

    public static Optional<Timestamp> getMinAsTimestamp(DataTable table, int columnIndex) {
        if (table.getColumnType(columnIndex) != DataType.DATE) {
            return Optional.empty();
        }
        String query = String.format(
                "SELECT MIN(%s) FROM %s",
                table.getSQLColumnName(columnIndex),
                table.getSQLName());

        Optional<String> result = executeStringQuery(query);
        return result.map(Timestamp::valueOf);
    }

    public static Optional<Timestamp> getMaxAsTimestamp(DataTable table, int columnIndex) {
        if (table.getColumnType(columnIndex) != DataType.DATE) {
            return Optional.empty();
        }
        String query = String.format(
                "SELECT MAX(%s) FROM %s",
                table.getSQLColumnName(columnIndex),
                table.getSQLName());

        Optional<String> result = executeStringQuery(query);
        return result.map(Timestamp::valueOf);
    }

    public static Optional<String> getActionAsString(DataTable table, int columnIndex, StatisticOp action) {
        String query;
        String taskId = Pangol1.getController().addTask(
                Lang.get("task.stats.base", action.getDisplayName(), table.getColumnName(columnIndex)),
                TaskStatus.PENDING);
        switch (table.getColumnType(columnIndex)) {
            case DOUBLE, INTEGER, BOOLEAN -> {
                query = String.format(
                        "SELECT %s(val) FROM (" +
                                "SELECT %s AS val FROM %s) WHERE val IS NOT NULL",
                        action.name(), table.getSQLColumnName(columnIndex), table.getSQLName());
            }
            case DATE -> {
                if (action != StatisticOp.MIN && action != StatisticOp.MAX) {
                    return Optional.empty();
                }

                query = String.format(
                        "SELECT %s(%s) FROM %s",
                        action.name(),
                        table.getSQLColumnName(columnIndex),
                        table.getSQLName());

                String val = executeStringQuery(query).orElse("");
                return val.isEmpty() ? Optional.empty() : Optional.of(val);
            }
            default -> {
                query = String.format(
                        "SELECT %s(LENGTH(%s)) FROM %s WHERE %s IS NOT NULL",
                        action.name(),
                        table.getSQLColumnName(columnIndex),
                        table.getSQLName(),
                        table.getSQLColumnName(columnIndex));
            }
        }

        Pangol1.getController().updateTaskStatus(taskId, TaskStatus.RUNNING);

        Double val = executeDoubleQuery(query).orElse(Double.NaN);

        Pangol1.getController().updateTask(taskId,
                Lang.get("task.stats.base_done", action.getDisplayName(), table.getColumnName(columnIndex)),
                TaskStatus.SUCCESS);

        if (Double.isNaN(val) || Double.isInfinite(val)) {
            return Optional.empty();
        } else {
            return Optional
                    .of(formatDouble(val) + (table.getColumnType(columnIndex) == DataType.STRING ? " length" : ""));
        }
    }

    public static OptionalDouble getCorrelation(DataTable table, int columnIndexX, int columnIndexY) {
        if (!table.getColumnType(columnIndexX).isNumeric()
                || !table.getColumnType(columnIndexY).isNumeric()) {
            return OptionalDouble.empty();
        }

        String query = String.format(
                "SELECT CORR(x, y) FROM (" +
                        "SELECT %s AS x, %s AS y FROM %s) " +
                        "WHERE x IS NOT NULL AND y IS NOT NULL",
                table.getSQLColumnName(columnIndexX), table.getSQLColumnName(columnIndexY), table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble getCoefficientOfVariation(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric()) {
            return OptionalDouble.empty();
        }

        String query = String.format(
                "SELECT CASE WHEN AVG(val) = 0 THEN NULL " +
                        "ELSE STDDEV_SAMP(val) / AVG(val) END " +
                        "FROM (SELECT %s AS val FROM %s) WHERE val IS NOT NULL",
                table.getSQLColumnName(columnIndex), table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble getSkewness(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric())
            return OptionalDouble.empty();

        String query = String.format(
                "SELECT SKEWNESS(val) FROM (" +
                        "SELECT %s AS val FROM %s) WHERE val IS NOT NULL",
                table.getSQLColumnName(columnIndex), table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble getInterquartileRange(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric())
            return OptionalDouble.empty();

        String query = String.format(
                "SELECT QUANTILE_CONT(val, 0.75) - QUANTILE_CONT(val, 0.25) " +
                        "FROM (SELECT %s AS val FROM %s) WHERE val IS NOT NULL",
                table.getSQLColumnName(columnIndex), table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble getNullRate(DataTable table, int columnIndex) {
        String col = table.getSQLColumnName(columnIndex);

        String query = String.format(
                "SELECT COUNT(*) FILTER (WHERE %s IS NULL) * 1.0 / NULLIF(COUNT(*), 0) FROM %s",
                col, table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble getCardinalityRatio(DataTable table, int columnIndex) {
        String col = table.getSQLColumnName(columnIndex);

        String query = String.format(
                "SELECT COUNT(DISTINCT %s) * 1.0 / NULLIF(COUNT(*), 0) FROM %s",
                col, table.getSQLName());

        return executeDoubleQuery(query);
    }

    private static OptionalDouble executeDoubleQuery(String query) {
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                double value = resultSet.getDouble(1);
                return resultSet.wasNull() ? OptionalDouble.empty() : OptionalDouble.of(value);
            }

        } catch (Exception e) {
            Log.debug("Failed to execute query: " + query, e);
        }
        return OptionalDouble.empty();
    }

    private static Optional<String> executeStringQuery(String query) {
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String value = resultSet.getString(1);
                return resultSet.wasNull() ? Optional.empty() : Optional.of(value);
            }

        } catch (Exception e) {
            Log.debug("Failed to execute query: " + query, e);
        }
        return Optional.empty();
    }

    public static String computeSummary(DataTable currentTable, int columnIndex) {
        StringBuilder summary = new StringBuilder();
        for (StatisticOp action : StatisticOp.values()) {
            Optional<String> result = getActionAsString(currentTable, columnIndex, action);
            if (result.isPresent()) {
                summary.append(action.getDisplayName()).append(": ").append(result.get()).append("\n");
            } else {
                summary.append(action.getDisplayName()).append(": N/A\n");
            }
        }

        OptionalDouble nullRate = getNullRate(currentTable, columnIndex);
        if (nullRate.isPresent()) {
            summary.append("Null rate: ").append(formatDouble(nullRate.getAsDouble() * 100)).append("%\n");
        } else {
            summary.append("Null rate: N/A\n");
        }
        summary.append("Type: ").append(currentTable.getColumnType(columnIndex)).append("\n");
        summary.append("Native Type: ").append(currentTable.getColumnType(columnIndex).toSQL()).append("\n");

        return summary.toString();
    }
}