package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.repository.PollImpl;

import java.util.ArrayList;
import java.util.List;

public class Poll {

    //transient does not save to JSON
    transient public String token;
    //Serialized
    @SerializedName("titulo")
    public String title;
    @SerializedName("activar")
    public boolean active;
    @SerializedName("periodo")
    public String term;
    @SerializedName("preguntas")
    public List<Question> questions = new ArrayList<>();

    public String create() {
        String out = PollImpl.createPoll(this.title, this.active, this.term, this.token);
        if(!out.isEmpty()) {
            this.token = out;
            questions.forEach(p -> p.create(token));
        }
        return token;
    }

    public void overwrite() {
        PollImpl.createPoll(this.title, this.active, this.term, this.token);
    }

    public static Poll read(String token) {
        return PollImpl.readPoll(token);
    }
}
