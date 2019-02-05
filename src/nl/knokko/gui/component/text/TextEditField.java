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

import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextEditField extends TextComponent {
	
	protected GuiTexture activeTexture;
	protected Properties activeProperties;
	
	protected boolean active;

	public TextEditField(String text, Properties passiveProperties, Properties activeProperties) {
		super(text, passiveProperties);
		this.activeProperties = activeProperties;
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(active)
			renderer.renderTexture(activeTexture, 0, 0, 1, 1);
		else
			super.render(renderer);
	}
	
	@Override
	protected void updateTexture(){
		updatePassiveTexture();
		updateActiveTexture();
	}
	
	public void setActiveProperties(Properties newProperties){
		activeProperties = newProperties;
		updateActiveTexture();
	}
	
	protected void updatePassiveTexture(){
		texture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, properties));
		state.getWindow().markChange();
	}
	
	protected void updateActiveTexture(){
		activeTexture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, activeProperties));
		state.getWindow().markChange();
	}
	
	@Override
	public void click(float x, float y, int button){
		if(button == MouseCode.BUTTON_LEFT) {
			active = !active;
			state.getWindow().markChange();
		}
	}
	
	@Override
	public void clickOut(int button){
		active = false;
		state.getWindow().markChange();
	}
	
	@Override
	public void keyPressed(char character){
		if(active){
			text += character;
			updateTexture();
		}
	}
	
	@Override
	public void keyPressed(int key){
		if(key == KeyCode.KEY_ESCAPE || key == KeyCode.KEY_ENTER) {
			active = false;
			state.getWindow().markChange();
		}
		if(active){
			if(key == KeyCode.KEY_BACKSPACE && text.length() > 0){
				text = text.substring(0, text.length() - 1);
				updateTexture();
			}
			if(key == KeyCode.KEY_DELETE && text.length() > 0){
				text = text.substring(0);
				updateTexture();
			}
		}
	}
}