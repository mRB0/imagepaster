package imagepaster;

import java.awt.*;
import java.awt.event.*;
//import java.math.*;
//import java.awt.Image.*;

public class Imagebox extends Panel implements MouseListener, MouseMotionListener {
	
	static final long serialVersionUID = 1012021;

	Image img;
	int offs_x, offs_y;
	Point drag_begin;
	
	boolean panning, selecting;
	
	Point selectorect_top, selectorect_bot;
	
	public Imagebox()
	{
		super();
		this.img = null;
		offs_x = offs_y = 0;
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		selecting = panning = false;
		
		selectorect_top = selectorect_bot = null;
	}
	
	public void setImage(Image img)
	{
		this.img = img;
	}
	
	public void paint(Graphics real_g)
	{
		Image offscreen = createImage(getSize().width, getSize().height);
		
		Graphics g = offscreen.getGraphics();
		
		g.setColor(Color.magenta);
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		
		if (this.img != null)
		{
			g.drawImage(this.img, -offs_x, -offs_y, null);
		}
		
		if (selectorect_top != null)
		{
			g.setColor(Color.RED);
			
			int size_x = Math.abs(selectorect_bot.x - selectorect_top.x);
			int size_y = Math.abs(selectorect_bot.y - selectorect_top.y);
			
			
			g.drawRect(Math.min(selectorect_top.x, selectorect_bot.x), Math.min(selectorect_top.y, selectorect_bot.y), size_x, size_y);
			
		}
		
		real_g.drawImage(offscreen, 0, 0, null);
		
		g.dispose();
	}
	
	public void setOffset(Point new_offs)
	{
		this.offs_x += (drag_begin.x - new_offs.x);
		this.offs_y += (drag_begin.y - new_offs.y);
		
		
		if (this.offs_x < 0)
		{
			this.offs_x = 0;
		}
		if (this.offs_y < 0)
		{
			this.offs_y = 0;
		}
		
		
		if (this.offs_x > this.img.getWidth(this) - this.getWidth())
		{
			this.offs_x = this.img.getWidth(this) - this.getWidth();
		}
		if (this.offs_y > this.img.getHeight(this) - this.getHeight())
		{
			this.offs_y = this.img.getHeight(this) - this.getHeight();
		}
	}
	
	/* MouseListener events */
	public void mouseClicked(MouseEvent me)
	{
	}

	public void mouseEntered(MouseEvent me)
	{
	}

	public void mouseExited(MouseEvent me)
	{
	}

	public void mousePressed(MouseEvent me)
	{
		if ((me.getButton() == MouseEvent.BUTTON2 || me.getButton() == MouseEvent.BUTTON3) && this.img != null)
		{
			drag_begin = me.getPoint();
			panning = true;
		}
		else if (me.getButton() == MouseEvent.BUTTON1)
		{
			selectorect_top = selectorect_bot = me.getPoint();
			selecting = true;
			this.repaint();
		}
	}

	public void mouseReleased(MouseEvent me)
	{
		if ((me.getButton() == MouseEvent.BUTTON2 || me.getButton() == MouseEvent.BUTTON3) && this.img != null)
		{
			this.setOffset(me.getPoint());
		
			this.repaint();
			panning = false;
		}
		else if (me.getButton() == MouseEvent.BUTTON1)
		{
			selectorect_top = selectorect_bot = null;
			this.repaint();
			selecting = false;
		}
	}
	
	/* MouseMotionListener events */
	
	public void mouseDragged(MouseEvent me)
	{
		if (panning)
		{
			this.setOffset(me.getPoint());
			this.repaint();
			drag_begin = me.getPoint();
		}
		else if (selecting)
		{
			selectorect_bot = me.getPoint();
			
			/*
			if (selectorect_bot.x < selectorect_top.x)
			{
				selectorect_bot.x = selectorect_top.x;
				selectorect_top.x = me.getPoint().x;
			}
			if (selectorect_bot.y < selectorect_top.y)
			{
				selectorect_bot.y = selectorect_top.y;
				selectorect_top.y = me.getPoint().y;
			}
			*/
			this.repaint();
		}
	}
	
	public void mouseMoved(MouseEvent me)
	{
		
	}
}
