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
package nl.knokko.gui.window;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.CharBuilder;
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
	
	protected boolean renderContinuously;
	protected boolean needsRender;
	
	protected boolean shouldStopRunning;
	
	public GuiWindow(){
		input = new WindowInput();
	}
	
	/**
	 * Settings this to true will cause the application to render every tick, regardless of whether or not
	 * anything changed. By default this is false for AWT windows to spare power and performance, however, GL
	 * windows are very glitchy if they don't render every frame, so it is true by default for GL windows. Settings 
	 * this to true is only useful for applications like games that really have to render every tick or for components
	 * that want to change their color continuously.
	 * @param value True to render continuously, false to only render after changes
	 */
	public void setRenderContinuously(boolean value) {
		renderContinuously = value;
		getRenderer().setRenderAlways(value);
	}
	
	/**
	 * Notifies the window that something changed so that it should render again. Calling this only has
	 * effect if the window is not in continuous render mode.
	 */
	public void markChange() {
		needsRender = true;
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
		if (mainComponent != null) {
			mainComponent.setState(state);
			mainComponent.init();
		}
		markChange();
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
		markChange();
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
			markChange();
		}
	}
	
	public GuiComponent getMainComponent() {
		return mainComponent;
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
			postUpdate();
		}
	}
	
	protected abstract void preUpdate();
	
	protected abstract void postUpdate();
	
	/**
	 * Renders the main component of this window and calls the render methods of the window listener if there is a window listener
	 */
	public void render() {
		if ((renderContinuously || needsRender) && (listener == null || !listener.preRender())) {
			directRender();
			if(listener != null)
				listener.postRender();
			needsRender = false;
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
	 * This method gives the CharBuilder of this window. The CharBuilder can be used to get single character textures.
	 * @return The CharBuilder of this window
	 */
	public abstract CharBuilder getCharBuilder();
	
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
	
	/**
	 * This method gives the x-coordinate of the mouse in this window.
	 * The value will be between 0 and 1. A value of 0 means the mouse is at the left border of this window.
	 * A value of 1 means the mouse is at the right border of this window. If the mouse is not on the window,
	 * this method returns NaN.
	 * @return The x-coordinate of the mouse in this window
	 */
	public abstract float getMouseX();
	
	/**
	 * This method gives the y-coordinate of the mouse in this window.
	 * The value will be between 0 and 1. A value of 0 means the mouse is at the bottom border of this window.
	 * A value of 1 means the mouse is at the top border of this window. If the mouse is not on the window,
	 * this methods returns NaN.
	 * @return The y-coordinate of the mouse in this window
	 */
	public abstract float getMouseY();
	
	/**
	 * This method gives the mouse movement in the x-direction since the last call to update().
	 * The value will be between -1 and 1. A value of -1 indicates that the mouse would have moved from the 
	 * right border of the window to the left border of the window within the previous tick.
	 * A value of 0 indicates that the x-coordinate of the mouse didn't change in within the previous tick.
	 * A value of 1 indicates that the mouse would have moved from the left border to the right border within
	 * the previous tick.
	 * @return the mouse movement in the x-direction
	 */
	public abstract float getMouseDX();
	
	/**
	 * This method gives the mouse movement in the y-direction since the last call to update().
	 * The value will be between -1 and 1. A value of -1 indicates that the mouse would have moved from the 
	 * top border of the window to the bottom border of the window within the previous tick.
	 * A value of 0 indicates that the y-coordinate of the mouse didn't change in within the previous tick.
	 * A value of 1 indicates that the mouse would have moved from the bottom border to the top border within
	 * the previous tick.
	 * @return the mouse movement in the y-direction
	 */
	public abstract float getMouseDY();
	
	/**
	 * Determines the x-coordinate of the left border of this window. If this window is not yet open,
	 * this method will return -1.
	 * @return the x-coordinate of the left border of this window or -1 if the window isn't open
	 */
	public abstract int getWindowPosX();
	
	/**
	 * Determines the x-coordinate of the left-most point where this window will render its components. If
	 * this window is not yet open, this method will return -1.
	 * @return the x-coordinate of the left most point where this window will render its components
	 */
	public abstract int getPosX();
	
	/**
	 * @return The width of the window in pixels or -1 if the window isn't open
	 */
	public abstract int getWindowWidth();
	
	/**
	 * @return The width of the space in pixels where this window will render its components or -1 if the
	 * window isn't open
	 */
	public abstract int getWidth();
	
	/**
	 * Determines the y-coordinate of the upper border of this window. If this window is not yet open,
	 * this method will return -1.
	 * @return the y-coordinate of the upper border of this window or -1 if the window isn't open
	 */
	public abstract int getWindowPosY();
	
	
	/**
	 * Determines the y-coordinate of the highest point on the screen where this window will render its
	 * components. If this window isn't open, this method will return -1.
	 * @return the y-coorindate of the highest point this window will render its components or -1 if this
	 * window isn't open
	 */
	public abstract int getPosY();
	
	/**
	 * @return The height of the window in pixels or -1 if the window isn't open
	 */
	public abstract int getWindowHeight();
	
	/**
	 * @return The height of the space in pixels where this window will render its components or -1 if this
	 * window isn't open
	 */
	public abstract int getHeight();
}