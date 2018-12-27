import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class Application {
	private String url;
	private String database;
	private String dbUser;
	private String password;

	Application() {
		this.setUrl(System.getenv("URL"));
		this.setDatabase(System.getenv("DATABASE"));
		this.setDbUser(System.getenv("DB_USER"));
		this.setPassword(System.getenv("PASSWORD"));
		this.checkEnvVars();
	}

	// TODO: refactor with methods and classes
	public static void main(String[] args) throws SQLException, IOException {
		Application app = new Application();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsMeta = null;

		// TODO: creates csv file if it does not exist
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("./csv/out.csv"));
		StringBuilder sb = new StringBuilder();

		try {
			// connect to database
			conn = DriverManager.getConnection(app.getUrl() + app.getDatabase(),
					app.getUsername(), app.getPassword());

			// create statement
			stmt = conn.createStatement();

			// TODO: discover available databases

			// TODO: discover available tables within databases

			// execute query
			rs = stmt.executeQuery("SELECT * FROM events");

			// get metadata
			rsMeta = rs.getMetaData();

			// print columns
			for (int i = 1; i < rsMeta.getColumnCount(); i++) {
				sb.append(rsMeta.getColumnName(i) + ",");
			}
			sb.append(rsMeta.getColumnName(rsMeta.getColumnCount()) + "\n");

			// process results
			while (rs.next()) {
				for (int i = 1; i < rsMeta.getColumnCount(); i++) {
					sb.append(rs.getString(i) + ",");
				}
				sb.append(rs.getString(rsMeta.getColumnCount()) + "\n");
			}

			// create file and print results
			writer.write(sb.toString());
			writer.close();
			System.out.println(sb.toString());

		} catch (Exception e) {
			System.out.println("An exception occurred!");
			System.out.println(e.getMessage());
			e.printStackTrace();

		} finally {
			if (rs != null) {
				rs.close();
			}

			if (stmt != null) {
				stmt.close();
			}

			if (conn != null) {
				conn.close();
			}
		}

	}

	private void checkEnvVars() {
		boolean envVarsSet = true;
		Set<EnvironmentVariable> vars = new HashSet<EnvironmentVariable>();

		vars.add(new EnvironmentVariable("DATABASE", this.database));
		vars.add(new EnvironmentVariable("PASSWORD", this.password));
		vars.add(new EnvironmentVariable("URL", this.url));
		vars.add(new EnvironmentVariable("DB_USER", this.dbUser));

		for (EnvironmentVariable var : vars) {
			if (var.getValue() == null) {
				System.out.println(var.toString());
				envVarsSet = false;
			}
		}

		if (!envVarsSet) {
			System.exit(0);
		}
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return this.database;
	}

	/**
	 * @param database
	 *            the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @return the dbUser
	 */
	public String getUsername() {
		return this.dbUser;
	}

	/**
	 * @param dbUser
	 *            the dbUser to set
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
