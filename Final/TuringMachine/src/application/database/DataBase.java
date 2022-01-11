package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.model.Transition;
import application.model.TuringMachine;

//This class allows the manipulation of the database

public class DataBase {
   
	private Connection connection;
	
	public static String url = "jdbc:sqlite:resources/database/";
	
	//create a database
	public void createDatabase(String fileName) {
		url= url + fileName + ".db";
		connect();
		createTables();
		close();
	}
	//create tables if they don't exist 
	public void createTables() {
        String sqlTM = "CREATE TABLE IF NOT EXISTS turingmachine("+
        		  " idturingmachine integer NOT NULL PRIMARY KEY AUTOINCREMENT,"+
        		  " name varchar(45) NOT NULL,"+
        		  " initialstate varchar(45) NOT NULL,"+
        		  " blank varchar(45) NOT NULL,"+
        		  " finalstates varchar(45) DEFAULT NULL,"+
        		  " comment varchar(256) DEFAULT NULL)";
        
        String sqlTr = "CREATE TABLE IF NOT EXISTS transition ("+
        		  "idtransition integer NOT NULL PRIMARY KEY AUTOINCREMENT,"+
        		  "currentstate varchar(45) NOT NULL,"+
        		  "symbolread char(1) NOT NULL,"+
        		  "nextstate varchar(45) NOT NULL,"+
        		  "symboltowrite char(1) NOT NULL,"+
        		  "move varchar(45) NOT NULL,"+
        		  "idturingmachine integer NOT NULL,"+
        		  "FOREIGN KEY (idturingmachine) REFERENCES turingmachine(idturingmachine) ON DELETE CASCADE)";
        try {
            connection.createStatement().execute(sqlTM);
            connection.createStatement().execute(sqlTr);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
	}
	//connect to the database
	public void connect() {
        try {
	    	connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	//close the database connection object
	public void close(){
        if(connection != null){
            try {
            	connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	//select all TM in the database
	public ResultSet selectAllMT() throws SQLException {
		String strSql = "SELECT * FROM turingmachine";
		return connection.createStatement().executeQuery( strSql );
	}
	//select a TM by its id
	public ResultSet selectMT(int selectedId) throws SQLException {
		String strSql = "SELECT * FROM turingmachine WHERE idturingmachine="+selectedId;
		return connection.createStatement().executeQuery( strSql );
	}
	//select a TM transitions
	public ResultSet selectTransitions(int selectedId) throws SQLException {
		String strSql = "SELECT * FROM transition WHERE idturingmachine="+selectedId;
		return connection.createStatement().executeQuery( strSql );
	}
	//insert a TM in the database
	public boolean insertMT(TuringMachine tm) {
		try{
			connection.setAutoCommit(false);
			String fs="";
			if(tm.getFinalstates().size()!=0) {
				for(String state:tm.getFinalstates()) {
					fs=fs+","+state;
				}
				fs=fs.substring(1, fs.length());
			}
			String strSql ="INSERT INTO turingmachine VALUES (null,'"+ tm.getName()+"','"+tm.getInitialState()+"','"+tm.getBlank()+"','"+fs+"','"
					+tm.getComment()+"')";
			PreparedStatement ps = connection.prepareStatement(strSql,Statement.RETURN_GENERATED_KEYS);
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			int id = 0;
			if (rs.next()) {
			    id = rs.getInt(1);
			} else {
				throw new SQLException();
			}
			for(Transition tr :tm.getTransitions()) {
				String str ="INSERT INTO transition VALUES (null,'"+ tr.getCurrentState()+"','"+tr.getSymbolRead()+"','"+tr.getNextState()+
						"','"+tr.getSymboleToWrite()+"','"+tr.getMove().toString()+"',"+id+")";
				connection.createStatement().executeUpdate(str);
			}
			connection.commit();
			return true;
		}catch(SQLException se){
			
		   try {
			connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	//delete a TM
	public int deleteMt(int selectedId) throws SQLException {
		String strSql = "Delete FROM turingmachine WHERE idturingmachine="+selectedId;
		return connection.createStatement().executeUpdate(strSql);
	}
}
