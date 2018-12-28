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
import java.util.Scanner;
import java.util.Set;

public class Application {
	private String url;
	private String database;
	private String dbUser;
	private String password;

	// TODO: refactor with methods and classes
	public static void main(String[] args) throws SQLException, IOException {
		Application app = new Application();

		Scanner scanner = new Scanner(System.in);
		String menuOption = "help";
		while (!menuOption.equals("exit")) {
			switch (menuOption) {
				case "help" :
					app.printMenu();
					break;
				default :
					System.out.print("enter 'help' for assistance\n> ");
			}
			menuOption = scanner.nextLine();
		}
		scanner.close();

		// TODO: creates csv file if it does not exist
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("./csv/out.csv"));
		StringBuilder sb = new StringBuilder();

		try {

			// connect to database
			Connection conn = DriverManager.getConnection(
					app.getUrl() + app.getDatabase(), app.getUsername(),
					app.getPassword());

			Connection serverConn = DriverManager.getConnection(app.getUrl(),
					app.getUsername(), app.getPassword());

			app.printDatabases(serverConn);

			// create statement
			Statement stmt = conn.createStatement();

			// TODO: discover available tables within databases

			// execute query
			ResultSet rs = stmt.executeQuery("SELECT * FROM events");

			// get metadata
			ResultSetMetaData rsMeta = rs.getMetaData();

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

		}

	}

	Application() {
		this.setUrl(System.getenv("URL"));
		this.setDatabase(System.getenv("DATABASE"));
		this.setDbUser(System.getenv("DB_USER"));
		this.setPassword(System.getenv("PASSWORD"));
		this.checkEnvVars();
	}

	private void printMenu() {
		System.out.println("--------------------------------------");
		System.out.println("          MySQL JDBC Toolbox");
		System.out.println("--------------------------------------");
		System.out.println(" help   - Prints this menu");
		System.out.println(" url    - Read/Set the URL");
		System.out.println(" db     - Read/Set the database");
		System.out.println(" user   - Read/Set the username");
		System.out.println(" pass   - Read/Set the password");
		System.out.println(" dbs    - Provides a list of databases");
		System.out.println(" tables - Provides a list of tables");
		System.out.println(" exit   - Exits the program");
		System.out.print("> ");
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

	private void printDatabases(Connection conn) throws SQLException {
		// TODO: discover available databases
		ResultSet catalogs = conn.getMetaData().getCatalogs();
		System.out.println("Available databases:");
		while (catalogs.next()) {
			System.out.println("  " + catalogs.getString("TABLE_CAT"));
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
