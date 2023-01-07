package fr.univtln.abenzekri086.entities;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Fields implements Serializable {
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
