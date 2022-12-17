package fr.univtln.abenzekri086.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Movie implements Serializable {
    private Fields fields;
    private String id;
    private String type;

}
