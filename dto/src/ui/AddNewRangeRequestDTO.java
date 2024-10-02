package ui;

public class AddNewRangeRequestDTO {
    private final String from;
    private final String to;
    private final String name;

    public AddNewRangeRequestDTO(String name,String from, String to) {
        this.from = from;
        this.to = to;
        this.name = name;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getName() {
        return name;
    }
}
