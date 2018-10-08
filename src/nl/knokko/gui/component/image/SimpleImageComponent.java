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
package nl.knokko.gui.component.image;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;

public class SimpleImageComponent extends AbstractGuiComponent {
	
	private final GuiTexture texture;
	
	public SimpleImageComponent(GuiTexture texture){
		super();
		this.texture = texture;
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

        @Override
	public void init() {}
}