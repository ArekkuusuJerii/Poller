package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

public class Respuesta {

    //transient does not save to JSON
    transient int id;
    //Serialized
    @SerializedName("respuesta")
    public String texto;

    public Respuesta(String texto) {
        this.texto = texto;
    }

    public void create(Pregunta parent) {
        int id = PollImpl.createAnswer(this.texto, parent.id, this.id);
        if(id > 0) {
            this.id = id;
        }
    }

    public void read() {

    }
}
