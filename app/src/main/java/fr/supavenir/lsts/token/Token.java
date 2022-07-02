package fr.supavenir.lsts.token;

public class Token {
    float high;
    float low;
    float actual;
    String name;

    public Token(float actual,float low, float high, String name) {
        this.actual = actual;
        this.low = low;
        this.high = high;
        this.name = name;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float actual) {
        if (this.high<actual) {
            this.high = actual;
        }
    }

    public float getLow() {
        return low;
    }

    public void setLow(float actual) {
        if (this.low>actual) {
            this.low = actual;
        }
    }

    public float getActual() {
        return actual;
    }

    public void setActual(float actual) {
        this.actual = actual;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return this.name + " - " + this.actual + " - " + this.low + " - " + this.high;
    }
}
