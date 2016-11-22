import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class UIThread extends Application {
	
	String modelName;
	String fbxAbsFilePath;
	String fbxCanonFilePath;
	String saveDirectory;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("FST Generator");
		
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25,25,25,25));
		
		Text sceneTitle = new Text("FST Generator");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		root.add(sceneTitle, 0, 0, 2, 1);
		
		// label for getting FBX file
		Label fbxLabel = new Label("FBX File:");
		root.add(fbxLabel, 0, 1);
		
		// display FBX file path
		TextField fbxFileDisplay = new TextField();
		fbxFileDisplay.setEditable(false);
		root.add(fbxFileDisplay, 1, 1);
		
		// button for opening file chooser
		Button chooseFbx = new Button("Select FBX File");
		chooseFbx.setMaxWidth(Double.MAX_VALUE);
		
		// label for save directory
		Label saveLabel = new Label("Save Directory:");
		root.add(saveLabel, 0, 2);

		// display for save directory
		TextField saveDirectoryDisplay = new TextField();
		saveDirectoryDisplay.setEditable(false);
		root.add(saveDirectoryDisplay, 1, 2);
		
		// button for changing directory
		Button changeDirectory = new Button("Change Directory");
		changeDirectory.setMaxWidth(Double.MAX_VALUE);
		
		// on click, select file, display file path in text view, save data
		chooseFbx.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select FBX file");
				fileChooser.getExtensionFilters().add(new ExtensionFilter("FBX Files", "*.fbx"));
				File retrievedFile = fileChooser.showOpenDialog(primaryStage);
				// TODO: Null pointer if closed out of fileChooser.
				fbxAbsFilePath = retrievedFile.getAbsolutePath();
				
				try {
					fbxCanonFilePath = retrievedFile.getCanonicalPath();
				} catch (IOException e) {
					fbxCanonFilePath = fbxAbsFilePath;
				}
				
				// get name of model
				String tempName = retrievedFile.getName();
				modelName = tempName.substring(0, tempName.indexOf("."));
				
				// update save directory
				saveDirectory = fbxAbsFilePath.substring(0,fbxAbsFilePath.indexOf(modelName));
				
				// show file path
				fbxFileDisplay.setText(fbxCanonFilePath);
				saveDirectoryDisplay.setText(fbxCanonFilePath.substring(0, fbxCanonFilePath.indexOf(modelName)));
				
				// display end of file path, not beginning
				fbxFileDisplay.positionCaret(fbxFileDisplay.getText().length());
				saveDirectoryDisplay.positionCaret(saveDirectoryDisplay.getText().length());
				
			}
		});
		
		root.add(chooseFbx, 2, 1);
		
		// choose directory, save data and update display
		changeDirectory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Choose Save Directory");
				File defaultDirectory = new File(fbxAbsFilePath.substring(0, fbxAbsFilePath.indexOf(modelName)));
				directoryChooser.setInitialDirectory(defaultDirectory);
				
				File retrievedDirectory = directoryChooser.showDialog(primaryStage);
				saveDirectory = retrievedDirectory.getAbsolutePath() + "/";
				
				try {
					saveDirectoryDisplay.setText(retrievedDirectory.getCanonicalPath());
				} catch (IOException e) {
					saveDirectoryDisplay.setText(retrievedDirectory.getAbsolutePath());
				}
				
				// show end of file path instead of beginning
				saveDirectoryDisplay.selectPositionCaret(saveDirectoryDisplay.getLength());
				
			}		
		});
		
		root.add(changeDirectory, 2, 2);
		
		// TODO: don't need texture field?
		/*
		// label for texture field
		Label textureLabel = new Label("Texture File:");
		root.add(textureLabel, 0, 2);
		
		// create display for texture directory
		TextField textureFileDisplay = new TextField();
		root.add(textureFileDisplay, 1, 2);
		
		// button for opening file chooser
		Button chooseTexture = new Button("Select Texture Directory");
		
		// on click, select file, save it's path and display it in textureFileDisplay
		chooseTexture.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		root.add(chooseTexture, 2, 2);
		*/
		
		Button submitButton = new Button("Generate FST");
		submitButton.setMaxWidth(Double.MAX_VALUE);
		
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(fbxFileDisplay.getText().equals("")) {
					Alert alertDialog = new Alert(AlertType.WARNING);
					alertDialog.setTitle("No FBX File");
					alertDialog.setHeaderText("Please select a FBX file");
					alertDialog.showAndWait();
				}
				else {
					DataManager data = new DataManager(modelName, fbxAbsFilePath, saveDirectory);
					// ensure file is written properly, then alert 
					if(data.generateFile()) {
						Alert completedWriting = new Alert(AlertType.INFORMATION);
						completedWriting.setTitle("Finished Writing");
						completedWriting.setHeaderText(null);
						completedWriting.setContentText("FST File was successfully saved at :" + "\n" 
														+ saveDirectory + modelName + "_Mapping.fst");
						completedWriting.showAndWait();
						System.exit(0);
					} else {
						Alert alertDialog = new Alert(AlertType.WARNING);
						alertDialog.setTitle("Error writing file");
						alertDialog.setHeaderText("There was an error writing the file. Please try again.");
						alertDialog.showAndWait();
					}
					
				}
			}
		});
		
		root.add(submitButton, 2, 3);
		
		
		Scene myScene = new Scene(root);
		primaryStage.setScene(myScene);
		primaryStage.show();
		
	}
	
}
