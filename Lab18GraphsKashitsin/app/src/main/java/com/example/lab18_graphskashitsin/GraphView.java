package com.example.lab18_graphskashitsin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

import model.Link;
import model.Node;

public class GraphView extends SurfaceView {
    GraphHelper g = new GraphHelper();
    Paint p;
    int selected1 = -1;
    int selected2 = -1;
    int lasthit = -1;
    int selectedLink = -1;
    float rad = 70.0f;
    float halfside = 30.0f;
    float lastX;
    float lastY;

    public void addNode()
    {
        g.addNode(100.0f,100.0f);
        invalidate();
    }

    public void removeSelectedLink()
    {
        if (selectedLink < 0) return;
        g.removeLink(selectedLink);
        selectedLink = -1;
        invalidate();
    }

    public void removeSelectedNodes()
    {
        if (selected1 < 0) return;
        ArrayList<Link> involvedLinks = new ArrayList<Link>();
        for (int i = 0; i < g.link.size();i++)
        {
            Link l = g.link.get(i);
            if (l.a == g.getNode(selected1) || l.b == g.getNode(selected1))
            {
                involvedLinks.add(l);
            }
        }
        g.link.removeAll(involvedLinks);
        g.removeNode(selected1);
        selected1 =-1;
        selected2 =-1;
        invalidate();
    }

    public void setNameOfSelectedNode(String name)
    {
        if (selected1 < 0) return;
        if (name == null) return;
        g.setNodeText(selected1, name);
        invalidate();
    }

    public void setValueOfSelectedLink(String value)
    {
        if (selectedLink < 0) return;
        if (value == null) return;
        float v = 0.0f;
        try {
            v = Float.valueOf(value);
        }
        catch (Exception e)
        {
            return;
        }
        g.setLinkValue(selectedLink,v);
        invalidate();

    }

    public void linkSelectedNodes()
    {
        if (selected1 < 0) return;
        if (selected2 < 0) return;
        if (GraphHelper.isDirectable)
        {
            DirectedlinkSelectedNodes();
            return;
        }
        for (Link l: g.link)
        {
            if (l.a == g.getNode(selected1) || l.a == g.getNode(selected2))
            {
                if (l.b == g.getNode(selected1) || l.b == g.getNode(selected2))
                    return;
            }
        }
        g.addLink(g.getNode(selected1),g.getNode(selected2));
        invalidate();
    }

    public void DirectedlinkSelectedNodes()
    {
        for (Link l: g.link)
        {
            if (l.a == g.getNode(selected1) && l.b == g.getNode(selected2))
                return;
        }
        g.addLink(g.getNode(selected1), g.getNode(selected2));
        invalidate();
        return;
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        p.setAntiAlias(true);
        setWillNotDraw(false);
    }

    public int getLinkAtXY(float x,float y)
    {
        if (GraphHelper.isDirectable)
            return DirectedGetLinkAtXY(x,y);
        for (int i = 0; i < g.link.size();i++)
        {
            Link l = g.link.get(i);
            Node na = l.a;
            Node nb = l.b;
            float bx = (na.x + nb.x) * 0.5f;
            float by = (na.y + nb.y) * 0.5f;
            if (x >= bx - halfside && x <=bx + halfside && y>= by - halfside && y <= by + halfside)
                return i;
        }
        return -1;
    }

    public int DirectedGetLinkAtXY(float x, float y)
    {
        for (int i = 0; i < g.link.size();i++)
        {
            Link l = g.link.get(i);
            Node na = l.a;
            Node nb = l.b;
            float bx = (na.x + nb.x) * 0.5f;
            float by = (na.y + nb.y) * 0.5f;
            if (g.Reverse(l) != null)
            {
                by += 40f;
                if (x >= bx - halfside && x <=bx + halfside && y>= by - halfside && y <= by + halfside)
                    return i;
                by -= 80f;
                if (x >= bx - halfside && x <=bx + halfside && y>= by - halfside && y <= by + halfside)
                {
                    for (int j = 0; j < g.link.size(); j++)
                    {
                        if (g.link.get(j) == g.Reverse(l))
                            return j;
                    }
                }
            }
            if (x >= bx - halfside && x <=bx + halfside && y>= by - halfside && y <= by + halfside)
                return i;
        }
        return -1;
    }


