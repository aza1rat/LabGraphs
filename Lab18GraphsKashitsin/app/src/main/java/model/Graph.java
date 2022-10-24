package model;

public class Graph {
    public int id;
    public boolean isDirectable;
    public String name;

    public Graph(int id, int directable, String name)
    {
        this.id = id;
        if (directable == 1)
            this.isDirectable = true;
        else
            this.isDirectable = false;
        this.name = name;
    }

    public String toString()
    {
        String text = id + ") " + name + "\n";
        if (isDirectable)
            return text + "Ориентированный граф";
        else
            return text + "Неориентированный граф";
    }
}
