package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Pregunta implements ISerializable {

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

    @Override
    public void write() {

    }

    @Override
    public void read() {

    }
}
