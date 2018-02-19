package net.cinnamon.entity;

import com.google.gson.annotations.SerializedName;
import net.cinnamon.controller.Menu;
import net.cinnamon.repository.PollImpl;

import java.util.ArrayList;
import java.util.List;

public class Poll {

    //transient does not save to JSON
    transient public String token;
    transient public int owner;
    //Serialized
    @SerializedName("titulo")
    public String title;
    @SerializedName("activar")
    public boolean active;
    @SerializedName("preguntas")
    public List<Question> questions = new ArrayList<>();

    public Poll(String title) {
        this.title = title;
    }

    public String create() {
        String out = PollImpl.createPoll(this.title, this.active, this.token);
        if(!out.isEmpty()) {
            this.token = out;
            this.owner = Menu.getId();
            questions.forEach(p -> p.create(this));
        }
        return token;
    }

    public void read() {

    }
}
