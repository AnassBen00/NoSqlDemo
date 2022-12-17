package fr.univtln.abenzekri086.redis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class redisExemple {

    public static void main(String[] args) throws IOException {
        // Connect to Redis server running on localhost
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "redis://127.0.0.1:6379");


        /// Jedis implements Closeable. Hence, the jedis instance will be auto-closed after the last statement.
        try (Jedis jedis = pool.getResource()) {
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

            // Parcourir la liste d'objets et stocker chaque objet dans Redis
            for (Movie movie : movies) {
                jedis.set(movie.getId().getBytes(), mapper.writeValueAsString(movie).getBytes());
            }

            // Recuperer la valeur d'une cle

            String m = jedis.get("tt1538403");
            System.out.println(m);

            // Modifier la valeur d'une cle
            Movie movie = new Movie();
            movie.setId("tt1538403");
            jedis.set("tt1538403".getBytes(), mapper.writeValueAsString(movie).getBytes());
            System.out.println(jedis.get("tt1538403"));

            // supprimer un element
            jedis.del("tt1538403");

        }

        pool.close();
        // Close the connection to the Redis server

    }

}