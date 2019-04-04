package nl.knokko.gui.testing;

import nl.knokko.gui.window.GuiWindow;

public abstract class GuiTestHelper {
	
	protected final GuiWindow window;

	public GuiTestHelper(GuiWindow window) {
		this.window = window;
	}
	
	public void moveMouse(float destX, float destY) {
		moveMouse(Math.round(destX * window.getWidth()), Math.round(destY * window.getHeight()));
	}
	
	protected abstract void moveMouseAbsolute(int destX, int destY);
	
	public void moveMouse(int destX, int destY) {
		moveMouseAbsolute(window.getWindowPosX() + destX, window.getWindowPosY() + destY);
	}
	
	public abstract void click(int button);
	
	public void click() {
		click(0);
	}
	
	public void click(float x, float y, int button) {
		moveMouse(x, y);
		click(button);
	}
	
	public void click(float x, float y) {
		click(x, y, 0);
	}
	
	public void click(int x, int y, int button) {
		moveMouse(x, y);
		click(button);
	}
	
	public void click(int x, int y) {
		click(x, y, 0);
	}
	
	public void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new TestException("Delay has been interupted");
		}
	}
}