package com.MysqlLoadTest.GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.ExecutionUnit.*;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;
@SuppressWarnings("restriction")
public class Gui extends Application {
	
	private static Connection connect;
	private static int testId =-1;

	
	private static Logger log = LogManager.getLogger(Gui.class); 
	
	public static void main(String[] args) {
        launch(args);
    }
	

    	
	@Override public void start(Stage stage) {
		
         
   	 	stage.setTitle("MySQL Load Tester");
         
         //////////////////////////////////////////////////////////////
   	 	BorderPane mainBorder = new BorderPane();
         
         TabPane tabPane = new TabPane();
         Tab tabNewTest = new Tab();

	         VBox vboxNewTest = new VBox();
	         vboxNewTest.setPadding(new Insets(10)); // Set all sides to 10
	         vboxNewTest.setSpacing(8);              // Gap between nodes
	
	         Text title = new Text("Data");
	         title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	         vboxNewTest.getChildren().add(title);
	         
	         
	         Label threadsLable = new Label("Concurrent Threads");
	         TextField threadsTextField = new TextField();
	         threadsTextField.setText("10");
	         Label runCountLable = new Label("runCount per Thread");
	         TextField runCountTextField = new TextField();
	         runCountTextField.setText("30000");
	         
	         Label commentLable = new Label("Test Comment");
	         TextField commentTextField = new TextField();
	         commentTextField.setText("");
	         
	         vboxNewTest.getChildren().add(threadsLable);
	         vboxNewTest.getChildren().add(threadsTextField);
	         vboxNewTest.getChildren().add(runCountLable);
	         vboxNewTest.getChildren().add(runCountTextField);
	         vboxNewTest.getChildren().add(commentLable);
	         vboxNewTest.getChildren().add(commentTextField);
	         
	         Button buttonRun = new Button("Run Test");
	         buttonRun.setPrefSize(100, 20);
	         buttonRun.setOnMouseClicked( e -> {
		        	 int threads = Integer.parseInt(threadsTextField.getText());
		        	 int runCount = Integer.parseInt(runCountTextField.getText());
		        	 String comment = commentTextField.getText();
		        	 
		        	 int testType = 1;
		        	 
		        	 TestInfo testInfo = new TestInfo(testType,threads,runCount,comment);
		        	 
		        	 //GraphManager.chartGrid.getChildren().remove(GraphManager.lineChart);
		        	 
		        	 
		        	 int testId = DataManager.runTest(testInfo);
		        	 ArrayList<Integer>  testIdArray = new ArrayList<Integer>();
		        	 testIdArray.add(testId);
		        	 GraphManager.getLineChart(testIdArray);
		        	 //GraphManager.chartGrid.add(GraphManager.lineChart, 0, 0);
		        	 GraphManager.existingTestBorder.setCenter(GraphManager.getExistingTestVbox());
	         });

	         Button buttonClear = new Button("Clear Result");
	         buttonClear.setPrefSize(100, 20);
	         buttonClear.setOnMouseClicked( e -> {
	        	 GraphManager.chartGrid.getChildren().remove(GraphManager.lineChart);
	         });
	         
	         vboxNewTest.getChildren().addAll(buttonRun,buttonClear);

         tabNewTest.setContent(vboxNewTest);
         tabNewTest.setText("New Test");
         tabNewTest.setClosable(false);
         tabPane.getTabs().add(tabNewTest);
         
         Tab tabExistingTest = new Tab();
	     GraphManager.existingTestBorder.setCenter(GraphManager.getExistingTestVbox());
	     GraphManager.existingTestBorder.setBottom(GraphManager.getVboxExistingTestInfo());
         	
         tabExistingTest.setText("Existing Tests");
         tabExistingTest.setContent(GraphManager.existingTestBorder);
         tabExistingTest.setClosable(false);
         tabPane.getTabs().add(tabExistingTest);
         
         mainBorder.setLeft(tabPane);
         //border.setLeft(vbox);

         
         
         GraphManager.chartGrid.setHgap(10);
         GraphManager.chartGrid.setVgap(10);
         GraphManager.chartGrid.setPadding(new Insets(0, 10, 0, 10));
         mainBorder.setCenter(GraphManager.chartGrid);


         Scene scene = new Scene(mainBorder);
         stage.setScene(scene);
         stage.setTitle("Load Test");
         
         stage.show();
        
    }
}
