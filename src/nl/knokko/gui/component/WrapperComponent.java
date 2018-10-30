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
package nl.knokko.gui.component;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;

public class WrapperComponent<C extends GuiComponent> extends AbstractGuiComponent {
    
    protected C component;
    protected GuiColor background;
    
    public WrapperComponent(C component){
        this.component = component;
    }
    
    public WrapperComponent(C component, GuiColor background) {
    	this(component);
    	this.background = background;
    }
    
    public void setComponent(C component){
        this.component = component;
        if(component != null){
            component.setState(state);
            component.init();
        }
    }
    
    public C getComponent(){
        return component;
    }
    
    public boolean isActive(){
        return true;
    }
    
    @Override
    public void setState(GuiComponentState state) {
    	super.setState(state);
    	if(component != null)
    		component.setState(state);
    }

    @Override
    public void init() {
        if(component != null)
            component.init();
    }

    @Override
    public void update() {
        if(component != null && isActive())
            component.init();
    }

    @Override
    public void render(GuiRenderer renderer) {
    	if (background != null)
    		renderer.clear(background);
        if(component != null && isActive())
            component.render(renderer);
    }

    @Override
    public void click(float x, float y, int button) {
        if(component != null && isActive())
            component.click(x, y, button);
    }

    @Override
    public void clickOut(int button) {
        if(component != null && isActive())
            component.clickOut(button);
    }

    @Override
    public boolean scroll(float amount) {
        if(component != null && isActive())
            return component.scroll(amount);
        return false;
    }

    @Override
    public void keyPressed(int keyCode) {
        if(component != null && isActive())
            component.keyPressed(keyCode);
    }

    @Override
    public void keyPressed(char character) {
        if(component != null && isActive())
            component.keyPressed(character);
    }

    @Override
    public void keyReleased(int keyCode) {
        if(component != null && isActive())
            component.keyReleased(keyCode);
    }
}