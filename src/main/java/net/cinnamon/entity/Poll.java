package net.cinnamon.entity;

public class Poll implements ISerializable {

    public String title;
    public String token;

    public Poll(String title) {
        this.title = title;
    }

    @Override
    public void write() {

    }

    @Override
    public void read() {

    }
}
