package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

import java.util.ArrayList;
import java.util.List;

public class Question {

    //transient does not save to JSON
    transient public int id;
    //Serialized
    @SerializedName("pregunta")
    public String text;
    @SerializedName("tipo")
    public Kind kind;
    @SerializedName("respuestas")
    public List<Answer> answers = new ArrayList<>();

    public Question(String text, Kind kind) {
        this.text = text;
        this.kind = kind;
    }

    public void create(Poll parent) {
        int id = PollImpl.createQuestion(this.text, this.kind, parent.token, this.id);
        if(id > 0) {
            this.id = id;
            answers.forEach(r -> r.create(this));
        }
    }

    public void read() {

    }

    public enum Kind {
        SINGLE,
        MULTIPLE,
        OPEN;
        
        public int id() {
            return ordinal() + 1;
        }
    }
}
