package fr.univtln.abenzekri086.memcached;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.abenzekri086.entities.Fields;
import fr.univtln.abenzekri086.entities.Movie;
import lombok.extern.log4j.Log4j;
import net.spy.memcached.*;
import net.spy.memcached.internal.OperationFuture;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Log4j
public class AppDemo {

    public static void main(String[] args) {
        try {
            createCluster();
            /*add();
            getObject("tt1538403");
            prepend("tt1538403");
            delete("tt1538403");
            cas("tt1817273");
            getStats();*/
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public static void createCluster() throws IOException {
        // Set the IP addresses and ports of each memcached node
        String[] servers = { "192.168.1.100:11211", "192.168.1.101:11212", "192.168.1.102:11213" };

        // Create a connection to the memcached cluster
        MemcachedClient memcachedClient = new MemcachedClient(new ConnectionFactoryBuilder().setDaemon(true).setFailureMode(FailureMode.Retry).build(), AddrUtil.getAddresses(Arrays.asList(servers)));

        Movie movie = new Movie();
        movie.setId("tt1115899");

        // Set a value in the cache
        memcachedClient.set("tt1115899", 100, movie);

        // Retrieve a value from the cache
        Object value = memcachedClient.get("tt1115899");

        // Delete a value from the cache
        memcachedClient.delete("tt1115899");

        // Close the connection to the cluster
        memcachedClient.shutdown();
    }

    /**
     * cette methode permet d'ajouter un ensemble de donnees recupere depuis un fichier json
     */
    public static void add() throws IOException {
        // Pour répartir la charge entre plusieurs serveurs
        /*
        List<InetSocketAddress> addresses = Arrays.asList(
                new InetSocketAddress("server1", 11211),
                new InetSocketAddress("server2", 11211),
                new InetSocketAddress("server3", 11211)
        );
        MemcachedClient client = new MemcachedClient(addresses);
        */

        // cree le MemcachedClient
        MemcachedClient client = new MemcachedClient(new InetSocketAddress("localhost", 11211));

        // vider les donnees
        client.flush();

        List<Movie> movies = new ArrayList<>();
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();

        try (JsonParser parser = factory.createParser(new File("src/main/resources/movies.json"))) {
            parser.setCodec(mapper);
            while (parser.nextToken() != null) {
                movies.add(parser.readValueAs(Movie.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parcourir la liste d'objets et stocker chaque objet dans memcached
        for (Movie movie : movies) {
            // définir un délai d'expiration en secondes lors de l'enregistrement d'une valeur en cache 3600
            client.set(movie.getId(), 3600, movie);
        }

        // fermeture de client
        client.shutdown();
    }

    /**
     * cette methode permet de recuperer une donnee en passant la cle qui correspond comme parametre
     */
    public static void getObject(String key) throws IOException, ExecutionException, InterruptedException {
        // Cree le MemcachedClient
        MemcachedClient client = new MemcachedClient(new InetSocketAddress("localhost", 11211));

        // Creation de ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();

        // verifier la donnee dans le cache
        Future<Object> future = client.asyncGet(key);
        if (future != null) {
            // donnee trouver dans le cache on peut l'utiliser
            System.out.println(future.get().toString());

        } else {
            // la donnee n'a pas ete trouver donc on la recupere de la dataset
            try (JsonParser parser = factory.createParser(new File("src/main/resources/movies.json"))) {
                parser.setCodec(objectMapper);
                while (parser.nextToken() != null) {
                    if (parser.readValueAs(Movie.class).getId().equals(key)){
                        client.add(parser.readValueAs(Movie.class).getId(), 3600, parser.readValueAs(Movie.class));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Fermer le client
        client.shutdown();
    }

    /**
     * cette methode permet de supprimer une donnee en passant la cle qui correspond comme parametre
     */
    public static void delete(String key) throws IOException, ExecutionException, InterruptedException {
        // Create the MemcachedClient
        MemcachedClient client = new MemcachedClient(new InetSocketAddress("localhost", 11211));
        // Delete the data from the cache

        Future<Boolean> future = client.delete(key);

        while (!future.isDone()) {
            // Attendez jusqu'à ce que l'opération soit terminée
            Thread.sleep(100);
        }
        if (future.get()) {
            System.out.println("delete request in done");
        } else {
            System.out.println("delete request failed");
        }

        System.out.println("find request : " + client.get(key));
        // Close the client when you are done
        client.shutdown();
    }


    /**
     * cette methode crée un objet de type Movie et l'assigne à la clé en utilisant la méthode "prepend", ce qui permet d'ajouter de
     * nouvelles données avant les données existantes de la clé.c'est utile pour suivre l'historique des modifications apportées à une clé particulière.*/

    public static void prepend(String key) throws IOException, InterruptedException, ExecutionException {
        // Create the MemcachedClient
        MemcachedClient client = new MemcachedClient(new InetSocketAddress("localhost", 11211));
        // Delete the data from the cache
        Movie movie = new Movie();
        movie.setId("tt1538403");
        Future<Boolean> future = client.prepend(key, movie);

        while (!future.isDone()) {
            // Attendez jusqu'à ce que l'opération soit terminée
            Thread.sleep(100);
        }
        if (future.get()) {
            System.out.println("prepend request in done");
        } else {
            System.out.println("prepend request failed");
        }

        System.out.println("find request : " + client.get(key));
        // Close the client when you are done
        client.shutdown();
    }

    /**
     * permet de mettre à jour une valeur en cache de manière atomique,
     * c'est-à-dire de manière à ce que l'opération soit exécutée en une seule fois et sans interruption.
     * Cela garantit que la valeur en cache ne sera pas modifiée par une autre opération pendant que la méthode CAS est en cours d'exécution.
     **/
    public static void cas(String key) throws IOException, ExecutionException, InterruptedException {
        // Create the MemcachedClient
        MemcachedClient client = new MemcachedClient(new InetSocketAddress("localhost", 11211));
        OperationFuture<CASResponse> response = client.asyncCAS(key, 0, 3600, new Movie(new Fields(),key,"update"));
        if (response.get() != CASResponse.OK) {
            System.out.println("CAS request succeded");

        }else if (response.get() == CASResponse.EXISTS) {
                System.out.println("La valeur en cache a été modifiée");
                long casId = client.gets(key).getCas();
                client.cas(key, casId, 3600, new Movie(new Fields(), key, "update"));
                // La valeur en cache a été modifiée par une autre opération entre-temps
                // réessayer l'opération en utilisant un nouvel identifiant de version
            } else if (response.get() == CASResponse.NOT_FOUND) {
                System.out.println("La valeur en cache n'a pas été trouvée");
                // La valeur en cache n'a pas été trouvée
                // mettre en cache la valeur en utilisant la méthode set()
            }

        client.shutdown();
    }

    public static void getStats() throws IOException {
        MemcachedClient client = new MemcachedClient(new InetSocketAddress("localhost", 11211));
        System.out.println("Stats: " + client.getStats());

        client.shutdown();
    }
}
