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
package nl.knokko.gui.component.state;

import java.awt.Insets;
import java.awt.Point;

import javax.swing.JFrame;

import nl.knokko.gui.window.AWTGuiWindow;

public class AWTComponentState implements GuiComponentState {
	
	private final JFrame frame;
	private final AWTGuiWindow window;

	public AWTComponentState(AWTGuiWindow window) {
		this.frame = window.getFrame();
		this.window = window;
	}

	public boolean isMouseOver() {
		return frame.getMousePosition() != null;
	}

	public float getMouseX() {
		Point mouse = frame.getMousePosition();
		if(mouse == null)
			return Float.NaN;
		Insets insets = frame.getInsets();
		return (float) (mouse.x - insets.left) / (frame.getWidth() - 1 - insets.right - insets.left);
	}

	public float getMouseY() {
		Point mouse = frame.getMousePosition();
		if(mouse == null)
			return Float.NaN;
		Insets insets = frame.getInsets();
		return 1 - (float) (mouse.y - insets.top) / (frame.getHeight() - 1 - insets.top - insets.bottom);
	}

	public AWTGuiWindow getWindow(){
		return window;
	}
}