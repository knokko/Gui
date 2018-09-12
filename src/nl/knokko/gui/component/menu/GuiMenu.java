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
package nl.knokko.gui.component.menu;

import java.util.ArrayList;
import java.util.List;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.component.state.RelativeComponentState;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.window.input.WindowInput;

public abstract class GuiMenu extends AbstractGuiComponent {
	
	protected List<SubComponent> components;
	
	/**
	 * The screenCenterX determines what X-coordinate will be rendered in the middle of the screen.
	 */
	protected float screenCenterX;
	
	/**
	 * The screenCenterY determines what Y-coordinate will be rendered in the middle of the screen.
	 */
	protected float screenCenterY;
	
	protected float minCenterX;
	protected float minCenterY;
	protected float maxCenterX;
	protected float maxCenterY;
	
	protected boolean directRefresh;
	
	public GuiMenu(){
		super();
		components = new ArrayList<SubComponent>();
	}
	
	public void init(){
		directRefresh = false;
		addComponents();
		refreshMovement();
		directRefresh = true;
	}
	
	protected abstract void addComponents();

	public void update() {
		for(SubComponent component : components)
			if(component.isActive())
				component.getComponent().update();
		if(allowArrowMoving()){
			WindowInput input = state.getWindow().getInput();
			if(input.isKeyDown(KeyCode.KEY_LEFT))
				screenCenterX -= 0.005f;
			if(input.isKeyDown(KeyCode.KEY_RIGHT))
				screenCenterX += 0.005f;
			if(input.isKeyDown(KeyCode.KEY_UP))
				screenCenterY += 0.005f;
			if(input.isKeyDown(KeyCode.KEY_DOWN))
				screenCenterY -= 0.005f;
			if(screenCenterX < minCenterX)
				screenCenterX = minCenterX;
			if(screenCenterX > maxCenterX)
				screenCenterX = maxCenterX;
			if(screenCenterY < minCenterY)
				screenCenterY = minCenterY;
			if(screenCenterY > maxCenterY)
				screenCenterY = maxCenterY;
		}
	}

	public void render(GuiRenderer renderer) {
		renderer.clear(getBackgroundColor());
		for(SubComponent component : components)
			if(component.isActive())
				component.render(renderer);
	}

	public void click(float x, float y, int button) {
		x += screenCenterX;
		y += screenCenterY;
		for(SubComponent component : components)
			if(component.isActive())
				component.click(x, y, button);
	}

	public void clickOut(int button) {
		for(SubComponent component : components)
			if(component.isActive())
				component.getComponent().clickOut(button);
	}

	public boolean scroll(float amount) {
		SubComponent component = getComponentAt(state.getMouseX() + screenCenterX, state.getMouseY() + screenCenterY);
		if(component != null && component.getComponent().scroll(amount))
			return true;
		if(!allowScrolling())
			return false;
		float prevCenterY = screenCenterY;
		screenCenterY += 2 * amount;
		if(screenCenterY < minCenterY)
			screenCenterY = minCenterY;
		if(screenCenterY > maxCenterY)
			screenCenterY = maxCenterY;
		return screenCenterY != prevCenterY;
	}
	
	public void keyPressed(int keyCode) {
		for(SubComponent component : components)
			if(component.isActive())
				component.component.keyPressed(keyCode);
	}
	
	public void keyPressed(char character) {
		for(SubComponent component : components)
			if(component.isActive())
				component.component.keyPressed(character);
	}

	public void keyReleased(int keyCode) {
		for(SubComponent component : components)
			if(component.isActive())
				component.component.keyReleased(keyCode);
	}
	
	protected void refreshMovement(){
		float minX = 0;
		float minY = 0;
		float maxX = 1;
		float maxY = 1;
		for(SubComponent component : components){
			if(component.minX < minX)
				minX = component.minX;
			if(component.maxX > maxX)
				maxX = component.maxX;
			if(component.minY < minY)
				minY = component.minY;
			if(component.maxY > maxY)
				maxY = component.maxY;
		}
		minCenterX = minX;
		if(minCenterX > 0)
			minCenterX = 0;
		minCenterY = minY;
		if(minCenterY > 0)
			minCenterY = 0;
		maxCenterX = maxX - 1;
		if(maxCenterX < 0)
			maxCenterX = 0;
		maxCenterY = maxY - 1;
		if(maxCenterY < 0)
			maxCenterY = 0;
	}
	
	public GuiColor getBackgroundColor(){
		return SimpleGuiColor.BLACK;
	}
	
	protected boolean allowScrolling(){
		return true;
	}
	
	protected boolean allowArrowMoving(){
		return true;
	}
	
	public void addComponent(SubComponent component){
		components.add(component);
		if(directRefresh)
			refreshMovement();
	}
	
	public void addComponent(GuiComponent component, float minX, float minY, float maxX, float maxY){
		components.add(new SubComponent(component, minX, minY, maxX, maxY));
		if(directRefresh)
			refreshMovement();
	}
	
	public SubComponent getComponentAt(float x, float y){
		for(SubComponent component : components)
			if(component.isActive() && component.inBounds(x, y))
				return component;
		return null;
	}
	
	public List<SubComponent> getComponents(){
		return components;
	}
	
	public class SubComponent {
		
		private final GuiComponent component;
		
		private float minX;
		private float minY;
		private float maxX;
		private float maxY;
		
		public SubComponent(GuiComponent component, float minX, float minY, float maxX, float maxY){
			this.component = component;
			setBounds(minX, minY, maxX, maxY);
			component.setState(new RelativeComponentState.Dynamic(new State()));
			component.init();
		}
		
		public void render(GuiRenderer renderer){
			component.render(renderer.getArea(minX - screenCenterX, minY - screenCenterY, maxX - screenCenterX, maxY - screenCenterY));
		}
		
		public GuiComponent getComponent(){
			return component;
		}
		
		public void setBounds(float minX, float minY, float maxX, float maxY){
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}
		
		public void click(float x, float y, int button){
			if(inBounds(x, y))
				component.click((x - minX) / (maxX - minX), (y - minY) / (maxY - minY), button);
			else
				component.clickOut(button);
		}
		
		public boolean inBounds(float x, float y){
			return x >= minX && x <= maxX && y >= minY && y <= maxY;
		}
		
		protected boolean isActive(){
			return true;
		}
		
		public class State implements RelativeComponentState.Dynamic.State {

			public GuiComponentState parent() {
				return GuiMenu.this.state;
			}

			public float minX() {
				return minX - screenCenterX;
			}

			public float minY() {
				return minY - screenCenterY;
			}

			public float maxX() {
				return maxX - screenCenterX;
			}

			public float maxY() {
				return maxY - screenCenterY;
			}
		}
	}
}