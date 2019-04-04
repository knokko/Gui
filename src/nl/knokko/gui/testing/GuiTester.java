package nl.knokko.gui.testing;

import nl.knokko.gui.window.GuiWindow;

public class GuiTester {
	
	public static void start(GuiTestProgram test, GuiTestHelper helper) {
		test.test(helper);
	}
	
	public static void start(GuiTestProgram test, GuiWindow window) {
		window.open("Test Window", true);
		Thread windowThread = new Thread(() -> {
			window.run(60);
		});
		windowThread.start();
		RobotTestHelper helper = new RobotTestHelper(window);
		helper.delay(500);
		start(test, helper);
		window.stopRunning();
	}
}