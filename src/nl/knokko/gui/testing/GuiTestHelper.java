package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.util.Collection;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.window.GuiWindow;

public abstract class GuiTestHelper {

	protected final GuiWindow window;

	protected Thread testThread;

	protected int delayTime;

	protected boolean stopped;

	public GuiTestHelper(GuiWindow window, int delayTime) {
		this.window = window;
		this.delayTime = delayTime;
	}

	public GuiTestHelper(GuiWindow window) {
		this(window, 100);
	}

	public void setDelayTime(int millis) {
		delayTime = millis;
	}

	public void stop() {
		stopped = true;
		testThread.interrupt();
		window.stopRunning();
	}

	public void moveMouse(float destX, float destY) {
		moveMouse(Math.round(destX * window.getWidth()), Math.round((1f - destY) * window.getHeight()));
	}

	protected abstract void moveMouseNow(int destX, int destY);

	public void moveMouse(int destX, int destY) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		moveMouseNow(window.getPosX() + destX, window.getPosY() + destY);
		delay();
	}

	protected abstract void clickNow(int button);

	public void click(int button) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		clickNow(button);
		delay();
	}

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

	/**
	 * Waits the current delay time. This method will block until the time has
	 * expired. Since the window should run on another thread, it should get time to
	 * do whatever it is doing. This method will automatically be called after every
	 * mouse and keyboard operation to give the window some time to respond. The
	 * setDelayTime() method can be used to set that delay time.
	 */
	public void delay() {
		delay(delayTime);
	}

	/**
	 * Waits for the specified amount of milliseconds. This method will block until
	 * the time has expired. Since the window should run on another thread, it
	 * should get time to do whatever it is doing. This method will automatically be
	 * called after every mouse operation to give the window some time to respond.
	 * How long that delay may take can be specified with setDelayTime().
	 * 
	 * @param millis The time in milliseconds to wait
	 */
	public void delay(int millis) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		if (testThread == null) {
			testThread = Thread.currentThread();
		} else if (testThread != Thread.currentThread()) {
			throw new TestException("Delay method of this test helper has been called on multiple threads");
		}
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new TestException("Delay has been interupted");
		}
	}

	/**
	 * Finds a component in the window that shows the specified text. If such a
	 * component can not be found, this method will return null. If your window uses
	 * your own text components, make sure that they implement TextShowingComponent.
	 * If you have your own menu components, make sure they implement
	 * TextShowingComponent and search their children.
	 * 
	 * @param text The text to search for
	 * @return A component that shows the text, or null if no component shows the
	 *         specified text
	 */
	public TextShowingComponent getComponentWithText(String text) {
		TextShowingComponent.Pair foundPair = getPairWithText(text);
		if (foundPair != null) {
			return foundPair.getComponent();
		} else {
			return null;
		}
	}

	protected TextShowingComponent.Pair getPairWithText(String text) {
		GuiComponent main = window.getMainComponent();
		if (main instanceof TextShowingComponent) {
			for (int counter = 0; counter < 24; counter++) {
				TextShowingComponent.Pair pair =  ((TextShowingComponent) main).getShowingComponent(text);
				if (pair == null) {
					delay(delayTime / 4);
					System.out.println("Delay a while");
					continue;
				}
				return pair;
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * Checks if there is a component in the window that shows the specified text.
	 * If such a component can not be found, this method will throw a TestException
	 * If your window uses your own text components, make sure that they implement
	 * TextShowingComponent. If you have your own menu components, make sure they
	 * implement TextShowingComponent and search their children.
	 * 
	 * @param text The text to search for
	 * 
	 * @throws TestException If no component is showing the given text
	 */
	public void assertComponentWithText(String text) {
		if (getComponentWithText(text) == null) {
			throw new TestException("There is no component showing the text " + text);
		}
	}

	public void assertComponentsWithTexts(String... texts) {
		for (String text : texts) {
			assertComponentWithText(text);
		}
	}

	/**
	 * Attempts to click at the middle of a component that claims to show the
	 * specified text. If such a component is found, the mouse will be moved to the
	 * middle of the component and the mouse will click there. If such a component
	 * can't be found, a TestException will be thrown.
	 * 
	 * @param text   The text to search for
	 * @param button The mouse button to click with
	 */
	public void click(String text, int button) {
		TextShowingComponent.Pair pair = getPairWithText(text);
		if (pair != null) {
			Point2D.Float point = pair.getPosition();
			click(point.x, point.y, button);
		} else {
			throw new TestException("No component with text " + text + " can be found");
		}
	}

	public void clickNearest(String text, GuiComponent from, int button, int amount) {
		GuiComponent main = window.getMainComponent();
		if (main instanceof TextShowingComponent) {
			int previousAmount = -1;
			for (int counter = 0; counter < 24; counter++) {
				Collection<TextShowingComponent.Pair> all = ((TextShowingComponent) main).getShowingComponents(text);
				if (all.size() != amount) {
					delay(delayTime / 4);
					previousAmount = amount;
					continue;
				}
				Point2D.Float middleFrom = new Point2D.Float(from.getState().getMidX(), from.getState().getMidY());
				Point2D.Float nearest = null;
				double nearestDistance = 0;
				for (TextShowingComponent.Pair pair : all) {
					if (nearest == null) {
						nearest = pair.getPosition();
						nearestDistance = nearest.distance(middleFrom);
					} else {
						double distance = pair.getPosition().distance(middleFrom);
						if (distance < nearestDistance) {
							nearestDistance = distance;
							nearest = pair.getPosition();
						}
					}
				}
				if (nearest != null) {
					click(nearest.x, nearest.y, button);
					return;
				} else {
					throw new TestException("No component with text " + text + " can be found");
				}
			}
			throw new TestException("There should have been " + amount + " components with text " + text + ", but only " + previousAmount + " were found");
		} else {
			throw new TestException("The main component doesn't implement TextShowingComponent");
		}
	}

	/**
	 * Attempts to click at the middle of a component that claims to show the
	 * specified text. If such a component is found, the mouse will be moved to the
	 * middle of the component and the mouse will click there. If such a component
	 * can't be found, a TestException will be thrown.
	 * 
	 * @param text The text to search for
	 */
	public void click(String text) {
		click(text, 0);
	}

	public void type(char character) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		typeNow(character);
		delay();
	}

	public void type(String string) {
		for (int index = 0; index < string.length(); index++) {
			type(string.charAt(index));
		}
	}

	protected abstract void typeNow(char character);

	public void pressAndRelease(int keycode, int amount) {
		for (int counter = 0; counter < amount; counter++) {
			pressAndRelease(keycode);
		}
	}

	public void pressAndRelease(int keycode) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		pressAndReleaseNow(keycode);
		delay();
	}

	protected abstract void pressAndReleaseNow(int keycode);
}