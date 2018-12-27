import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

	public static void main(String[] args) throws SQLException, IOException {
		Connection conn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		ResultSetMetaData myRsMeta = null;

		BufferedWriter writer = new BufferedWriter(
				new FileWriter("./csv/out.csv"));
		StringBuilder sb = new StringBuilder();

		String url = System.getenv("URL");
		String database = System.getenv("DATABASE");
		String username = System.getenv("USERNAME");
		String password = System.getenv("PASSWORD");

		try {
			// connect to database
			conn = DriverManager.getConnection(url + database, username,
					password);

			// create statement
			myStmt = conn.createStatement();

			// TODO: discover available databases

			// TODO: discover available tables within databases

			// execute query
			myRs = myStmt.executeQuery("SELECT * FROM events");

			// get metadata
			myRsMeta = myRs.getMetaData();

			// print columns
			for (int i = 1; i < myRsMeta.getColumnCount(); i++) {
				sb.append(myRsMeta.getColumnName(i) + ",");
			}
			sb.append(myRsMeta.getColumnName(myRsMeta.getColumnCount()) + "\n");

			// process results
			while (myRs.next()) {
				for (int i = 1; i < myRsMeta.getColumnCount(); i++) {
					sb.append(myRs.getString(i) + ",");
				}
				sb.append(myRs.getString(myRsMeta.getColumnCount()) + "\n");
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
			if (myRs != null) {
				myRs.close();
			}

			if (myStmt != null) {
				myStmt.close();
			}

			if (conn != null) {
				conn.close();
			}
		}

	}

}
