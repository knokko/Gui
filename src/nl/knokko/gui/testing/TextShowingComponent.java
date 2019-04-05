package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.util.Collection;

import nl.knokko.gui.component.GuiComponent;

public interface TextShowingComponent extends GuiComponent {
	
	/**
	 * Checks if this component is currently displaying the requested text. If so, a location where the text
	 * is being displayed is returned. (Since text is shown in a certain area rather than just a point,
	 * only a single point in that area is returned, but it is undefined where exactly in the area.)
	 * If this component doesn't display the given text, this method returns null.
	 * If this component is a menu, it will check the components inside it.
	 * @param text The text to search for
	 * @return A location where the text is displayed, or null if this component doesn't display the text
	 */
	Point2D.Float getLocationForText(String text);
	
	/**
	 * Gets the component that is showing the specified text. If this component shows the specified text,
	 * this component will be returned. If this is a menu, this method will search its subcomponents until
	 * it finds the exact source that displays the text. If this component and its children do not show
	 * the specified text, this method returns null.
	 * @param text The text to search for
	 * @return The component responsible for showing the specified text, or null if there is no such component
	 */
	TextShowingComponent getShowingComponent(String text);
	
	/**
	 * Gets a collection containing all components that show the specified text. If this is a simple text
	 * component and the text matches the specified text, it will return a collection only containing itself.
	 * If this is a simple text component that doesn't show the specified text, this will return an empty
	 * collection. If this is a menu, it will return a collection containing all subcomponents of this menu
	 * that show the specified text (possibly empty).
	 * @param text
	 * @return
	 */
	Collection<TextShowingComponent> getShowingComponents(String text);
	
	// TODO How about returning pairs of components and texts instead?
}