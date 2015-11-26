package com.webapp.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class VerifyImage
{

    public VerifyImage()
    {
        width = 70;
        height = 19;
        length = 4;
        type = 4;
        random = new Random();
    }

    public int getHeight()
    {
        return height;
    }

    public int getLength()
    {
        return length;
    }

    public int getType()
    {
        return type;
    }

    public int getWidth()
    {
        return width;
    }

    public void setHeight(int i)
    {
    }

    public void setLength(int i)
    {
    	length = i;
    }

    public void setType(int i)
    {
    }

    public void setWidth(int i)
    {
    }

    public String createValue()
    {
        StringBuffer strbuf = new StringBuffer();
        String temp = "";
        int itmp = 0;
        for(int i = 0; i < getLength(); i++)
        {
            switch(random.nextInt(5))
            {
            case 1: // '\001'
                itmp = random.nextInt(26) + 65;
                temp = String.valueOf((char)itmp);
                break;

            case 2: // '\002'
                itmp = random.nextInt(26) + 65;
                temp = String.valueOf((char)itmp);
                break;

            default:
                itmp = random.nextInt(10) + 48;
                temp = String.valueOf((char)itmp);
                break;
            }
            strbuf.append(temp);
        }

        return strbuf.toString();
    }

    public Color getRandomColor(int fc, int bc)
    {
        if(fc > 255)
            fc = 200;
        if(bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public void drawRandomLines(Graphics2D g, int lineCount)
    {
        for(int i = 0; i < lineCount; i++)
        {
            int xs = random.nextInt(getWidth());
            int ys = random.nextInt(getHeight());
            int xe = xs + random.nextInt(getWidth() / 8);
            int ye = ys + random.nextInt(getHeight() / 8);
            g.drawLine(xs, ys, xe, ye);
        }

    }

    public void drawRandomString(String value, Graphics2D g)
    {
        for(int i = 0; i < value.length(); i++)
        {
            g.setColor(Color.DARK_GRAY);
            g.drawString((new StringBuilder()).append("").append(value.charAt(i)).toString(), 18 * i + 5, 16);
        }

    }

    public void writeImage(OutputStream stream, String value, String type)
        throws IOException
    {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), 4);
        Graphics2D g = image.createGraphics();
        Font myFont = new Font("Arial", 1, 18);
        g.setFont(myFont);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GRAY);
        drawRandomLines(g, 20);
        drawRandomString(value, g);
        g.dispose();
        ImageIO.write(image, "JPEG", stream);
    }

    private int width;
    private int height;
    private int length;
    private int type;
    private Random random;
}