    public int getNodeAtXY(float x, float y)
    {
        for (int i = g.node.size() - 1; i>= 0; i--)
        {
            Node n = g.node.get(i);
            float dx = x - n.x;
            float dy = y - n.y;
            if (dx * dx + dy * dy <= rad * rad) return i;
        }
        return -1;

    }

    public void DrawRect(Canvas canvas, float bx, float by, Link l)
    {
        float x0 = bx - halfside;
        float x1 = bx + halfside;
        float y0 = by - halfside;
        float y1 = by + halfside;
        canvas.drawRect(x0,y0,x1,y1,p);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(32f);
        canvas.drawText(String.valueOf(l.value), bx,by, p);
    }


    private void drawArrow(Paint paint, Canvas canvas, float fromX, float fromY, float toX, float toY)
    {
        float angle = 45,anglerad, radius = 30, lineangle;
        anglerad= (float) (Math.PI*angle/180.0f);
        lineangle= (float) (Math.atan2(toY-fromY,toX-fromX));
        canvas.drawLine(fromX,fromY,toX,toY,paint);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(toX, toY);
        path.lineTo((float)(toX-radius*Math.cos(lineangle - (anglerad / 2.0))),
                (float)(toY-radius*Math.sin(lineangle - (anglerad / 2.0))));
        path.lineTo((float)(toX-radius*Math.cos(lineangle + (anglerad / 2.0))),
                (float)(toY-radius*Math.sin(lineangle + (anglerad / 2.0))));
        path.close();

        canvas.drawPath(path, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) { //доделать
        canvas.drawColor(Color.rgb(255,255,255));
        ArrayList<Link> exceptLinks = new ArrayList<Link>();
        for (int i = 0; i < g.link.size();i++)
        {
            Link l = g.link.get(i);
            Node na = l.a;
            Node nb = l.b;
            if (i == selectedLink)
                p.setColor(Color.argb(128,127,0,255));
            else
                p.setColor(Color.argb(127,0,0,0));
            canvas.drawLine(na.x,na.y,nb.x,nb.y, p);
            float bx = (na.x + nb.x) * 0.5f;
            float by = (na.y + nb.y) * 0.5f;
            if (GraphHelper.isDirectable)
            {
                drawArrow(p,canvas,l.a.x,l.a.y,l.b.x,l.b.y);
            }
            if (GraphHelper.isDirectable && g.Reverse(l) != null)
            {
                if (exceptLinks.contains(l) == false)
                {
                    DrawRect(canvas,bx, by + 40f,l);
                    DrawRect(canvas,bx, by - 40f,g.Reverse(l));
                    exceptLinks.add(g.Reverse(l));
                }

            }
            else {
                DrawRect(canvas, bx, by,l);

            }

        }
        for (int i = 0; i < g.node.size();i++)
        {
            Node n = g.node.get(i);
            p.setStyle(Paint.Style.FILL);
            if (i == selected1)
                p.setColor(Color.argb(50,127,0,255));
            else if (i == selected2) p.setColor(Color.argb(50,255,0,50));
            else
                p.setColor(Color.argb(50,0,127,255));
            canvas.drawCircle(n.x,n.y,rad,p);
            p.setStyle(Paint.Style.STROKE);
            if (i == selected1)
                p.setColor(Color.rgb(127,0,255));
            else if (i == selected2) p.setColor(Color.rgb(255,0,50));
            else
                p.setColor(Color.rgb(0,127,255));

            canvas.drawCircle(n.x,n.y, rad, p);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(48f);
            canvas.drawText(n.text, n.x, n.y + rad + 40, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action)
        {

            case MotionEvent.ACTION_DOWN:
                int i = getNodeAtXY(x,y);
                int j = getLinkAtXY(x,y);
                if (j < 0)
                    selectedLink = -1;
                else
                    selectedLink = j;
                lasthit = i;
                if (i < 0)
                {
                    selected1 = -1;
                    selected2 = -1;
                }
                else
                {
                    if (selected1 >= 0) selected2 = i;
                    else selected1 = i;
                }
                lastX = x;
                lastY = y;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
            {
                if (lasthit >= 0)
                {
                    Node n = g.node.get(lasthit);
                    n.x += x - lastX;
                    n.y += y - lastY;
                    invalidate();
                }
                lastX = x;
                lastY = y;
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
