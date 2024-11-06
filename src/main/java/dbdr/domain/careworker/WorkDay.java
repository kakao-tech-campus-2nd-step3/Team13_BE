package dbdr.domain.careworker;

public enum WorkDay {
	MONDAY(1),
	TUESDAY(2),
	WEDNESDAY(4),
	THURSDAY(8),
	FRIDAY(16),
	SATURDAY(32),
	SUNDAY(64);

	private final int value;

	WorkDay(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
