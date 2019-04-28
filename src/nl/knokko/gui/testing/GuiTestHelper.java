package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

import javax.imageio.ImageIO;

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

	/**
	 * Sets the delay time of this gui test helper. The delay time is the time in
	 * milliseconds that the helper will wait after calling a method that
	 * manipulates 'user' input (like mouseMouse and click). Also, whenever the test
	 * helper looks for a component with certain properties (for instance one that
	 * shows some text) and such a component can not be found, the test helper will
	 * wait 1/4 of the delay time before retrying to find that component. It will
	 * perform at most 256 such retries and it will thus time out after 256 / 4 = 64
	 * delay times have been expired without finding the component.
	 * 
	 * @param millis The time to wait after every manipulating method, in
	 *               milliseconds
	 */
	public void setDelayTime(int millis) {
		delayTime = millis;
	}

	/**
	 * Orders the test helper to stop its test. This method will make sure the
	 * testing thread will stop as soon as the next method of this test helper is
	 * called. If the test is currently sleeping (which happens during every delay),
	 * it will be interrupted and then stopped. Also, the stopRunning() method of
	 * the window will be called, which should close the window soon.
	 * 
	 * It is preferred not to call this method directly, but use GuiTester.stop()
	 * instead.
	 */
	public void stop() {
		stopped = true;
		testThread.interrupt();
		window.stopRunning();
	}

	/**
	 * Moves the test mouse to the specified location. The given coordinates must be
	 * between 0 and 1. Giving destX = 0 will cause the test mouse to move to the
	 * left border of the test window and destX = 1 will cause the test mouse to
	 * move to the right border of the test window. A destY = 0 will cause the test
	 * mouse to move to the lower border of the test window and a destY = 1 will
	 * cause the test mouse to move to the upper border of the test window. Using
	 * values between 0 and 1 will move the test mouse to somewhere within the test
	 * window.
	 * 
	 * The test mouse could be the actual mouse of the computer, but that doesn't
	 * have to be the case. It could also be a virtual mouse or just generate fake
	 * events for the test window.
	 * 
	 * @param destX The x-coordinate to move the mouse to
	 * @param destY The y-coordinate to move the mouse to
	 */
	public void moveMouse(float destX, float destY) {
		moveMouse(Math.round(destX * window.getWidth()), Math.round((1f - destY) * window.getHeight()));
	}

	/**
	 * Subclasses of GuiTestHelper must override this method. Invoking this method
	 * should move the test mouse to the specified location. The given coordinates
	 * (destX,destY) are relative to the upper-left inner corner of the test window,
	 * so (destX = 0,destY = 0) should move the test mouse to the upper-left corner
	 * of the inner space of the window. The inner space does not include the window
	 * border.
	 * 
	 * The test mouse doesn't have to be the actual mouse. It can be anything that
	 * causes the test window to 'think' that the mouse has moved.
	 * 
	 * @param destX The x-coordinate to move the test mouse to, relative to the
	 *              window
	 * @param destY The y-coordinate to move the test mouse to, relative to the
	 *              window
	 */
	protected abstract void moveMouseNow(int destX, int destY);

	/**
	 * Moves the test mouse to the given location. The given coordinates
	 * (destX,destY) are relative to the upper-left inner corner of the test window,
	 * so (destX = 0,destY = 0) should move the test mouse to the upper-left corner
	 * of the inner space of the window. The inner space does not include the window
	 * border.
	 * 
	 * The test mouse doesn't have to be the actual mouse. It can be anything that
	 * causes the test window to 'think' that the mouse has moved.
	 * 
	 * @param destX The x-coordinate to move the test mouse to, relative to the
	 *              inner space of the window
	 * @param destY The y-coordinate to move the test mouse to, relative to the
	 *              inner space of the window
	 */
	public void moveMouse(int destX, int destY) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		moveMouseNow(destX, destY);
		delay();
	}

	/**
	 * Subclasses of GuiTestHelper must override this method. Invoking this method
	 * should cause the test mouse to click with the specified button at its current
	 * position. Clicking the test mouse does not necessarily mean the real mouse
	 * should click. It is sufficient to let the window 'think' the user clicked.
	 * 
	 * button = 0 is for left clicking. button = 1 is for right clicking and button
	 * = 2 is for clicking with the scroll wheel.
	 * 
	 * @param button The button to click with
	 */
	protected abstract void clickNow(int button);

	/**
	 * Lets the test mouse click at its current position with the specified button.
	 * This doesn't necessarily mean that the real user mouse will click, it might
	 * also cause a fake click event for instance.
	 * 
	 * button = 0 is for left clicking. button = 1 is for right clicking and button
	 * = 2 is for clicking with the scroll wheel.
	 * 
	 * @param button The button to click with
	 */
	public void click(int button) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		clickNow(button);
		delay();
	}

	/**
	 * Lets the test mouse left click at its current position. This doesn't
	 * necessarily mean that the real user mouse will click, it might also cause a
	 * fake click event for instance.
	 */
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
	 * component can not be found, this method will retry until the component is
	 * found or the test times out.
	 * 
	 * 
	 * If it eventually finds the component, that component will be returned. If the
	 * test times out, a TestException will be thrown instead. If you use your own
	 * text components, make sure that they implement TextShowingComponent. If you
	 * have your own menu components, make sure they implement TextShowingComponent
	 * and search their children.
	 * 
	 * @param text The text to search for
	 * @return A component that shows the text
	 * @throws TestException If a component showing the given text can not be found
	 */
	public TextShowingComponent getComponentWithText(String text) {
		TextShowingComponent.Pair foundPair = getPairWithText(text);
		if (foundPair != null) {
			return foundPair.getComponent();
		} else {
			throw new TestException("No component with text " + text + " can be found");
		}
	}

	protected TextShowingComponent.Pair getPairWithText(String text) {
		for (int counter = 0; counter < 256; counter++) {
			GuiComponent main = window.getMainComponent();
			if (main instanceof TextShowingComponent) {
				TextShowingComponent.Pair pair = ((TextShowingComponent) main).getShowingComponent(text);
				if (pair == null) {
					delay(delayTime / 4);
					System.out.println("Delay a while");
					continue;
				}
				return pair;
			} else {
				delay(delayTime / 4);
				System.out.println("Delay a while");
				continue;
			}
		}
		System.out.println("getPairWithText timed out");
		return null;
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

	/**
	 * Checks if there is for every text a component in the window that shows it. If
	 * any of the texts can not be found in the window, a TestException will be
	 * thrown.
	 * 
	 * If you use your own text components, make sure they implement
	 * TextShowingComponent. If you use your own menu components, make sure they
	 * implement textShowingComponent and search their children when those methods
	 * are invoked.
	 * 
	 * @param texts All texts that should be shown in the window
	 */
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

	/**
	 * Clicks on the text component that shows the given text that is closest to a
	 * component showing the from text. The given amount must equal the number of
	 * components that show the specified text.
	 * 
	 * If no component showing from can be found, a TestException will be thrown.
	 * Also, a TestException will be thrown if the number of found components that
	 * show the given text doesn't equal the given amount.
	 * 
	 * @param text   The text that the component to click must contain
	 * @param from   The text displayed by the component that the component to click
	 *               must be close to
	 * @param amount The number of components that display the given text
	 */
	public void clickNearest(String text, String from, int amount) {
		clickNearest(text, getComponentWithText(from), 0, amount);
	}

	/**
	 * Clicks with the given button on the text component that shows the given text
	 * that is the closest to from. The given amount must equal the number of
	 * components that show the given text.
	 * 
	 * If the number of found components that display the given text doesn't equal
	 * the given amount, a TestException will be thrown.
	 * 
	 * @param text   The text that the component to click should display
	 * @param from   The component that the component to click must be close to
	 * @param button The button to click with
	 * @param amount The number of components that show the given text
	 */
	public void clickNearest(String text, GuiComponent from, int button, int amount) {
		int previousAmount = -1;
		for (int counter = 0; counter < 256; counter++) {
			GuiComponent main = window.getMainComponent();
			if (main instanceof TextShowingComponent) {
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
			} else {
				delay(delayTime / 4);
				previousAmount = 0;
				continue;
			}
		}
		throw new TestException("There should have been " + amount + " components with text " + text + ", but only "
				+ previousAmount + " were found");
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

	/**
	 * Attempts to type the specified character on the test keyboard. For some
	 * characters, this method will also press shift on the test keyboard.
	 * 
	 * The test keyboard is not necessarily the real keyboard of the computer. It
	 * could also be some virtual keyboard that simple generates key events in the
	 * application to test for instance.
	 * 
	 * @param character The character to type
	 */
	public void type(char character) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		typeNow(character);
		delay();
	}

	/**
	 * Attempts to the specified string character by character on the test keyboard.
	 * For some characters, this method will also press shift on the test keyboard.
	 * 
	 * The test keyboard is not necessarily the real keyboard of the computer. It
	 * could also be some virtual keyboard that simple generates key events in the
	 * application to test for instance.
	 * 
	 * @param string The string to type
	 */
	public void type(String string) {
		for (int index = 0; index < string.length(); index++) {
			type(string.charAt(index));
		}
	}

	/**
	 * Subclasses of GuiTestHelper must override this method. Invoking this method
	 * should attempt to type the specified character on the test keyboard. If
	 * necessary, additional keys like shift and control can be pressed.
	 * 
	 * The test keyboard doesn't have to be the actual keyboard, it could also be a
	 * virtual keyboard that simply generates key events for the application.
	 * 
	 * @param character The character to type
	 */
	protected abstract void typeNow(char character);

	/**
	 * Presses and releases the key with the specified keycode amount times. The
	 * delay between pressing and releasing each time will be the delay time of this
	 * gui test helper. Use the constants of KeyEvent to find the right keycode.
	 * 
	 * @param keycode The code of the key to press and release
	 * @param amount  The number of times the key should be pressed and released
	 */
	public void pressAndRelease(int keycode, int amount) {
		for (int counter = 0; counter < amount; counter++) {
			pressAndRelease(keycode);
		}
	}

	/**
	 * Presses and releases the key with the specified keycode. The delay between
	 * the pressing and the releasing the key will be the delay time of this gui
	 * test helper. Use the constants of KeyEvent to find the right keycode.
	 * 
	 * @param keycode The code of the key to press and release
	 */
	public void pressAndRelease(int keycode) {
		if (stopped) {
			throw new TestException("Test has been forced to stop");
		}
		pressAndReleaseNow(keycode);
		delay();
	}

	/**
	 * Subclasses of GuiTestHelper must override this method. Invoking this method
	 * should press and release the key with the specified code on the test
	 * keyboard. The delay() method should be called between the pressing and the
	 * releasing of the key.
	 * 
	 * The key code should be based on the constants of java.awt.KeyEvent.
	 * 
	 * The test keyboard is not necessarily the actual keyboard, it can also be a
	 * virtual keyboard that simply generates key events for the application to
	 * test.
	 * 
	 * @param keycode The code of the key to press and release
	 */
	protected abstract void pressAndReleaseNow(int keycode);

	/**
	 * Creates a random BufferedImage of the given size, writes it to the
	 * destination file and returns it. With 'random', I mean that the red, green,
	 * blue and alpha component of every pixel will be set to a random integer in
	 * the range 0 (inclusive) to 256 (exclusive).
	 * 
	 * @param destination The destination file to where the image should be saved,
	 *                    or null if the image should not be written to disk
	 * @param width       The width of the image to create
	 * @param height      The height of the image to create
	 * @return The created image
	 */
	public BufferedImage createImage(File destination, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Random random = new Random();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, random.nextInt());
			}
		}
		if (destination != null) {
			try {
				ImageIO.write(image, "PNG", destination);
			} catch (IOException ioex) {
				throw new TestException("Failed to save image: ", ioex);
			}
		}
		return image;
	}

	private static final File FOLDER = new File(".").getAbsoluteFile().getParentFile();

	/**
	 * Creates a random BufferedImage of the given size, writes it to the
	 * destination file and returns it. With 'random', I mean that the red, green,
	 * blue and alpha component of every pixel will be set to a random integer in
	 * the range 0 (inclusive) to 256 (exclusive).
	 * 
	 * @param destination The destination path where the image should be saved,
	 *                    relative to the java project that runs the test
	 * @param width       The width of the image to create
	 * @param height      The height of the image to create
	 * @return The created image
	 */
	public BufferedImage createImage(String relativePath, int width, int height) {
		return createImage(new File(FOLDER + "/" + relativePath), width, height);
	}

	/**
	 * Checks if two buffered images are equal. Two buffered images are considered
	 * equal if the following 3 conditions hold: The width of image1 is equal to the
	 * width of image2. The height of image1 is equal to the height of image2. The
	 * pixel at any location on image1 has the same color as the pixel on the same
	 * location at image2 (the red, green, blue and alpha components are all equal).
	 * 
	 * @param image1 An image to compare with the other image
	 * @param image2 The other image
	 * @return true if the images are equal, false if they are different
	 */
	public boolean compareImages(BufferedImage image1, BufferedImage image2) {
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
			return false;
		}

		int width = image1.getWidth();
		int height = image1.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Invokes compareImages(image1, image2) and throws a TestException if it
	 * returned false.
	 * 
	 * @param image1 An image that should be compared to image2
	 * @param image2 The image to compare with image1
	 */
	public void assertEqual(BufferedImage image1, BufferedImage image2) {
		if (!compareImages(image1, image2)) {
			throw new TestException("The images aren't equal!");
		}
	}

	/**
	 * Checks if the specified image is currently shown. If the image is not shown,
	 * this method will keep trying for a while until the image is shown. If the
	 * image is still not shown after the retrying, a TestException will be thrown.
	 * 
	 * @param image The image that should be shown at the moment
	 */
	public void assertImageShown(BufferedImage image) {
		for (int counter = 0; counter < 256; counter++) {
			GuiComponent main = window.getMainComponent();
			if (main instanceof ImageShowingComponent) {
				Collection<BufferedImage> images = ((ImageShowingComponent) main).getShownImages();
				for (BufferedImage bi : images) {
					if (compareImages(image, bi)) {
						return;
					}
				}
			}
			System.out.println("delay a while");
			delay(delayTime / 4);
		}
		throw new TestException("The image is not shown");
	}
	
	private Collection<ImageShowingComponent.Pair> getAllImages(int amount){
		int lastAmount = 0;
		for (int counter = 0; counter < 256; counter++) {
			GuiComponent main = window.getMainComponent();
			Collection<ImageShowingComponent.Pair> images = null;
			if (main instanceof ImageShowingComponent) {
				images = ((ImageShowingComponent) main).getShowingComponents();
				lastAmount = images.size();
			}
			if (images == null || images.size() != amount) {
				System.out.println("Delay a while");
				delay(delayTime / 4);
			} else {
				return images;
			}
		}
		Collection<ImageShowingComponent.Pair> images = ((ImageShowingComponent) window.getMainComponent()).getShowingComponents();
		for (ImageShowingComponent.Pair pair : images) {
			System.out.println(pair.getPosition());
		}
		throw new TestException("There should be " + amount + " visible images, but only " + lastAmount + " were found");
	}

	/**
	 * Gets the ImageShowingComponent with its location that is located the closest to (the parameter) from.
	 * The amount parameter must equal the total amount of images that are currently shown in the application.
	 * If the found number of image showing components does not equal this amount, this method will retry for
	 * a while until the amount of found image showing components equals the given amount. If they are still
	 * not equal at the end, a TestException will be thrown.
	 * @param from The location from where the nearest image (showing component) should be found
	 * @param amount The total number of images shown by the application
	 * @return The nearest image showing component with its location
	 */
	public ImageShowingComponent.Pair getNearestImage(Point2D.Float from, int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive, but is " + amount);
		}
		Collection<ImageShowingComponent.Pair> images = getAllImages(amount);
		
		ImageShowingComponent.Pair closest = null;
		double closestDistanceSQ = Float.POSITIVE_INFINITY;
		
		for (ImageShowingComponent.Pair pair : images) {
			double distanceSQ = pair.getPosition().distanceSq(from);
			if (distanceSQ < closestDistanceSQ) {
				closestDistanceSQ = distanceSQ;
				closest = pair;
			}
		}
		
		return closest;
	}
	
	/**
	 * Gets the ImageShowingComponent with its location that is located the closest to (the parameter) from.
	 * The amount parameter must equal the total amount of images that are currently shown in the application.
	 * If the found number of image showing components does not equal this amount, this method will retry for
	 * a while until the amount of found image showing components equals the given amount. If they are still
	 * not equal at the end, a TestException will be thrown.
	 * @param from The component from where the nearest image (showing component) should be found
	 * @param amount The total number of images shown by the application
	 * @return The nearest image showing component with its location
	 */
	public ImageShowingComponent.Pair getNearestImage(GuiComponent from, int amount){
		return getNearestImage(new Point2D.Float(from.getState().getMidX(), from.getState().getMidY()), amount);
	}
	
	/**
	 * Gets the ImageShowingComponent with its location that is located the closest to the text component
	 * displaying (the parameter) from.
	 * The amount parameter must equal the total amount of images that are currently shown in the application.
	 * If the found number of image showing components does not equal this amount, this method will retry for
	 * a while until the amount of found image showing components equals the given amount. If they are still
	 * not equal at the end, a TestException will be thrown.
	 * @param from The text of the component from where the nearest image (showing component) should be found
	 * @param amount The total number of images shown by the application
	 * @return The nearest image showing component with its location
	 */
	public ImageShowingComponent.Pair getNearestImage(String from, int amount){
		return getNearestImage(getComponentWithText(from), amount);
	}
	
	/**
	 * Find the ImageShowingComponent that is closest to (the parameter) from and checks if it displays the
	 * given image. If it doesn't, a TestException will be thrown.
	 * The amount parameter must equal the total amount of images that are currently shown in the application.
	 * If the found number of image showing components does not equal this amount, this method will retry for
	 * a while until the amount of found image showing components equals the given amount. If they are still
	 * not equal at the end, a TestException will be thrown.
	 * @param from The point to find the nearest image from
	 * @param image The image that should be shown by the nearest image showing component to from
	 * @param amount The total number of images shown by the application
	 */
	public void assertNearestImage(Point2D.Float from, BufferedImage image, int amount) {
		Collection<BufferedImage> shownImages = getNearestImage(from, amount).getComponent().getShownImages();
		for (BufferedImage shownImage : shownImages) {
			if (compareImages(image, shownImage)) {
				return;
			}
		}
		throw new TestException("The nearest image showing component to from doesn't show the given image");
	}
	
	/**
	 * Find the ImageShowingComponent that is closest to (the parameter) from and checks if it displays the
	 * given image. If it doesn't, a TestException will be thrown.
	 * The amount parameter must equal the total amount of images that are currently shown in the application.
	 * If the found number of image showing components does not equal this amount, this method will retry for
	 * a while until the amount of found image showing components equals the given amount. If they are still
	 * not equal at the end, a TestException will be thrown.
	 * @param from The component to find the nearest image from
	 * @param image The image that should be shown by the nearest image showing component to from
	 * @param amount The total number of images shown by the application
	 */
	public void assertNearestImage(GuiComponent from, BufferedImage image, int amount) {
		assertNearestImage(new Point2D.Float(from.getState().getMidX(), from.getState().getMidY()), image, amount);
	}
	
	/**
	 * Find the ImageShowingComponent that is closest to the text component showing (the parameter) from and 
	 * checks if it displays the given image. If it doesn't, a TestException will be thrown.
	 * The amount parameter must equal the total amount of images that are currently shown in the application.
	 * If the found number of image showing components does not equal this amount, this method will retry for
	 * a while until the amount of found image showing components equals the given amount. If they are still
	 * not equal at the end, a TestException will be thrown.
	 * @param from The text of the component to find the nearest image from
	 * @param image The image that should be shown by the nearest image showing component to from
	 * @param amount The total number of images shown by the application
	 */
	public void assertNearestImage(String from, BufferedImage image, int amount) {
		assertNearestImage(getComponentWithText(from), image, amount);
	}
}