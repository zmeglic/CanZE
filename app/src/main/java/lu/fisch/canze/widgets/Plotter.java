package lu.fisch.canze.widgets;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import java.util.ArrayList;

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;
import lu.fisch.canze.widgets.Drawable;

/**
 *
 * @author robertfisch
 */
public class Plotter extends Drawable {

    protected ArrayList<Double> values = new ArrayList<>();
    protected ArrayList<String> sids = new ArrayList<>();

    public Plotter() {
        super();
    }

    public Plotter(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // test
    }

    public Plotter(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        this.drawSurface=drawSurface;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addValue(double value)
    {
        values.add(value);
    }

    public void setValue(int index, double value)
    {
        values.set(index, value);
    }

    @Override
    public void setValue(int value) {
        super.setValue(value);
        //addValue(value);
    }

    @Override
    public void draw(Graphics g) {
        // black border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // calculate fill height
        int fillHeight = (int) ((value-min)/(double)(max-min)*(height-1));
        int barWidth = width-Math.max(g.stringWidth(min+""),g.stringWidth(max+""))-10-10;

        // draw the graph
        g.drawRect(x+width-barWidth, y, barWidth, height);
        if(values.size()>0)
        {
            double w = (double) barWidth/values.size();
            double h = (double) getHeight()/(getMax()-getMin()+1);

            double lastX = Double.NaN;
            double lastY = Double.NaN;
            g.setColor(Color.RED);
            for(int i=0; i<values.size(); i++)
            {
                double mx = w/2+i*w;
                double my = getHeight()-(values.get(i)-getMin())*h;
                int rayon = 2;
                g.fillOval(getX()+getWidth()-barWidth+(int)mx-rayon,getY()+(int)my-rayon,2*rayon+1,2*rayon+1);
                if(i>0)
                {
                    g.drawLine(getX()+getWidth()-barWidth+(int)lastX,
                            getY()+(int)lastY,
                            getX()+getWidth()-barWidth+(int)mx,
                            getY()+(int)my);
                }
                lastX=mx;
                lastY=my;
            }
        }

        // draw the ticks
        if(minorTicks>0 || majorTicks>0)
        {
            g.setColor(Color.GRAY);
            int toTicks = minorTicks;
            if(toTicks==0) toTicks=majorTicks;
            double accel = (double)height/((max-min)/(double)toTicks);
            double ax,ay,bx=0,by=0;
            int actual = min;
            int sum = 0;
            for(double i=height; i>=0; i-=accel)
            {
                if(minorTicks>0)
                {
                    ax = x+width-barWidth-5;
                    ay = y+i;
                    bx = x+width-barWidth;
                    by = y+i;
                    g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                }
                // draw majorTicks
                if(majorTicks!=0 && sum % majorTicks == 0) {
                    if(majorTicks>0)
                    {
                        ax = x+width-barWidth-10;
                        ay = y+i;
                        bx = x+width-barWidth;
                        by = y+i;
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                    }

                    // draw String
                    if(showLabels)
                    {
                        String text = (actual)+"";
                        double sw = g.stringWidth(text);
                        bx = x+width-barWidth-16-sw;
                        by = y+i;
                        g.drawString(text, (int)(bx), (int)(by+g.stringHeight(text)*(1-i/height)));
                    }

                    actual+=majorTicks;
                }
                sum+=minorTicks;
            }
        }

        // draw the title
        if(title!=null && !title.equals(""))
        {
            /*g.setColor(Color.BLUE);
            //g.setTextSize(20);
            int tw = g.stringWidth(title);
            int th = g.stringHeight(title);
            //int th = g.stringHeight(title);
            int tx = x; //x+width-barWidth/2-tw/2;
            int ty = y+height; //getY()+getHeight()-8;
            g.rotate(-90, tx, ty);
            g.drawString(title,tx+th/2,ty+th);
            g.rotate(90, tx, ty);*/

            g.setColor(Color.BLUE);
            g.setTextSize(20);
            int tw = g.stringWidth(title);
            int tx = getX()+width-barWidth+8;
            int ty = getY()+g.stringHeight(title)+4;
            g.drawString(title,tx,ty);
        }
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        String sid = field.getSID();

        MainActivity.debug(sid+" --> "+field.getValue());

        int index = sids.indexOf(sid);
        if(index==-1){
            sids.add(sid);
            addValue(field.getValue());
        }
        else setValue(index,field.getValue());
        // only repaint if the last field has been updated
        //if(index==sids.size()-1)
            super.onFieldUpdateEvent(field);
    }
}
