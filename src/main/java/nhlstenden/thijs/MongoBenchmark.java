package nhlstenden.thijs;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import nhlstenden.thijs.Bouwblok;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import java.util.ArrayList;
import java.util.List;

public class MongoBenchmark {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public void init() {
        String connectionString = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.5.10";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        // Silence driver logs
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        try {
            mongoClient = MongoClients.create(settings);

            // Ping to confirm connection
            database = mongoClient.getDatabase("dt_benchmark");
            database.runCommand(new Document("ping", 1));
            System.out.println("MONGO: OK");

            // Get collection (will be created if it doesn't exist)
            collection = database.getCollection("bouwblokken");

            // Optional: clear previous data for benchmarking
            collection.drop();

        } catch (MongoException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public double insert(List<Bouwblok> list) {
        long t0 = System.nanoTime();

        List<Document> docs = new ArrayList<>();
        for (Bouwblok b : list) {
            Document doc = new Document("type", b.getType())
                    .append("x", b.getX())
                    .append("y", b.getY())
                    .append("z", b.getZ())
                    .append("width", b.getWidth())
                    .append("depth", b.getDepth())
                    .append("height", b.getHeight());
            docs.add(doc);
        }

        collection.insertMany(docs);

        long t1 = System.nanoTime();
        double seconds = (t1 - t0) / 1_000_000_000.0;
        double throughput = list.size() / seconds;

        System.out.printf("%d bouwblokken opgeslagen in %.3f seconden.",
                list.size(), seconds, throughput);

        return seconds;
    }

    public double retrieve() {
        long t0 = System.nanoTime();

        List<Bouwblok> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Bouwblok b = new Bouwblok(
                        doc.getString("type"),
                        doc.getInteger("x"),
                        doc.getInteger("y"),
                        doc.getInteger("z"),
                        doc.getInteger("width"),
                        doc.getInteger("depth"),
                        doc.getInteger("height")
                );
                list.add(b);
            }
        }

        long t1 = System.nanoTime();
        double seconds = (t1 - t0) / 1_000_000_000.0;
        double throughput = list.size() / seconds;

        System.out.printf("%d bouwblokken opgehaald in %.3f seconden.",
                list.size(), seconds, throughput);

        return seconds;
    }

    public double modify() {
        long t0 = System.nanoTime();

        // Update all documents
        UpdateResult result = collection.updateMany(new Document(), Updates.set("type", "vrijstaand"));

        long t1 = System.nanoTime();
        double seconds = (t1 - t0) / 1_000_000_000.0;
        double throughput = result.getModifiedCount() / seconds;

        System.out.printf("%d bouwblokken aangepast in %.3f seconden.",
                result.getModifiedCount(), seconds, throughput);

        return seconds;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
}
