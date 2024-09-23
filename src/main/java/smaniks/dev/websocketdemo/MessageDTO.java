package smaniks.dev.websocketdemo;

public class MessageDTO {
    private String content;

    public MessageDTO() {}

    public MessageDTO(String s) {
        this.content = s;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
