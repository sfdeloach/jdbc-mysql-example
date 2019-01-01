package jdbc_mysql_example;

public class ConnectionVariables {
	private String url;
	private String database;
	private String dbUser;
	private String password;
	private String query;
	
	ConnectionVariables(String url, String database, String dbUser, String query) {
		this.url = url;
		this.database = database;
		this.dbUser = dbUser;
		this.query = query;
	}

	public boolean validate() {
		if (this.url != null && this.database != null && this.dbUser != null && this.password != null
				&& this.query != null) {
			return true;
		} else {
			return false;
		}
	}

	public String getVal(String key) {
		switch (key) {
		case ("url"):
			return this.url;
		case ("db"):
			return this.database;
		case ("user"):
			return this.dbUser;
		case ("pass"):
			return this.password;
		case ("qry"):
			return this.query;
		default:
			return "key not found";
		}
	}

	public void setVal(String key, String val) {
		switch (key) {
		case ("url"):
			this.url = val;
			break;
		case ("db"):
			this.database = val;
			break;
		case ("user"):
			this.dbUser = val;
			break;
		case ("pass"):
			this.password = val;
			break;
		case ("qry"):
			this.query = val;
			break;
		default:
			System.out.println("key not found");
		}
	}

	public String toString() {
		return "url: " + this.url + "\ndatabase: " + this.database + "\ndbUser: " + this.dbUser + "\npassword: "
				+ this.password + "\nquery: " + this.query;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
