package samwang.anki.model;

import java.util.List;

public class OutputDTO {
    private List<String> files;
    private String question;

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
