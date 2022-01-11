package application;
	
import application.controller.MainController;
import application.database.DataBase;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		DataBase db = new DataBase();
		db.createDatabase("TuringMachine");
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		primaryStage.setTitle("Turing Machine Simulator");
		MainController controller = new MainController();
		controller.setWindow(primaryStage);
		controller.startWelcomeWindow(true,false);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
