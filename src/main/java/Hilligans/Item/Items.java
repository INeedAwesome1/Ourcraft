package Hilligans.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class Items {

    public static final ArrayList<Item> ITEMS = new ArrayList<>();
    public static final HashMap<String, Item> HASHED_ITEMS = new HashMap<>();


    static short id = 0;
    public static short getNextId() {
        short val = id;
        id++;
        return val;
    }


}
