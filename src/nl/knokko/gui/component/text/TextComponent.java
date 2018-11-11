/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
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
 *******************************************************************************/
package nl.knokko.gui.component.text;

import java.awt.Color;
import java.awt.Font;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class TextComponent extends AbstractGuiComponent {
	
	public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(0, 0, 0, 0);
	public static final Color DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 0);
	public static final Font DEFAULT_FONT = new Font("TimesRoman", 0, 20);
	
	protected static int round(double number){
		return (int) Math.round(number);
	}
	
	protected GuiTexture texture;
	
	protected String text;
	protected TextBuilder.Properties properties;
	
	public TextComponent(String text, TextBuilder.Properties properties){
		this.text = text;
		this.properties = properties;
	}
	
	protected void updateTexture(){
		texture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, properties));
	}
	
        @Override
	public void init() {
		updateTexture();
	}

        @Override
	public void update() {}

        @Override
	public void render(GuiRenderer renderer) {
		renderer.renderTexture(texture, 0, 0, 1, 1);
	}

        @Override
	public void click(float x, float y, int button) {}

        @Override
	public void clickOut(int button) {}

        @Override
	public boolean scroll(float amount) {
		return false;
	}

        @Override
	public void keyPressed(int keyCode) {}
	
        @Override
	public void keyPressed(char character) {}

        @Override
	public void keyReleased(int keyCode) {}
	
	public void setText(String newText){
		text = newText;
		updateTexture();
	}
	
	/**
	 * Set the text without changing the texture.
	 * @param newText The new text for this component
	 */
	public void setDirectText(String newText) {
		text = newText;
	}
	
	public String getText(){
		return text;
	}
	
	public TextBuilder.Properties getProperties(){
		return properties;
	}
	
	public void setProperties(TextBuilder.Properties newProperties){
		properties = newProperties;
		updateTexture();
	}
}