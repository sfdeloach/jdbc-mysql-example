package jdbc_mysql_example;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Application {
	protected ConnectionVariables connVars;
	private Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		Application app = new Application();
		String menuOption = "help";

		while (!menuOption.equals("exit")) {
			switch (menuOption) {
			case "help":
				app.printMenu();
				break;
			case "conn":
				app.printConnVars();
				break;
			case "save":
				app.saveConnVars();
				break;
			case "url":
			case "db":
			case "user":
			case "pass":
			case "qry":
				app.setConnVar(menuOption);
				break;
			case "dbs":
				app.printDatabases();
				break;
			case "tbls":
				app.printTables();
				break;
			case "exe":
				app.executeQuery();
				break;
			default:
				System.out.println("command not recognized...");
				System.out.print("enter 'help' for assistance");
			}
			System.out.print("\n> ");
			menuOption = app.scanner.nextLine();
		}
		app.scanner.close();

	}

	Application() throws IOException {
		this.setup();
		if (!this.connVars.validate()) {
			System.out.println("*** connection variables may be incomplete ***");
		}
	}

	private void setup() throws IOException {
		// create supporting directories if necessary
		if (Files.notExists(Paths.get("./csv"))) {
			System.out.println("*** creating csv output directory ***");
			Files.createDirectory(Paths.get("./csv"));
		}

		if (Files.notExists(Paths.get("./con/vars.json"))) {
			System.out.println("*** creating new connection variables file ***");

			Files.createDirectory(Paths.get("./con"));
			Files.createFile(Paths.get("./con/vars.json"));

			ConnectionVariables conVars = new ConnectionVariables(
					"jdbc:mysql://mysqlinstance.cymianwmsyts.us-east-1.rds.amazonaws.com", "xcad", "mySqlAdmin",
					"SELECT * FROM events");

			BufferedWriter writer = new BufferedWriter(new FileWriter("./con/vars.json"));
			writer.write(new Gson().toJson(conVars));
			writer.close();

		}

		String filename = "./con/vars.json";
		String json = new String(Files.readAllBytes(Paths.get(filename)));
		this.connVars = new Gson().fromJson(json, ConnectionVariables.class);
	}

	private void printMenu() {
		System.out.println("--------------------------------------");
		System.out.println("          MySQL JDBC Toolbox");
		System.out.println("--------------------------------------");
		System.out.println(" help - Print this menu");
		System.out.println(" conn - Print all connnection vars");
		System.out.println(" save - Save connection vars");
		System.out.println(" url  - Set the URL");
		System.out.println(" db   - Set the database");
		System.out.println(" user - Set the username");
		System.out.println(" pass - Set the password");
		System.out.println(" qry  - Set the query");
		System.out.println(" dbs  - Provides a list of databases");
		System.out.println(" tbls - Provides a list of tables");
		System.out.println(" exe  - Execute the query");
		System.out.println(" exit - Exits the program");
	}

	private void printConnVars() {
		System.out.println(this.connVars.toString());
	}

	private void saveConnVars() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("./con/vars.json"));
		writer.write(new Gson().toJson(this.connVars));
		writer.close();
		System.out.println("Connection variables saved to ./con/vars.json");
	}

	private void setConnVar(String key) throws Exception {
		System.out.println("    current " + key + ": " + this.connVars.getVal(key));
		System.out.print("  enter new " + key + ": ");
		String val = this.scanner.nextLine();
		this.connVars.setVal(key, val);
		System.out.println("     " + key + " set to: " + this.connVars.getVal(key));
	}

	private void executeQuery() throws Exception {
		System.out.println("Executing query: " + this.connVars.getQuery());

		BufferedWriter writer = new BufferedWriter(new FileWriter("./csv/out.csv"));
		StringBuilder sb = new StringBuilder();

		Connection conn = DriverManager.getConnection(this.connVars.getUrl() + "/" + this.connVars.getDatabase(),
				this.connVars.getDbUser(), this.connVars.getPassword());
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(this.connVars.getQuery());
		ResultSetMetaData rsMeta = rs.getMetaData();

		// append columns to string builder
		for (int i = 1; i < rsMeta.getColumnCount(); i++) {
			sb.append(rsMeta.getColumnName(i) + ",");
		}
		sb.append(rsMeta.getColumnName(rsMeta.getColumnCount()) + "\n");

		// append data to string builder
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

		conn.close();
	}

	private void printDatabases() throws SQLException {
		System.out.println("Available databases:");

		Connection conn = DriverManager.getConnection(this.connVars.getUrl(), this.connVars.getDbUser(),
				this.connVars.getPassword());
		ResultSet catalogs = conn.getMetaData().getCatalogs();

		while (catalogs.next()) {
			System.out.println("  " + catalogs.getString("TABLE_CAT"));
		}

		conn.close();
	}

	private void printTables() throws SQLException {
		System.out.println("Available tables:");

		Connection conn = DriverManager.getConnection(this.connVars.getUrl() + "/" + this.connVars.getDatabase(),
				this.connVars.getDbUser(), this.connVars.getPassword());
		ResultSet tables = conn.getMetaData().getTables(null, null, "%", null);

		System.out.println("-----------------------------");
		System.out.println("|   CATALOG -> TABLE NAME   |");
		System.out.println("-----------------------------");
		while (tables.next()) {
			System.out.println("  " + tables.getString("TABLE_CAT") + " -> " + tables.getString("TABLE_NAME"));
		}

		conn.close();
	}

}
