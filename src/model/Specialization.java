package model;

public class Specialization {
    private final String name;

    public Specialization(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Specialization))
            return false;
        return name.equals( ((Specialization)obj).name );
    }
}
