package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

public class Answer {

    //transient does not save to JSON
    transient public int id;
    //Serialized
    @SerializedName("respuesta")
    public String text;

    public void create(int parent) {
        int id = PollImpl.createAnswer(this.text, parent, this.id);
        if(id > 0) {
            this.id = id;
        }
    }

}
