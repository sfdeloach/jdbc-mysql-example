
public class EnvironmentVariable {
	private String key;
	private String value;

	EnvironmentVariable(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{ " + this.key + ": " + this.value + " }";
	}
}
