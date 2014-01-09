package vswe.stevesfactory.interfaces;

public enum Color {
	BLACK(0),
	BLUE(1),
	GREEN(2),
	CYAN(3),
	RED(4),
	PURPLE(5),
	ORANGE(6),
	LIGHT_GRAY(7),
	GRAY(8),
	LIGHT_BLUE(9),
	LIME(10),
	TURQUOISE(11),
	PINK(12),
	MAGENTA(13),
	YELLOW(14),
	WHITE(15);

	private int number;
	Color(int number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return "\u00a7" + Integer.toHexString(number);
	}
	
}
