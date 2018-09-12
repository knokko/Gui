/* 
 * The MIT License
 *
 * Copyright 2018 20182191.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.knokko.gui.render;

import java.awt.Color;
import java.awt.Graphics;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.texture.GuiTexture;

public class AWTGuiRenderer implements GuiRenderer {
	
	private Graphics g;
	
	private int minX;
	private int minY;
	//private int maxX; this field is never used
	private int maxY;
	private int deltaX;
	private int deltaY;
	
	public void set(Graphics graphics, int minX, int minY, int maxX, int maxY){
		this.g = graphics;
		this.minX = minX;
		this.minY = minY;
		//this.maxX = maxX;
		this.maxY = maxY;
		deltaX = maxX - minX;
		deltaY = maxY - minY;
	}
	
	public void setGraphics(Graphics newGraphics){
		g = newGraphics;
	}

	public GuiRenderer getArea(float minX, float minY, float maxX, float maxY) {
		return new RelativeGuiRenderer.Static(this, minX, minY, maxX, maxY);
	}

	public void renderTexture(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		g.drawImage(texture.getImage(), this.minX + Math.round(deltaX * minX), this.maxY - Math.round(deltaY * maxY), this.minX + Math.round(deltaX * maxX), this.maxY - Math.round(deltaY * minY), texture.getMinX(), texture.getMinY(), texture.getMaxX(), texture.getMaxY(), null);
	}

	public void fill(GuiColor color, float minX, float minY, float maxX, float maxY) {
		g.setColor(new Color(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF()));
		g.fillRect(this.minX + Math.round(deltaX * minX), this.maxY - Math.round(deltaY * maxY), Math.round(deltaX * (maxX - minX)), Math.round(deltaY * (maxY - minY)));
	}

	public void clear(GuiColor color) {
		g.setColor(new Color(color.getRedF(), color.getGreenF(), color.getBlueF()));
		g.fillRect(minX, minY, deltaX, deltaY);
	}
}