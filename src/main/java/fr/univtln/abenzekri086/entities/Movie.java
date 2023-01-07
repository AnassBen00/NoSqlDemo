package fr.univtln.abenzekri086.entities;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Movie implements Serializable {
    private Fields fields;
    private String id;
    private String type;

}
