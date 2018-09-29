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
package nl.knokko.gui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public final class TextBuilder {
	
	protected static int round(double number){
		return (int) Math.round(number);
	}
	
	public static BufferedImage createTexture(String text, Properties p, int width, int height){
		int type;
		if(p.textColor.getAlpha() == 255 && p.backgroundColor.getAlpha() == 255 && p.borderColor.getAlpha() == 255)
			type = BufferedImage.TYPE_INT_RGB;
		else
			type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage image = new BufferedImage(width, height, type);
		int minBX = round(p.borderX * width);
		int minBY = round(p.borderY * height);
		int maxBX = round((1 - p.borderX) * width);
		int maxBY = round((1 - p.borderY) * height);
		float outerX = p.borderX + p.marginX;
		float outerY = p.borderY + p.marginY;
		int minTX = round(outerX * width);
		int minTY = round(outerX * height);
		int maxTX = round((1 - outerY) * width);
		int maxTY = round((1 - outerY) * height);
		int textWidth = maxTX - minTX + 1;
		int textHeight = maxTY - minTY + 1;
		Graphics2D g = image.createGraphics();
		g.setColor(p.borderColor);
		g.fillRect(0, 0, minBX, height);
		g.fillRect(minBX, 0, maxBX - minBX, minBY);
		g.fillRect(maxBX, 0, width - maxBX, height);
		g.fillRect(minBX, maxBY, maxBX - minBX, height - maxBY);
		g.setColor(p.backgroundColor);
		g.fillRect(minBX, minBY, maxBX - minBX, maxBY - minBY);
		g.setColor(p.textColor);
		g.setFont(p.font);
		Rectangle2D bounds = p.font.getStringBounds(text, g.getFontRenderContext());
		if(bounds.getWidth() != 0 && bounds.getHeight() != 0){
			double factorX = textWidth / bounds.getWidth();
			double factorY = textHeight / bounds.getHeight();
			double factor = Math.min(factorX, factorY);
			Font realFont = new Font(p.font.getFontName(), p.font.getSize(), (int) (p.font.getSize() * factor));
			LineMetrics line = realFont.getLineMetrics(text, g.getFontRenderContext());
			g.setFont(realFont);
			Rectangle2D realBounds = realFont.getStringBounds(text, g.getFontRenderContext());
			int textX;
			int textY;
			if(p.horAlignment == HorAlignment.LEFT)
				textX = minTX;
			else if(p.horAlignment == HorAlignment.MIDDLE)
				textX = minTX + round((textWidth - realBounds.getWidth()) / 2);
			else
				textX = maxTX - round(realBounds.getWidth());
			if(p.verAlignment == VerAlignment.UP)
				textY = minTY + round(line.getAscent());
			else if(p.verAlignment == VerAlignment.MIDDLE)
				textY = minTY + round((textHeight - realBounds.getHeight()) / 2 + line.getAscent());
			else
				textY = maxTY - round(realBounds.getHeight() - line.getAscent());
			g.drawString(text, textX, textY);
		}
		g.dispose();
		return image;
	}
	
	public static class Properties {
		
		public static final Font DEFAULT_BUTTON_FONT = new Font("TimesRoman", 0, 30);
		public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
		
		public static Properties createLabel(Color textColor, Color backgroundColor) {
			return createText(DEFAULT_BUTTON_FONT, textColor, backgroundColor);
		}
		
		public static Properties createLabel(Color textColor) {
			return createLabel(textColor, TRANSPARENT);
		}
		
		public static Properties createLabel() {
			return createLabel(Color.BLACK);
		}
		
		public static Properties createText(Font font, Color textColor, Color backgroundColor){
			return new Properties(font, textColor, backgroundColor, backgroundColor, HorAlignment.LEFT, VerAlignment.MIDDLE, 0, 0, 0, 0);
		}
		
		public static Properties createButton(Font font, Color backgroundColor, Color borderColor, Color textColor){
			return new Properties(font, textColor, backgroundColor, borderColor, HorAlignment.MIDDLE, VerAlignment.MIDDLE, 0.1f, 0.1f, 0.1f, 0.1f);
		}
		
		public static Properties createButton(Color backgroundColor, Color borderColor, Color textColor){
			return createButton(DEFAULT_BUTTON_FONT, backgroundColor, borderColor, textColor);
		}
		
		public static Properties createButton(Color backgroundColor, Color borderColor){
			return createButton(backgroundColor, borderColor, Color.BLACK);
		}
		
		public final Font font;
		
		public final Color textColor;
		public final Color backgroundColor;
		public final Color borderColor;
		
		public final HorAlignment horAlignment;
		public final VerAlignment verAlignment;
		
		public final float borderX;
		public final float borderY;
		public final float marginX;
		public final float marginY;
		
		public Properties(Font font, Color textColor, Color backgroundColor, Color borderColor, HorAlignment horAlignment, 
				VerAlignment verAlignment, float borderX, float borderY, float marginX, float marginY){
			this.font = font;
			this.textColor = textColor;
			this.backgroundColor = backgroundColor;
			this.borderColor = borderColor;
			
			this.horAlignment = horAlignment;
			this.verAlignment = verAlignment;
			
			this.borderX = borderX;
			this.borderY = borderY;
			this.marginX = marginX;
			this.marginY = marginY;
		}
	}
	
	public static enum HorAlignment {
		
		LEFT,
		MIDDLE,
		RIGHT;
	}
	
	public static enum VerAlignment {
		
		UP,
		MIDDLE,
		DOWN;
	}
}