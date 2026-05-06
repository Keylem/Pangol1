package fr.univrennes.istic.l2gen.application.core.filter;

import fr.univrennes.istic.l2gen.application.core.table.DataTable;

public final class FilterBuilder {

    public static StringBuilder base(String base, DataTable table, boolean orderBy) {
        StringBuilder sql = new StringBuilder(base);
        sql.append(" ").append(table.getSQLName());

        int filterCount = table.getFilters().size();
        if (filterCount > 0) {
            int initialLength = sql.length();
            String whereClause = " WHERE ";
            sql.append(whereClause);

            FilterSort sort = FilterSort.NONE;
            String sortColumn = null;
            for (int i = 0; i < filterCount; i++) {
                Filter filter = table.getFilters().get(i);

                String columnName = table.getSQLColumnName(filter.getColumnIndex());
                sql.append(filter.getSQL(columnName));

                if (i < filterCount - 1 && filter.hasConditions()) {
                    if (filter.getOperator() == FilterLogic.AND) {
                        sql.append(" AND ");
                    } else {
                        sql.append(" OR ");
                    }
                }

                if (filter.getSort() != FilterSort.NONE) {
                    sortColumn = table.getSQLColumnName(filter.getColumnIndex());
                    sort = filter.getSort();
                }
            }

            if (sql.length() == initialLength + whereClause.length()) {
                sql.setLength(initialLength);
            }

            if (orderBy && sort != FilterSort.NONE) {
                sql.append(" ORDER BY ").append(sortColumn);
                if (sort == FilterSort.ASCENDING) {
                    sql.append(" ASC");
                } else {
                    sql.append(" DESC");
                }
            }
        }

        return sql;
    }

    public static String count(DataTable table) {
        return base("SELECT COUNT(*) FROM", table, false).toString();
    }

    public static String query(DataTable table, long limit, long offset) {
        StringBuilder sql = base("SELECT * FROM", table, true);
        if (limit != -1) {
            sql.append(" LIMIT ").append(limit);
        }

        if (offset != -1) {
            sql.append(" OFFSET ").append(offset);
        }
        return sql.toString();
    }
}