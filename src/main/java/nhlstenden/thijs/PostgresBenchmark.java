package nhlstenden.thijs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PostgresBenchmark {
    private Connection conn;
    public void init() {
        final String adminUrl = "jdbc:postgresql://localhost:5432/postgres";
        final Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "root");

        try (Connection adminConn = DriverManager.getConnection(adminUrl, props);
             Statement adminStmt = adminConn.createStatement()) {

            // force drop existing connections
            adminStmt.execute("""
            SELECT pg_terminate_backend(pid)
            FROM pg_stat_activity
            WHERE datname = 'dt_benchmark'
              AND pid <> pg_backend_pid();
        """);

            adminStmt.execute("DROP DATABASE IF EXISTS dt_benchmark");
            adminStmt.execute("CREATE DATABASE dt_benchmark");


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // now connect to the new DB
        final String dbUrl = "jdbc:postgresql://localhost:5432/dt_benchmark";

        try {
            conn = DriverManager.getConnection(dbUrl, props);
            System.out.println("POSTGRES: OK");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                CREATE TABLE bouwblokken (
                    type VARCHAR(64),
                    x INT,
                    y INT,
                    z INT,
                    width INT,
                    depth INT,
                    height INT
                );
            """);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public double insert(List<Bouwblok> list) {
        double time;
        final String sql = "INSERT INTO bouwblokken (type, x, y, z, width, depth, height) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                long t0 = System.nanoTime();

                for (Bouwblok b : list) {
                    ps.setString(1, b.getType());
                    ps.setInt(2, b.getX());
                    ps.setInt(3, b.getY());
                    ps.setInt(4, b.getZ());
                    ps.setInt(5, b.getWidth());
                    ps.setInt(6, b.getDepth());
                    ps.setInt(7, b.getHeight());
                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit();

                long t1 = System.nanoTime();
                double seconds = (t1 - t0) / 1_000_000_000.0;  // 1_000_000_000 ns in 1 s
                time = seconds;
                double throughput = list.size() / seconds;

                System.out.printf("%d bouwblokken opgeslagen in %.3f seconden.%n",
                        list.size(), seconds, throughput);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return time;
    }

    public double retrieve() {
        double time;
        final String sql = "SELECT type, x, y, z, width, depth, height FROM bouwblokken";

        try (Statement stmt = conn.createStatement()) {

            long t0 = System.nanoTime(); // ---- start timing ----

            ResultSet rs = stmt.executeQuery(sql);

            List<Bouwblok> list = new ArrayList<>();
            while (rs.next()) {
                Bouwblok b = new Bouwblok(
                        rs.getString("type"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z"),
                        rs.getInt("width"),
                        rs.getInt("depth"),
                        rs.getInt("height")
                );
                list.add(b);
            }

            long t1 = System.nanoTime(); // ---- end timing ----
            double seconds = (t1 - t0) / 1_000_000_000.0;
            time = seconds;

            double throughput = list.size() / seconds;

            System.out.printf("%d bouwblokken opgehaald in %.3f seconden.",
                    list.size(), seconds, throughput);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return time;
    }

    public Connection getConnection() {
        return conn;
    }

    public double modify() {
        double time;
        final String sql = "UPDATE bouwblokken SET type = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            long t0 = System.nanoTime(); // ---- start timing ----

            // Set the new type
            ps.setString(1, "vrijstaand");
            int updatedRows = ps.executeUpdate(); // updates all rows
            conn.commit();

            long t1 = System.nanoTime(); // ---- end timing ----
            double seconds = (t1 - t0) / 1_000_000_000.0;
            time = seconds;

            double throughput = updatedRows / seconds;

            System.out.printf("%d bouwblokken aangepast in %.3f seconden.",
                    updatedRows, seconds, throughput);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return time;
    }

}
