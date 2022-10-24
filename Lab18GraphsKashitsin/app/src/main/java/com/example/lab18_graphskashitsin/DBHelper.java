package com.example.lab18_graphskashitsin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import model.Graph;
import model.Link;
import model.Node;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sqlCreate = "CREATE TABLE Graph(" +
                "id INTEGER PRIMARY KEY," +
                "isDirectable INTEGER," +
                "name TEXT);";
        sqlDB.execSQL(sqlCreate);
        sqlCreate = "CREATE TABLE Node(" +
                "id INTEGER PRIMARY KEY," +
                "graph INTEGER," +
                "x REAL," +
                "y REAL," +
                "text TEXT," +
                "FOREIGN KEY(graph) REFERENCES Graph(id));";
        sqlDB.execSQL(sqlCreate);
        sqlCreate = "CREATE TABLE Link(" +
                "id INTEGER PRIMARY KEY," +
                "graph INTEGER," +
                "a INTEGER," +
                "b INTEGER," +
                "value REAL," +
                "FOREIGN KEY (graph) REFERENCES Graph(id)," +
                "FOREIGN KEY(a) REFERENCES node (id)," +
                "FOREIGN KEY(b) REFERENCES node (id));";
        sqlDB.execSQL(sqlCreate);
    }

    public int getMaxId(String table)
    {
        String sql = "SELECT MAX(id) FROM " + table + ";";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return 0;
    }

    public void getAllGraph(ArrayList<Graph> graphs)
    {
        String sql = "SELECT * FROM Graph";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
        {
            do {
                Graph graph = new Graph(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2)
                );
                graphs.add(graph);
            }
            while (cursor.moveToNext());
        }
    }

    public void addNotDirectlyGraph(int id, String name)
    {
        String sql = "INSERT INTO Graph VALUES ("+ id +", 0, '" + name +"');";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }
    public void addDirectlyGraph(int id, String name)
    {
        String sql = "INSERT INTO Graph VALUES ("+ id +", 1, '" + name +"');";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }
    public void renameGraph(int id, String newName)
    {
        String sql = "UPDATE Graph SET name = '" + newName +"' WHERE id = "+ id +";";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    public void delete()
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String sql = "DELETE FROM Link WHERE graph = " + GraphHelper.idGraph + ";";
        sqlDB.execSQL(sql);
        sql = "DELETE FROM Node WHERE graph = " + GraphHelper.idGraph + ";";
        sqlDB.execSQL(sql);
    }

    public void deleteGraph(int graphId)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String sql = "DELETE FROM Link WHERE graph = " + graphId + ";";
        sqlDB.execSQL(sql);
        sql = "DELETE FROM Node WHERE graph = " + graphId + ";";
        sqlDB.execSQL(sql);
        sql = "DELETE FROM Graph WHERE id = " + graphId + ";";
        sqlDB.execSQL(sql);
    }

    public void load(int idGraph)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        String sql = "SELECT * FROM Node WHERE graph = " + idGraph + ";";
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
        {
            do {
                ids.add(cursor.getInt(0));
                Node node = new Node(
                    cursor.getFloat(2),
                    cursor.getFloat(3)
                );
                if (cursor.getString(4) != null)
                    node.text = cursor.getString(4);
                GraphHelper.node.add(node);
            }
            while (cursor.moveToNext());
        }

        sql = "SELECT * FROM Link WHERE graph = " + idGraph + ";";
        cursor = sqlDB.rawQuery(sql, null);
        if (cursor.moveToFirst())
        {
            do {
                int schet = -1;
                Link link;
                Node a = null;
                Node b = null;
                int idA = cursor.getInt(2);
                int idB = cursor.getInt(3);
                for (int i : ids) {
                    schet++;
                    if (i == idA)
                        a = GraphHelper.node.get(schet);
                    if (i == idB)
                        b = GraphHelper.node.get(schet);
                }
                link = new Link(a,b);
                link.value = cursor.getFloat(4);
                GraphHelper.link.add(link);
            }
            while (cursor.moveToNext());

        }
    }

    public void copy(int graphId, String graphName, boolean graphIsDirect)
    {
        int[][] addedNodes;
        int copiedId = getMaxId("Graph") + 1;
        if (graphIsDirect)
            addDirectlyGraph(copiedId, graphName + " (скопировано)");
        else
            addNotDirectlyGraph(copiedId, graphName + " (скопировано)");

        String sql = "SELECT * FROM Node WHERE graph = " +graphId+ ";";
        String sqlInsert;
        SQLiteDatabase sqlDB = getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql, null);
        addedNodes = new int[cursor.getCount()][2];
        if (cursor.moveToNext())
        {

            int schet = -1;
            do {
                schet++;

                addedNodes[schet][0] = cursor.getInt(0);
                if (cursor.getString(4) == null)
                    sqlInsert = "INSERT INTO Node (id,graph,x,y) VALUES ("+(getMaxId("Node") + 1 )+","+copiedId+","+cursor.getFloat(2)+","+cursor.getFloat(3)+");";
                else
                    sqlInsert = "INSERT INTO Node VALUES ("+(getMaxId("Node") + 1)+","+copiedId+","+cursor.getFloat(2)+","+cursor.getFloat(3)+",'"+cursor.getString(4)+"');";

                sqlDB.execSQL(sqlInsert);
                addedNodes[schet][1] = getMaxId("Node");
            }
            while (cursor.moveToNext());
        }

        sql = "SELECT * FROM Link WHERE graph = " +graphId+ ";";
        cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToNext())
        {
            do {
                int linkA = 0;
                int linkB = 0;
                for (int i = 0; i < addedNodes.length; i++)
                {
                    if (addedNodes[i][0] == cursor.getInt(2))
                        linkA = addedNodes[i][1];
                    if (addedNodes[i][0] == cursor.getInt(3))
                        linkB = addedNodes[i][1];
                }
                sqlInsert = "INSERT INTO Link VALUES ("+(getMaxId("Link") + 1)+","+copiedId+","+linkA+","+linkB+","+cursor.getFloat(4)+");";

                sqlDB.execSQL(sqlInsert);
            }
            while (cursor.moveToNext());
        }
    }

    public void save(int idNode, int idLink, ArrayList<Node> nodes, ArrayList<Link> links, int idGraph)
    {
        int idNowNode = idNode;
        SQLiteDatabase sqlDB = getWritableDatabase();
        int [][] linkedNodes = new int[links.size()][2];
        for (int j = 0; j < nodes.size(); j++)
        {
            for (int i = 0; i < links.size(); i++)
            {
                if (links.get(i).a == nodes.get(j))
                {
                    linkedNodes[i][0] = j + 1;

                }
                if (links.get(i).b == nodes.get(j))
                {
                    linkedNodes[i][1] = j + 1;
                }

            }
            Node node = nodes.get(j);
            idNowNode++;
            String sql;
            if (node.text.isEmpty())
                sql = "INSERT INTO Node (id,graph,x,y) VALUES ("+idNowNode+","+idGraph+","+node.x+","+node.y+");";
            else
                sql = "INSERT INTO Node VALUES ("+idNowNode+","+idGraph+","+node.x+","+node.y+",'"+node.text+"');";
            sqlDB.execSQL(sql);

        }
        for (int i = 0; i < links.size(); i++)
        {
            Link link = links.get(i);
            idLink++;
            String sql = "INSERT INTO Link VALUES ("+idLink+","+idGraph+","+(linkedNodes[i][0] + idNode)+","+(linkedNodes[i][1] + idNode)+","+link.value+");";
            sqlDB.execSQL(sql);
        }
        return;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
