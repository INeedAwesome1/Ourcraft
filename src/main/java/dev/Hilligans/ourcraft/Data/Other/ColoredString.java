package dev.Hilligans.ourcraft.Data.Other;

import org.joml.Vector4f;

import java.util.ArrayList;

public class ColoredString {

    public String string;

    public ArrayList<Vector4f> colors = new ArrayList<>();

    public ColoredString(String string) {

    }

    public void append(char Char, Vector4f color) {
        string += Char;
        colors.add(color);
    }

}
