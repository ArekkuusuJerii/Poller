package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

import java.util.ArrayList;
import java.util.List;

public class Pregunta {

    //transient does not save to JSON
    transient public int id;
    //Serialized
    @SerializedName("pregunta")
    public String texto;
    @SerializedName("tipo")
    public Tipo tipo;
    @SerializedName("respuestas")
    public List<Respuesta> respuestas = new ArrayList<>();

    public Pregunta(String texto, Tipo tipo) {
        this.texto = texto;
        this.tipo = tipo;
    }

    public void create(Encuesta parent) {
        int id = PollImpl.createQuestion(this.texto, this.tipo, parent.token, this.id);
        if(id > 0) {
            this.id = id;
            respuestas.forEach(r -> r.create(this));
        }
    }

    public void read() {

    }
}
