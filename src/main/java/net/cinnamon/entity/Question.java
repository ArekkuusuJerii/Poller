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

    public void create(String token) {
        if(text == null || kind == null) return;
        int id = PollImpl.createQuestion(this.text, this.kind, token, this.id);
        if(answers != null && !answers.isEmpty()) {
            if (id > 0) {
                this.id = id;
                answers.forEach(r -> r.create(id));
            }
        }
    }

    public enum Kind {
        SINGLE,
        MULTIPLE,
        OPEN;

        public int id() {
            return ordinal() + 1;
        }

        public static Kind get(int id) {
            return id <= values().length && id > 0 ? values()[id - 1] : Kind.OPEN;
        }
    }
}
