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

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.AWTComponentState;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.AWTConverter;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.mousecode.AWTMouseConverter;
import nl.knokko.gui.render.AWTGuiRenderer;
import nl.knokko.gui.texture.loader.AWTTextureLoader;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.CharBuilder;
import nl.knokko.gui.window.input.CharacterFilter;

public class AWTGuiWindow extends GuiWindow {
	
	public static float getMouseX(JFrame frame, int x){
		Insets insets = frame.getInsets();
		return (float) (x - insets.left) / (frame.getWidth() - 1 - insets.right - insets.left);
	}
	
	public static float getMouseY(JFrame frame, int y){
		Insets insets = frame.getInsets();
		return 1 - (float) (y - insets.top) / (frame.getHeight() - 1 - insets.top - insets.bottom);
	}
	
	private JFrame frame;
	
	private final AWTTextureLoader textureLoader;
	private final AWTGuiRenderer guiRenderer;
	private final CharBuilder charBuilder;
	
	private int prevMouseX;
	private int prevMouseY;
	
	public AWTGuiWindow(){
		textureLoader = new AWTTextureLoader();
		guiRenderer = new AWTGuiRenderer();
		charBuilder = new CharBuilder(textureLoader);
	}
	
	public AWTGuiWindow(GuiComponent mainComponent){
		this();
		this.mainComponent = mainComponent;
	}
	
