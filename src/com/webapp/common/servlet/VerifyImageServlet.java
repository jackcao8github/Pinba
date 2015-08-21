package com.webapp.common.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.webapp.common.util.VerifyImage;



public class VerifyImageServlet extends HttpServlet
{

    public VerifyImageServlet()
    {
        isNoCache = false;
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
        type = config.getInitParameter("image-type");
        if(type == null || "".equals(type))
            type = "png";
        try
        {
            length = Integer.parseInt(config.getInitParameter("image-length"));
        }
        catch(Exception e)
        {
            length = 4;
        }
        try
        {
            width = Integer.parseInt(config.getInitParameter("image-width"));
        }
        catch(Exception e)
        {
            width = 80;
        }
        try
        {
            height = Integer.parseInt(config.getInitParameter("image-height"));
        }
        catch(Exception e)
        {
            height = 40;
        }
        isNoCache = "false".equals(config.getInitParameter("image-nocache"));
    }

    public void service(ServletRequest req, ServletResponse res)
        throws ServletException, IOException
    {
        HttpServletRequest hreq = (HttpServletRequest)req;
        HttpServletResponse hres = (HttpServletResponse)res;
        VerifyImage image = new VerifyImage();
        image.setLength(getLength(hreq.getParameter("length")));
        image.setWidth(getWidth(hreq.getParameter("width")));
        image.setHeight(getHeight(hreq.getParameter("height")));
        String value = image.createValue();
        HttpSession session = hreq.getSession(true);
        session.setAttribute("__SESSION_VERIFY__", value);
        hres.setContentType((new StringBuilder()).append("image/").append(type).toString());
        if(isNoCache)
        {
            hres.setHeader("Cache-Control", "no-cache");
            hres.setHeader("Pragma", "no-cache");
            hres.setHeader("Expires", "-1");
        }
        image.writeImage(hres.getOutputStream(), value, type);
    }

    private int getWidth(String width)
    {
        int value = this.width;
        if(width == null || "".equals(width))
            return value;
        try
        {
            value = Integer.parseInt(width);
        }
        catch(Exception e)
        {
            value = this.width;
        }
        return value;
    }

    private int getHeight(String height)
    {
        int value = this.height;
        if(height == null || "".equals(height))
            return value;
        try
        {
            value = Integer.parseInt(height);
        }
        catch(Exception e)
        {
            value = this.height;
        }
        return value;
    }

    private int getLength(String length)
    {
        int value = this.length;
        if(length == null || "".equals(length))
            return value;
        try
        {
            value = Integer.parseInt(length);
        }
        catch(Exception e)
        {
            value = this.length;
        }
        return value;
    }

    private static final long serialVersionUID = 1L;
    private String type;
    private int length;
    private int width;
    private int height;
    private boolean isNoCache;
}
