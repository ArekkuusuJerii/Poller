package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Encuesta implements ISerializable {

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

    @Override
    public void write() {

    }

    @Override
    public void read() {

    }
}
