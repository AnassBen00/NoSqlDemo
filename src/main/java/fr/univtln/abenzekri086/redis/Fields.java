package fr.univtln.abenzekri086.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Fields {
    private List<String> directors;
    private String release_date;
    private double rating;
    private List<String> genres;
    private String image_url;
    private String plot;
    private String title;
    private int rank;
    private int running_time_secs;
    private List<String> actors;
    private int year;


}
