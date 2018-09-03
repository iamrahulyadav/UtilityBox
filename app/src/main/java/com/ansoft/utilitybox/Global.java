package com.ansoft.utilitybox;

import java.util.ArrayList;

/**
 * Created by Abinash on 11/7/2016.
 */
public class Global {

    public static ArrayList<Appdata> list;

    public static ArrayList<Appdata> getList() {
        return list;
    }

    public static void setList(ArrayList<Appdata> list) {
        Global.list = list;
    }
}
