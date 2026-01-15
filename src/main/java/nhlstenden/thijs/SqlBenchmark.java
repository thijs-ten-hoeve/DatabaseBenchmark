package nhlstenden.thijs;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlBenchmark {
    private Connection connection;
    public void init() {
        connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/digital_twin?" +
                            "user=root&password=root&rewriteBatchedStatements=true&useSSL=false&allowPublicKeyRetrieval=true"
            );


            connection.getMetaData().getDatabaseProductVersion();
            System.out.println("MYSQL: OK");

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            System.exit(1);
        }

        try (Statement statement = connection.createStatement()) {
            // Drop DB if exists
            statement.executeUpdate("DROP DATABASE IF EXISTS dt_benchmark");
            statement.executeUpdate("CREATE DATABASE dt_benchmark");
            statement.executeUpdate("USE dt_benchmark");

            // Create table
            statement.executeUpdate("""
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public double insert(List<Bouwblok> list) {
        double time;
        final String sql = "INSERT INTO bouwblokken (type, x, y, z, width, depth, height) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            long t0 = System.nanoTime();   // ---- start timing ----

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
            connection.commit();

            long t1 = System.nanoTime();
            double seconds = (t1 - t0) / 1_000_000_000.0;
            time = seconds; // 1_000_000_000 ns in 1 s
            double throughput = list.size() / seconds;

            System.out.printf("%d bouwblokken opgeslagen in %.3f seconden.%n",
                    list.size(), seconds, throughput);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return time;
    }

    public double retrieve() {
        double time;
        final String sql = "SELECT type, x, y, z, width, depth, height FROM bouwblokken";

        try (Statement stmt = connection.createStatement()) {

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

    public double modify() {
        double time;
        final String sql = "UPDATE bouwblokken SET type = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            long t0 = System.nanoTime(); // ---- start timing ----

            // Set the new type
            ps.setString(1, "vrijstaand");
            int updatedRows = ps.executeUpdate(); // updates all rows
            connection.commit();

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
