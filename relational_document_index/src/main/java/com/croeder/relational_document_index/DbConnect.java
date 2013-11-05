package com.croeder.relational_document_index;

import static java.lang.System.out;

import java.sql.*;

public class DbConnect {

	// TODO: Properties
	//final String url="jdbc:postgresql://localhost:5432/";
	final String url="jdbc:postgresql:";
	final String database="croeder";
	final String user="croeder";
	final String password="cro3d3r!";

	Connection conn;

	public DbConnect() {
		try {
			conn = DriverManager.getConnection(url + database, user, password);
		}
		catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}

	public void query() {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from contact");
			while (rs.next()) {
				out.println(rs.getInt("id"));
				out.println(rs.getString("firstName"));	
			}	
		}
		catch (SQLException e) {
			out.println("error:" + e);
		}
	}
		
		

	public static void main(String args[]) {
		DbConnect dbCon = new DbConnect();
		dbCon.query();
	}		
}
