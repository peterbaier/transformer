package co.elliptic.baier.transformer.enums;

public enum LineType {
    tx(5),
    bk(3),
    fx(2);

    private final int lineSize;

    LineType(int lineSize) {
        this.lineSize = lineSize;
    }

    public int getLineSize() {
        return this.lineSize;
    }
}