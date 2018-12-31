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

	// TODO: refactor with methods and classes
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
			case "url":
			case "db":
			case "user":
			case "pass":
			case "qry":
				app.getNewConnVar(menuOption);
				break;
			case "tbls":
				app.printDatabases(DriverManager.getConnection(app.connVars.getUrl(), app.connVars.getDbUser(),
						app.connVars.getPassword()));
				System.out.print("\n> ");
				break;
			case "exe":
				app.executeQuery();
				break;
			default:
				System.out.println("command not recognized...");
				System.out.print("enter 'help' for assistance\n> ");
			}
			menuOption = app.scanner.nextLine();
		}
		app.scanner.close();

		// TODO: creates csv file if it does not exist
		BufferedWriter writer = new BufferedWriter(new FileWriter("./csv/out.csv"));
		StringBuilder sb = new StringBuilder();

		try {

			// connect to database
			Connection conn = DriverManager.getConnection(app.connVars.getUrl() + "/" + app.connVars.getDatabase(),
					app.connVars.getDbUser(), app.connVars.getPassword());

			// create statement
			Statement stmt = conn.createStatement();

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
			e.printStackTrace();
		}

	}

	Application() throws IOException {
		this.setConnectionVars();
		if (!this.connVars.validate()) {
			System.out.println("warning: connection file may be missing data");
		}
	}

	private void setConnectionVars() throws IOException {
		String filename = "./connection-data/connection.json";
		String json = new String(Files.readAllBytes(Paths.get(filename)));
		this.connVars = new Gson().fromJson(json, ConnectionVariables.class);
	}

	private void printMenu() {
		System.out.println("--------------------------------------");
		System.out.println("          MySQL JDBC Toolbox");
		System.out.println("--------------------------------------");
		System.out.println(" help - Print this menu");
		System.out.println(" conn - Print all connnection vars");
		System.out.println(" url  - Set the URL");
		System.out.println(" db   - Set the database");
		System.out.println(" user - Set the username");
		System.out.println(" pass - Set the password");
		System.out.println(" qry  - Set the query");
		System.out.println(" dbs  - Provides a list of databases");
		System.out.println(" tbls - Provides a list of tables");
		System.out.println(" exe  - Execute the query");
		System.out.println(" exit - Exits the program");
		System.out.print("> ");
	}

	private void printConnVars() {
		System.out.print(this.connVars.toString() + "\n> ");
	}

	private void getNewConnVar(String key) throws Exception {
		System.out.print("enter new " + key + ": ");
		String newVal = this.scanner.nextLine();
		this.setConnVar(key, newVal);
	}

	private void setConnVar(String key, String val) throws Exception {
		System.out.print(key + " set to: ");
		switch (key) {
		case "url":
			this.connVars.setUrl(val);
			System.out.print(this.connVars.getUrl());
			break;
		case "db":
			this.connVars.setDatabase(val);
			System.out.print(this.connVars.getDatabase());
			break;
		case "user":
			this.connVars.setDbUser(val);
			System.out.print(this.connVars.getDbUser());
			break;
		case "pass":
			this.connVars.setPassword(val);
			System.out.print(this.connVars.getPassword());
			break;
		case "qry":
			this.connVars.setQuery(val);
			System.out.print(this.connVars.getQuery());
			break;
		default:
			throw new Exception("Connection variable not found.");
		}
		System.out.print("\n> ");
	}

	private void executeQuery() {
	}

	private void printDatabases(Connection conn) throws SQLException {
		// TODO: discover available databases
		ResultSet catalogs = conn.getMetaData().getCatalogs();
		System.out.println("Available databases:");
		while (catalogs.next()) {
			System.out.println("  " + catalogs.getString("TABLE_CAT"));
		}
	}

}
