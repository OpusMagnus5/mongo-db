package pl.bodzioch.damian;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public class Main {

    static MongoClient mongoClient = createMongoClient();
    static ClientSession session = mongoClient.startSession();
    static MongoCollection<Document> collection = getCollection();

    public static void main(String[] args) {
        logInfo(mongoClient);
    }

    //tworzenie clienta / połączenia do bazy / clustra
    private static MongoClient createMongoClient() {
        ConnectionString connectionString =
                new ConnectionString("mongodb+srv://szaddaj:password@cluster0.zyfwz8j.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        return MongoClients.create(settings);
    }

    // wyciąganie kolekcji
    private static MongoCollection<Document> getCollection() {
        MongoDatabase database = mongoClient.getDatabase("name_of_database");
        return database.getCollection("name_of_collection");
    }

    private static void logInfo(MongoClient mongoClient) {
        mongoClient.listDatabases().into(new ArrayList<>()).stream()
                .map(Document::toJson)
                .forEach(System.out::println);
    }

    // tworzenie dokumentu do zapisu
    private static Document exampleNewDocument() {
        return new Document("_id", new ObjectId())
                .append("name", "Damian")
                .append("number", 12345)
                .append("date", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("address", new Document().append("city", "Katowice").append("street", "Albatrosów")); // zagnieżdżony dokument
    }

    // zapis pojedynczego dokumentu
    private static void insertDocument() {
        InsertOneResult insertOneResult = collection.insertOne(exampleNewDocument());
        BsonValue insertedId = insertOneResult.getInsertedId();
    }

    // zapis wielu dokumentów
    private static void insertManyDocuments() {
        List<Document> documents = Stream.of(1, 2, 3).map(element -> exampleNewDocument()).toList();
        InsertManyResult insertManyResult = collection.insertMany(documents);
        List<BsonValue> ids = insertManyResult.getInsertedIds().values().stream().toList();
        BsonObjectId objectId = ids.getFirst().asObjectId();
    }

    // szukanie dokumentów po filtrze
    private static void findWithFilter() {
        Bson filters = Filters.and(gte("balance", 1000), eq("account_type", "checking"));
        FindIterable<Document> documents = collection.find(filters);
        String json = documents.first().toJson();
    }

    // zwracanie pierwszego spełniającego filtr
    private static void findFirstWithFilter() {
        Bson filters = Filters.and(gte("balance", 1000), eq("account_type", "checking"));
        Document document = collection.find(filters).first();
        String json = document.toJson();
    }

    private static void updateOne() {
        Bson accountIdFilter = eq("account_id", 12345);
        Bson updates = Updates.combine(set("account_status", "active"), inc("balance", 100));
        UpdateResult result = collection.updateOne(accountIdFilter, updates);
        result.getModifiedCount();
    }

    private static void updateMany() {
        Bson accountIdFilter = eq("account_id", 12345);
        Bson updates = Updates.combine(set("account_status", "active"));
        UpdateResult result = collection.updateMany(accountIdFilter, updates);
    }

    private static void deleteOne() {
        Bson query = eq("account_id", 12345);
        DeleteResult result = collection.deleteOne(query);
        result.getDeletedCount();
    }

    private static void deleteMany() {
        Bson query = eq("account_id", 12345);
        DeleteResult result = collection.deleteMany(query);
        result.getDeletedCount();
    }

    private static void transaction() {
        TransactionBody<String> transaction = () -> {
            Bson query1 = eq("account_id", 12345);
            Bson update1 = Updates.combine(set("account_status", "active"), inc("balance", 100));

            Bson query2 = eq("account_id", 33333);
            Bson update2 = Updates.combine(set("account_status", "active"), inc("balance", 200));

            collection.updateOne(session, query1, update1);
            collection.updateOne(session, query2, update2);
            return "Completed";
        };

        try {
            session.withTransaction(transaction);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            session.close();
        }
    }
}