package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

import java.util.ArrayList;
import java.util.List;

public class Encuesta {

    //transient does not save to JSON
    transient public String token;
    transient public int propetario;
    //Serialized
    @SerializedName("titulo")
    public String titulo;
    @SerializedName("activar")
    public boolean activa;
    @SerializedName("preguntas")
    public List<Pregunta> preguntas = new ArrayList<>();

    public Encuesta(String titulo) {
        this.titulo = titulo;
    }

    public String create() {
        String out = PollImpl.createPoll(this.titulo, this.activa, this.token);
        if(!out.isEmpty()) {
            this.token = out;
            preguntas.forEach(p -> p.create(this));
        }
        return token;
    }

    public void read() {

    }
}
