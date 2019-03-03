package com.mach.LightDrive;

public final class Color
{
    public short red;
    public short green;
    public short blue;
    public static final Color RED;
    public static final Color GREEN;
    public static final Color BLUE;
    public static final Color TEAL;
    public static final Color YELLOW;
    public static final Color PURPLE;
    public static final Color WHITE;
    public static final Color OFF;
    
    static {
        RED = new Color(255, 0, 0);
        GREEN = new Color(0, 255, 0);
        BLUE = new Color(0, 0, 255);
        TEAL = new Color(0, 255, 255);
        YELLOW = new Color(255, 255, 0);
        PURPLE = new Color(255, 0, 255);
        WHITE = new Color(255, 255, 255);
        OFF = new Color(0, 0, 0);
    }
    
    public Color() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }
    
    public Color(final short r, final short g, final short b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }
    
    public Color(final int r, final int g, final int b) {
        this.red = (short)r;
        this.green = (short)g;
        this.blue = (short)b;
    }
}
