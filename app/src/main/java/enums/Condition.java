package enums;

public enum Condition {
    TO (0),
    FROM (0),
    CC (0),
    SUBJECT (0);

    private int value;

    Condition(int value) {
        this.value = value;
    }


    public int getValue() {
        return this.value;
    }
}
