package application.controller;

import java.io.IOException;

import application.model.Tape;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class MainController {

	private Stage window;
	private int selectedId;
	private Tape tape;
	
	
	public void startMainWindow(boolean fromHome) throws Exception{ // Main window conception
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MainWindow.fxml"));
		Parent root = loader.load();
		root.getStylesheets().add(getClass().getResource("/application/view/style.css").toExternalForm());
		MainWindowController controller = loader.getController();
		controller.setMain(this);
		controller.setTuringMachine(selectedId);
		if(!fromHome)
			controller.setTape(tape);
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setX(Screen.getPrimary().getBounds().getHeight()*0.4);
		window.setY(Screen.getPrimary().getBounds().getWidth()*0.05);
        window.show();
    }
	
	public void startWelcomeWindow(boolean isHome, boolean delete) throws IOException { // Start window conception
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/WelcomeWindow.fxml"));
        Parent root = loader.load();
		WelcomeWindowController controller = loader.getController();
		controller.setMain(this);
		controller.setIsHome(isHome);
		controller.setDelete(delete);
		if(!isHome) {
			Stage sc=new Stage();
			sc.setScene(new Scene(root));
			sc.getIcons().add(new Image(getClass().getResourceAsStream("/application/icon.png")));
			sc.setX(Screen.getPrimary().getBounds().getHeight()*0.5);
			sc.setY(Screen.getPrimary().getBounds().getWidth()*0.12);
			sc.setResizable(false);
			sc.setAlwaysOnTop(true);
			sc.initOwner(window);
		    sc.initModality(Modality.WINDOW_MODAL);
			controller.setStage(sc);
			sc.show();
		} else {
			controller.setStage(window);
			window.setScene(new Scene(root));
			window.setX(Screen.getPrimary().getBounds().getHeight()*0.45);
			window.setY(Screen.getPrimary().getBounds().getWidth()*0.12);
			window.show();
		}
	}
	
	public void setSelectedId(int id) {
		selectedId = id;
	}
	
	public void setTape(Tape tape) {
		this.tape=tape;
	}

	public void setWindow(Stage window) {
		this.window = window;
	}
	
	public int getSelectedId() {
		return selectedId;
	}
	
}
