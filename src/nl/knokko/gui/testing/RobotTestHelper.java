package nl.knokko.gui.testing;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import nl.knokko.gui.window.GuiWindow;

public class RobotTestHelper extends GuiTestHelper {
	
	private final Robot robot;

	public RobotTestHelper(GuiWindow window) {
		super(window);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			throw new TestException("Couldn't create Robot instance: " + e.getMessage());
		}
	}

	@Override
	protected void moveMouseAbsolute(int destX, int destY) {
		robot.mouseMove(destX, destY);
	}

	@Override
	public void click(int button) {
		int buttonMask = InputEvent.getMaskForButton(button);
		robot.mousePress(buttonMask);
		delay(200);
		robot.mouseRelease(buttonMask);
	}

}