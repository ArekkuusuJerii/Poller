package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

public class Answer {

    //transient does not save to JSON
    transient int id;
    //Serialized
    @SerializedName("respuesta")
    public String text;

    public Answer(String text) {
        this.text = text;
    }

    public void create(Question parent) {
        int id = PollImpl.createAnswer(this.text, parent.id, this.id);
        if(id > 0) {
            this.id = id;
        }
    }

    public void read() {

    }
}
