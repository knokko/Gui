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
package nl.knokko.gui.window;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.window.input.WindowInput;

/**
 * The GuiWindow is the core of this library. The 2 types of GuiWindow are AWTGuiWindow and GLGuiWindow. GLGuiWindow is part of the GLGui library because it requires LWJGL. AWTGuiWindow is part of this library because everything it needs is part of java. Applications should create 1 instance of GuiWindow for their window. The getTextureLoader() should be used to create textures for the components of the window.
 * @author knokko
 *
 */
public abstract class GuiWindow {
	
	protected GuiComponent mainComponent;
	protected WindowListener listener;
	protected GuiComponentState state;
	protected WindowInput input;
	
	protected boolean shouldStopRunning;
	
	public GuiWindow(){
		input = new WindowInput();
	}
	
	/**
	 * Opens a window with the specified title, width and height. If the border is true, the window will have a border with a close button and other buttons.
	 * @param title The title of the window. This should be visible above the window if border is true
	 * @param width The width of the window in pixels
	 * @param height The height of the window in pixels
	 * @param border Determines whether this window has a border or not
	 */
	public void open(String title, int width, int height, boolean border){
		directOpen(title, width, height, border);
		state = createState();
		mainComponent.setState(state);
		mainComponent.init();
	}
	
	protected abstract void directOpen(String title, int width, int height, boolean border);
	
	/**
	 * Opens a window with the specified title in full screen. If border is true, the window will have a close button and other buttons.
	 * @param title The title of the window. This should be visible above the window if border is true
	 * @param border Determines whether this window has a border or not
	 */
	public void open(String title, boolean border){
		directOpen(title, border);
		state = createState();
		if (mainComponent != null) {
			mainComponent.setState(state);
			mainComponent.init();
		}
	}
	
	protected abstract void directOpen(String title, boolean border);
	
	/**
	 * Sets the main component of this window. This components is supposed to be the root component that contains all other components. It would be wise to use a GuiMenu as main component, but that is not required.
	 * @param component The main/root component of this window.
	 */
	public void setMainComponent(GuiComponent component){
		mainComponent = component;
		if(isOpen()){
			mainComponent.setState(state);
			mainComponent.init();
		}
	}
	
	protected abstract GuiComponentState createState();
	
	/**
	 * Sets the window listener of this window. The methods of the current listener will be called for events like clicking and closing. This allows more control over the window without overriding the class.
	 * @param listener The new window listener
	 */
	public void setWindowListener(WindowListener listener){
		this.listener = listener;
	}
	
	/**
	 * Updates the main component of this window and the listener if this window has a listener.
	 */
	public void update() {
		if(listener == null || !listener.preUpdate()){
			preUpdate();
			mainComponent.update();
			if(listener != null)
				listener.postUpdate();
		}
	}
	
	protected abstract void preUpdate();
	
	/**
	 * Renders the main component of this window and calls the render methods of the window listener if there is a window listener
	 */
	public void render() {
		if(listener == null || !listener.preRender()){
			directRender();
			if(listener != null)
				listener.postRender();
		}
	}
	
	protected abstract void directRender();
	
	/**
	 * Closes this window and informs the window listener if there is one. Don't use this if the run method is still active!
	 */
	public void close() {
		if(listener != null)
			listener.preClose();
		state = null;
		directClose();
		if(listener != null)
			listener.postClose();
	}
	
	/**
	 * Let the run method stop at the end of the current iteration
	 */
	public void stopRunning() {
		shouldStopRunning = true;
	}
	
	protected abstract void directClose();
	
	/**
	 * This method tries to start a loop that updates and renders this window. The window will try to call the update and render method fps times per seconds. A WindowListener can be set with setWindowListener() to get more control over the loop. It is also possible not to call this method, but use your own way to maintain enough fps.
	 * @param fps The preferred frames/updates per second
	 */
	public abstract void run(int fps);
	
	/**
	 * This method gives the GuiTextureLoader for this window type. All textures that are used by the components of this window should be created by this GuiTextureLoader. This method will not return until the loop of this window has been finished or the window has been closed.
	 * @return The GuiTextureLoader of this window.
	 */
	public abstract GuiTextureLoader getTextureLoader();
	
	/**
	 * This method gives the GuiRenderer that this window will use to render the main component.
	 * @return The GuiRenderer of this window
	 */
	public abstract GuiRenderer getRenderer();
	
	/**
	 * This method returns true if the window has been opened and is not yet closed.
	 * @return whether this window is open or not
	 */
	public boolean isOpen(){
		return state != null;
	}
	
	public WindowInput getInput(){
		return input;
	}
}