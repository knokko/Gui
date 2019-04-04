package nl.knokko.gui.testing;

import java.awt.geom.Point2D;

public interface TextShowingComponent {
	
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
}