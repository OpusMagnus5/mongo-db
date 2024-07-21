package pl.bodzioch.damian;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        MongoClient mongoClient = createMongoClient();
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

    private static void logInfo(MongoClient mongoClient) {
        mongoClient.listDatabases().into(new ArrayList<>()).stream()
                .map(Document::toJson)
                .forEach(System.out::println);
    }
}