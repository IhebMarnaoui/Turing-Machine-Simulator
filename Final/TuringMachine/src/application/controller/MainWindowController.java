package application.controller;


import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import  java.lang.Thread;
import java.sql.ResultSet;

import application.database.DataBase;
import application.model.Move;
import application.model.Tape;
import application.model.Transition;
import application.model.TuringMachine;
import application.model.Usage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class MainWindowController {
		
	@FXML
	private TextField nameTF;
	@FXML
	private TextField initStateTF;
	@FXML
	private TextField blankTF;
	@FXML
	private TextField finalStatesTF;
	@FXML
	private TextArea transitionsTA;
	@FXML
	private Button chargeButton;
	@FXML
	private TextField wordTF;
	@FXML
	private Button loadButton;
	@FXML
	private Button modifyButton;
	@FXML
	private Text currentStateText;
	@FXML	
	private Text rightTapeText;
	@FXML	
	private Text currentTapeText;
	@FXML	
	private Text leftTapeText;
	@FXML
	private Button haltButton;
	@FXML
	private Button nextStepButton;
	@FXML
	private Button initializeTapeButton;
	@FXML
	private Text resultText;
	@FXML
	private MenuItem commentMI; 
	@FXML
	private MenuItem saveMI; 
	@FXML
	private Text loadMessage;
	
	private MainController main;
	private TuringMachine turingMachine;
	private Tape tape;
	private int speed;
	
	public void initialize() { 
		this.speed=500;
		this.commentMI.setDisable(true);
		saveMI.setDisable(true);
		modifyButton.setVisible(false);
	}
	
	
	//Initialises The tape
	private void initTape() { 
		rightTapeText.setText(tape.getRight());
		currentTapeText.setText("|"+tape.getCurrent()+"|");
		leftTapeText.setText(tape.getLeft());
		haltButton.setDisable(false);
		nextStepButton.setDisable(false);
		resultText.setText("");
		if(turingMachine!=null) {
			turingMachine.init();
			currentStateText.setText(turingMachine.getInitialState());
		}
	}
	
	//Displays The final state of the TM.
	private void decision(boolean rejected) {
		 if(rejected){
				resultText.setFill(Color.RED);
				resultText.setText("Word rejected");
		} else {
			resultText.setFill(Color.GREENYELLOW);
			if(turingMachine.getUsage() == Usage.ACCEPT 
					&& turingMachine.getFinalstates().contains(turingMachine.getCurrentState()))
				resultText.setText("Word accepted");
			else if (turingMachine.getUsage() == Usage.CALCULATE)
				resultText.setText("Machine Stopped ");
		}
	}
	
	
	private void oneStep() throws NoSuchElementException {
		turingMachine.oneStep(tape);
		rightTapeText.setText(tape.getRight());
		currentTapeText.setText("|"+tape.getCurrent()+"|");
		leftTapeText.setText(tape.getLeft());
		currentStateText.setText(turingMachine.getCurrentState());
	}
	
	@FXML	
	public void onInitializeTape() throws Exception{
		tape = new Tape(wordTF.getText(), turingMachine.getBlank());
		this.initTape();
	}
	
	// Finishes all the steps until the machine is halted
	@FXML
	public void onHalt() throws Exception {	
		haltButton.setDisable(true);
		nextStepButton.setDisable(true);
		new Thread(() -> {
			boolean rejected=false;
			do {
				try {
					this.oneStep();
					Thread.sleep(speed);
				} catch (NoSuchElementException e) {
					rejected=true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			} while(!turingMachine.isHalt() && !rejected);
			this.decision(rejected);
			
		}).start();
	}
	
		@FXML
	public void onNextStep() {
		boolean rejected=false;
		try {
			this.oneStep();
		} catch (NoSuchElementException e) {
			rejected=true;
		}
		if(turingMachine.isHalt() || rejected) {
			decision(rejected);
			haltButton.setDisable(true);
			nextStepButton.setDisable(true);
		}
	}
	
	// Loads the Machine previously configured
	public void onLoad() {
		Set<Transition> transitions= new HashSet<Transition>();
		String[] transitionsText = transitionsTA.getText().split("\n");
		try {
			for(String line : transitionsText) {
				String[] component = line.split(",");
				Move move=null;
				switch(component[4].toUpperCase()) {
					case "L": case "LEFT": 
						move=Move.LEFT; break;
					case "R": case "RIGHT":
						move=Move.RIGHT; break;
					case "S": case "STOP":
						move=Move.STOP; break;
					default:
						throw new IOException();
				}
				if(component[1].trim().length()!=1 || component[3].trim().length()!=1 || component[0].trim().length()==0 || component[2].trim().length()==0 )
					throw new IOException();
				transitions.add(new Transition(component[0].trim(), component[1].trim().charAt(0), component[2].trim(), component[3].trim().charAt(0),move));
			}
			Set<String> finalstates=null;
			if(finalStatesTF.getText().trim() != "") {
				finalstates = new HashSet<String>();
				for(String finalState : finalStatesTF.getText().trim().split(",")) {
					if(finalState.trim().length()!=0)
						finalstates.add(finalState.trim());
				}
			}
			if(blankTF.getText().trim().length()!=1 || nameTF.getText().trim().length()==0 || initStateTF.getText().trim().length()==0 )
				throw new IOException();
			turingMachine = new TuringMachine(nameTF.getText().trim(),transitions,initStateTF.getText().trim(),blankTF.getText().trim().charAt(0),finalstates);
			initializeTapeButton.setDisable(false);
			if(turingMachine!=null) {
				turingMachine.init();
				currentStateText.setText(turingMachine.getInitialState());
			}
			saveMI.setDisable(false);
			loadMessage.setText(null);
			loadButton.setVisible(false);
			
			editAttributes(false);
			modifyButton.setVisible(true);
			
		} catch(Exception e) {
			loadMessage.setText("Please check the TM declaration");
		}
	}
	
	@FXML
	public void onModify() {
		editAttributes(true);
		modifyButton.setVisible(false);
		loadButton.setVisible(true);
	}
	
	
	//Allows to modify the TM configuration after loading.
	private void editAttributes(boolean bool) {
		nameTF.setEditable(bool);
		initStateTF.setEditable(bool);
		blankTF.setEditable(bool);
		finalStatesTF.setEditable(bool);
		transitionsTA.setEditable(bool);
	}
	
	@FXML
	public void onHome() {
		try {
			main.startWelcomeWindow(true,false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//Exit the program.
	@FXML
	public void onClose() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to leave?");
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("application/icon.png"));
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			System.exit(0);
	}
	
	
	//Allows to change the execution speed
	@FXML
	public void onSpeed(ActionEvent actionEvent) {
		switch( ( (MenuItem)actionEvent.getSource() ).getId() ) {
			case "slow": 
				speed=1000;
				break; 
			case "average": 
				speed=500;
				break; 
			case "fast":
				speed=100;				
		}
	}

	
	//Displays the "How to use" window.
	@FXML
	public void onHowToUse() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("How to use");
		alert.setHeaderText(null);
		alert.setContentText("Manual guide to the application:\r\n" + "The left area represents the Turing Machine definition: Name, initial state, final states(must be seperated with ',' if multiple final states), blank caractere, and the transitions list.\r\n" + "Once you've completly set up your machine, click on the load button to try it. The execution is shown to the right, where you input the word to try on your machine and click on Initialize tape. You can now see a step-by-step demonstration of the implementation of your machine. The machine is halted if there are no possible transitions left to perform.");
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("application/icon.png"));
		alert.show();
	}
	
	
	//Displays the "Comments" window
	@FXML
	public void onComment() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(turingMachine.getComment());
		((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("application/icon.png"));
		alert.show();
	}
		
	@FXML
	public void onChange() throws IOException {
		if(this.tape!=null)
			main.setTape(this.tape);
		main.startWelcomeWindow(false,false);
	}
	
	
	//Allows to add a new TM to the database.
	@FXML
	public void onSave() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Save Turing machine");
		dialog.setHeaderText(null);
		dialog.setContentText("enter a comment:");
		((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("application/icon.png"));
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			if(result.get()!="")
				turingMachine.setComment(result.get().trim());
		}
		DataBase db = new DataBase();
		try {
			db.connect();
			db.insertMT(this.turingMachine);
			dialog.close();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Sucess");
			alert.setHeaderText(null);
			alert.setContentText("TM inserted with success");
			((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("application/icon.png"));
			alert.showAndWait();
		} catch (Exception e) {
			dialog.close();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Error while inserting this TM in the database");
			((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("application/icon.png"));
			alert.showAndWait();
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	@FXML
	public void onDelete() throws IOException {
		main.startWelcomeWindow(false,true);
	}

	
	//Allows to set the TM
	public void setTuringMachine(int selectedId) throws Exception {
		if(selectedId != 0) {
			DataBase db = new DataBase();
			db.connect();
				String transitionsText="";
				Set<Transition> transitions = new HashSet<Transition>();
				ResultSet resultSet = db.selectTransitions(selectedId);
				while( resultSet.next() ) {
					Move move;
					switch(resultSet.getString("move")) {
						case "LEFT": move=Move.LEFT; break;
						case "RIGHT": move=Move.RIGHT; break;
						default: move=Move.STOP; break;
					}
					transitions.add(new Transition(resultSet.getString("currentstate"),
													resultSet.getString("symbolread").charAt(0),
													resultSet.getString("nextstate"),
													resultSet.getString("symboltowrite").charAt(0),
													move)
					);
					transitionsText=transitionsText+resultSet.getString("currentstate")+","+
							resultSet.getString("symbolread")+","+resultSet.getString("nextstate")+","+
							resultSet.getString("symboltowrite")+","+move.toString()+"\n";
				}
				
				ResultSet resultSetMT = db.selectMT(selectedId);
				resultSetMT.next();
				String finalStateText=resultSetMT.getString("finalstates");
				Set<String> finalstates=null;
				if(finalStateText!=null && !finalStateText.trim().equals("")) {
					finalstates = new HashSet<String>();
					for(String finalState : finalStateText.trim().split(",")) {
						finalstates.add(finalState.trim());
					}
				} 
				turingMachine = new TuringMachine(resultSetMT.getString("name"),transitions, resultSetMT.getString("initialstate"), resultSetMT.getString("blank").charAt(0),finalstates);
				turingMachine.setComment(resultSetMT.getString("comment"));
				
				nameTF.setText(resultSetMT.getString("name"));
				initStateTF.setText(resultSetMT.getString("initialstate"));
				blankTF.setText(resultSetMT.getString("blank"));
				finalStatesTF.setText(finalStateText);
				transitionsTA.setText(transitionsText);

			db.close();
			
			loadButton.setVisible(false);
			initializeTapeButton.setDisable(false);
			
			editAttributes(false);
			commentMI.setDisable(turingMachine.getComment()== null || turingMachine.getComment().equals(""));		
		}
	}
	
	public void setMain(MainController main) {
        this.main = main;
    }
	
	public void setTape(Tape tape) throws Exception{
		this.tape = tape;
		if(tape!=null)
			this.initTape();
	}
}