	public void setFrame(JFrame frame){
		this.frame = frame;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	@Override
	protected void preUpdate() {}
	
	@Override
	protected void postUpdate(){
		Point mouse = frame.getMousePosition();
		if (mouse != null) {
			prevMouseX = mouse.x;
			prevMouseY = mouse.y;
		} else {
			prevMouseX = -1;
			prevMouseY = -1;
		}
	}
	
	@Override
	protected void directRender(){
		frame.repaint();
	}
	
	@Override
	protected void directClose(){
		frame.dispose();
	}
	
	@Override
	protected void directOpen(String title, int width, int height, boolean border) {
		frame = new JFrame();
		frame.add(new AWTPanel());
		frame.setUndecorated(!border);
		frame.setSize(width, height);
		frame.setTitle(title);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Listener l = new Listener();
		frame.addKeyListener(l);
		frame.addMouseListener(l);
		frame.addMouseWheelListener(l);
		frame.addMouseMotionListener(l);
	}
	
	@Override
	protected void directOpen(String title, boolean border) {
		frame = new JFrame();
		frame.add(new AWTPanel());
		frame.setUndecorated(!border);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setTitle(title);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Listener l = new Listener();
		frame.addKeyListener(l);
		frame.addMouseListener(l);
		frame.addMouseWheelListener(l);
		frame.addMouseMotionListener(l);
	}
	
	@Override
	protected GuiComponentState createState(){
		return new AWTComponentState(this);
	}
	
	@Override
	public void run(int fps){
		int frameTime = 1000000000 / fps;
		try {
			while(!shouldStopRunning && frame.isDisplayable()){
				if(listener == null || !listener.preRunLoop()){
					long startTime = System.nanoTime();
					update();
					render();
					long sleepTime = startTime + frameTime - System.nanoTime();
					if(sleepTime > 0){
						long millis = sleepTime / 1000000;
						Thread.sleep(millis, (int) (sleepTime - millis * 1000000));
					}//if sleepTime smaller than 0, we are behind schedule
					if(listener != null)
						listener.postRunLoop();
				}
			}
		} catch(InterruptedException ex){
			ex.printStackTrace();
		}
		close();
	}
	
	@Override
	public GuiTextureLoader getTextureLoader() {
		return textureLoader;
	}
	
	@Override
	public AWTGuiRenderer getRenderer() {
		return guiRenderer;
	}
	
	@Override
	public CharBuilder getCharBuilder() {
		return charBuilder;
	}
	
	@Override
	public float getMouseX() {
		Point mouse = frame.getMousePosition();
		if(mouse == null)
			return Float.NaN;
		Insets insets = frame.getInsets();
		return (float) (mouse.x - insets.left) / (frame.getWidth() - 1 - insets.right - insets.left);
	}
	
	@Override
	public float getMouseY() {
		Point mouse = frame.getMousePosition();
		if(mouse == null)
			return Float.NaN;
		Insets insets = frame.getInsets();
		return 1 - (float) (mouse.y - insets.top) / (frame.getHeight() - 1 - insets.top - insets.bottom);
	}
	
	@Override
	public float getMouseDX() {
		Point mouse = frame.getMousePosition();
		Insets insets = frame.getInsets();
		if (mouse != null && prevMouseX != -1)
			return (float) (mouse.x - prevMouseX) / (frame.getWidth() - insets.right - insets.left);
		return 0;
	}
	
	@Override
	public float getMouseDY() {
		Point mouse = frame.getMousePosition();
		Insets insets = frame.getInsets();
		if (mouse != null && prevMouseY != -1)
			return (float) -(mouse.y - prevMouseY) / (frame.getHeight() - insets.top - insets.bottom);
		return 0;
	}
	
	private class AWTPanel extends JPanel {

		private static final long serialVersionUID = -1226511423029324679L;
		
		@Override
		public void paint(Graphics g){
			Insets insets = frame.getInsets();
			guiRenderer.set(g, 0, 0, frame.getWidth() - 1 - insets.right - insets.left, frame.getHeight() - 1 - insets.bottom - insets.top);
			mainComponent.render(guiRenderer);
		}
	}
	
	private class Listener implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

		public void mouseDragged(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) {}

		public void mouseWheelMoved(MouseWheelEvent event) {
			//well... I will need magic numbers sometimes...
			float amount = event.getUnitsToScroll() * -0.01f;
			if(listener != null)
				amount = listener.preScroll(amount);
			if(amount != 0){
				mainComponent.scroll(amount);
				if(listener != null)
					listener.postScroll(amount);
			}
		}

		public void mouseClicked(MouseEvent event) {
			float x = getMouseX(frame, event.getX());
			float y = getMouseY(frame, event.getY());
			int button = AWTMouseConverter.getMouseButton(event.getButton());
			if(listener == null || !listener.preClick(x, y, button)){
				mainComponent.click(x, y, button);
				if(listener != null)
					listener.postClick(x, y, button);
			}
		}

		public void mousePressed(MouseEvent event) {
			input.setMouseDown(AWTMouseConverter.getMouseButton(event.getButton()));
		}

		public void mouseReleased(MouseEvent event) {
			input.setMouseUp(AWTMouseConverter.getMouseButton(event.getButton()));
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void keyTyped(KeyEvent event) {
			char c = event.getKeyChar();
			if(c != KeyEvent.CHAR_UNDEFINED && CharacterFilter.approve(c) && (listener == null || !listener.preKeyPressed(c))){
				mainComponent.keyPressed(c);
				if(listener != null)
					listener.postKeyPressed(c);
			}
		}//LWJGL doesn't have this event, but keyPressed doesn't give a meaningful character so I will have to use this event
		//I can't find a way to get this equal in awt and lwjgl

		public void keyPressed(KeyEvent event) {
			int[] codes = AWTConverter.getDirect(event.getKeyCode());
			if(codes != null && codes[0] != KeyCode.UNDEFINED){
				for(int code : codes){
					if(!input.isKeyDown(code)){//awt can generate multiple keyPressed events before the keyReleased event if the key is being hold for a while.
						//but because this does not happen in lwjgl, I disable this with the check above to keep both windows equal
						if(listener == null || !listener.preKeyPressed(code)){
							mainComponent.keyPressed(code);
							if(listener != null)
								listener.postKeyPressed(code);
						}
						input.setKeyDown(code);
					}
				}
			}
		}

		public void keyReleased(KeyEvent event) {
			int[] codes = AWTConverter.getDirect(event.getKeyCode());
			if(codes != null && codes[0] != KeyCode.UNDEFINED){
				for(int code : codes){
					if(listener == null || !listener.preKeyReleased(code)){
						mainComponent.keyReleased(code);
						input.setKeyUp(code);
						if(listener != null)
							listener.postKeyReleased(code);
					}
				}
			}
		}
	}
}