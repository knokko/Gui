package nl.knokko.gui.component.text;

import nl.knokko.gui.util.TextBuilder.Properties;

public class IntEditField extends TextEditField {

	private final long minValue;
	private final long maxValue;

	public IntEditField(long initial, Properties passiveProperties, Properties activeProperties, long minValue,
			long maxValue) {
		super(initial + "", passiveProperties, activeProperties);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public void keyPressed(char character) {
		if ((character >= '0' && character <= '9') || (text.isEmpty() && character == '-')) {
			super.keyPressed(character);
		}
	}

	public long getLong() {
		long result;
		if (text.isEmpty() || text.equals("-")) {
			result = 0;
		} else {
			try {
				result = Long.parseLong(text);
			} catch (NumberFormatException ex) {
				if (text.charAt(0) == '-') {
					result = minValue;
				} else {
					result = maxValue;
				}
			}
		}
		setText(result + "");
		return result;
	}

	/**
	 * This method simple casts the result of getLong() to int, you should make sure
	 * the minValue and maxValue are in the int range.
	 * 
	 * @return the integer that is written in this edit field
	 */
	public int getInt() {
		return (int) getLong();
	}

	/**
	 * This method simple casts the result of getLong() to short, you should make
	 * sure the minValue and maxValue are in the short range.
	 * 
	 * @return the integer that is written in this edit field
	 */
	public short getShort() {
		return (short) getLong();
	}

	/**
	 * This method simple casts the result of getLong() to byte, you should make
	 * sure the minValue and maxValue are in the byte range.
	 * 
	 * @return the integer that is written in this edit field
	 */
	public byte getByte() {
		return (byte) getByte();
	}
}