package com.example.lab18_graphskashitsin;

import java.util.ArrayList;

import model.Link;
import model.Node;

public class GraphHelper {
    static int idGraph = 0;
    static boolean isDirectable = true;
    static String name = "Name";
    static ArrayList <Node> node = new ArrayList<Node>();
    static ArrayList <Link> link = new ArrayList<Link>();

    public void addNode(float x, float y)
    {
        node.add(new Node(x,y));
    }

    public void removeNode(int index)
    {
        if (index < 0) return;
        node.remove(index);
    }

    public Node getNode (int index)
    {
        if (index < 0) return null;
        return node.get(index);
    }

    public Link Reverse(Link selected)
    {
        for (Link l : link)
        {
            if (l.b == selected.a)
                if (l.a == selected.b)
                    return l;
        }
        return null;

    }

    public void setNodeText (int index, String name)
    {
        if (index < 0) return;
        Node n = node.get(index);
        n.text = name;
    }

    public void setLinkValue(int index, float value)
    {
        if (index < 0) return;
        Link l = link.get(index);
        l.value = value;
    }

    public void removeLink(int index)
    {
        if (index < 0) return;
        link.remove(index);

    }

    public void addLink(Node a,Node b)
    {
        link.add(new Link(a,b));
    }


}
