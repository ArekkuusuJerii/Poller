package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;

public class Respuesta implements ISerializable {

    //transient does not save to JSON
    transient int id;
    //Serialized
    @SerializedName("respuesta")
    public String texto;

    public Respuesta(String texto) {
        this.texto = texto;
    }

    @Override
    public void write() {

    }

    @Override
    public void read() {

    }
}
