package application.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import application.database.DataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WelcomeWindowController {
	
	@FXML
	private Text welcomeText;
	@FXML
	private Button submitButton;
	@FXML
	private ComboBox<String> mtComboBox;
	
	private MainController mainController;
	private int selectedId;
	private boolean isHome;
	private boolean delete;
	private Stage stage;
	private List<Integer> identifiers;

	
	// Initialise the database 
	public void initialize() throws Exception {
		DataBase db = new DataBase();
		db.connect();
		ResultSet resultSet = db.selectAllMT();
		
		identifiers = new ArrayList<Integer>();
		identifiers.add(0,0);
		int index=1;
		while( resultSet.next() ) {
			identifiers.add(index,resultSet.getInt( "idturingmachine" ));
			mtComboBox.getItems().add((index++)+".  "+ resultSet.getString( "name" ));
		}
		
		db.close();
		submitButton.setDisable(true);
	}
	
	// Allows to choose One TM
	public void onchooseMT() throws Exception {
		selectedId=identifiers.get(Integer.parseInt(mtComboBox.getValue().split(Pattern.quote("."))[0]));
	    submitButton.setDisable(false);
	}
	
	@FXML
	public void onSubmit() throws Exception {
		if(delete) {
			DataBase db = new DataBase();
			db.connect();
			db.deleteMt(selectedId);
			db.close();
			stage.close();
			mainController.startWelcomeWindow(false, true);
			mainController.setSelectedId(0);
			mainController.startMainWindow(true);;
		}else {
			if(!isHome) {
				stage.close();
			}
			mainController.setSelectedId(selectedId);
			mainController.startMainWindow(isHome);
		}
	}
	@FXML
	public void onSubmitPressedKEy(KeyEvent keyEvent) throws Exception {
	   if(keyEvent.getCode().equals(KeyCode.ENTER)){
	     this.onSubmit();
	   }
	}
	
	//Selects the main controller
	public void setMain(MainController mainController) {
        this.mainController = mainController;
    }	
	
	// Sets a new JavaFx Stage
	public void setStage(Stage stage) {
        this.stage = stage;
    }	
	
	// Allows to Set the "Change TM" screen
	public void setIsHome(boolean isHome) {
        this.isHome = isHome;
		welcomeText.setText(isHome? "Welcome in our Turing Machine simulator" : "Change Turing Machine");
    }
	
	//Allows to delete a TM from the database
	public void setDelete(boolean delete) {
        this.delete = delete;
        if(!delete)
    		mtComboBox.getItems().add("0. create a new Turing Machine");
        else 
        	welcomeText.setText("Delete a Turing machine");
    }
}
