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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.ExecutionUnit.*;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;
@SuppressWarnings("restriction")
public class Gui extends Application {
	
	private static Connection connect;
	private static int testId =-1;
	private static LineChart  lineChart = null;// = getLineChart();
	
	private static Logger log = LogManager.getLogger(Gui.class); 
	
	public static void main(String[] args) {
        launch(args);
    }
	
	private static LineChart getLineChart(TestInfo testInfo, boolean refreshData){
		connect = ConnectionManager.getConnection("testreport");
		
		if (refreshData||testId == -1)
		{
			try {
				testId = com.MysqlLoadTest.ExecutionUnit.Application.runTest(testInfo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "+
							"a.systemNanoTime / 1000000, "+
							"a.totalExecutionCount, "+
							"a.totalExecutionCount - @lasttotalExecutionCount as intervalExecutionCount, "+
							"@lasttotalExecutionCount := a.totalExecutionCount "+
							"from testreport.testruntimeinfo a, "+
							"(select @lasttotalExecutionCount := 0) SQLVars "+
							"where testid = ?");
			preparedStatement.setInt(1, testId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
         //defining the axes
         final NumberAxis xAxis = new NumberAxis();
         final NumberAxis yAxis = new NumberAxis();
         xAxis.setLabel("mili Seconds");
         //creating the chart
         final LineChart<Number,Number> lineChart = 
                 new LineChart<Number,Number>(xAxis,yAxis);
                 
         lineChart.setTitle("query per second ");
         //defining a series
         XYChart.Series series = new XYChart.Series();
         series.setName("test result");
         //populating the series with data
         
         try {
			rs = preparedStatement.executeQuery();
			while (rs.next()){
				
				series.getData().add(new XYChart.Data(rs.getLong(1), rs.getLong(3)));
				//log.info("insert data into graph: "+ rs.getLong(1) + " " + rs.getLong(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         /*series.getData().add(new XYChart.Data(1, 23));
         series.getData().add(new XYChart.Data(12, 25));*/
         
         lineChart.getData().add(series);
         
         return lineChart;
	}
    	
	@Override public void start(Stage stage) {
		
         
   	 	stage.setTitle("Line Chart Sample");
         
         //////////////////////////////////////////////////////////////
         
         GridPane grid = new GridPane();
   	 	BorderPane border = new BorderPane();
   	 	
   	 	

         
         
         
         
         
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
	         Label runCountLable = new Label("runCount Threads");
	         TextField runCountTextField = new TextField();
	         runCountTextField.setText("30000");
	         
	         vboxNewTest.getChildren().add(threadsLable);
	         vboxNewTest.getChildren().add(threadsTextField);
	         vboxNewTest.getChildren().add(runCountLable);
	         vboxNewTest.getChildren().add(runCountTextField);
	         vboxNewTest.getChildren().add(tabPane);
	         
	         Button buttonRun = new Button("Run Test");
	         buttonRun.setPrefSize(100, 20);
	         buttonRun.setOnMouseClicked( e -> {
		        	 int threads = Integer.parseInt(threadsTextField.getText());
		        	 int runCount = Integer.parseInt(runCountTextField.getText());
		        	 
		        	 int testType = 1;
		        	 
		        	 TestInfo testInfo = new TestInfo(testType,threads,runCount);
		        	 
		        	 grid.getChildren().remove(lineChart);
		        	 lineChart = getLineChart(testInfo, true);
		        	 grid.add(lineChart, 0, 0);
	         });

	         Button buttonClear = new Button("Clear Result");
	         buttonClear.setPrefSize(100, 20);
	         buttonClear.setOnMouseClicked( e -> {
	        	 grid.getChildren().remove(lineChart);
	         });
	         
	         vboxNewTest.getChildren().addAll(buttonRun,buttonClear);

         tabNewTest.setContent(vboxNewTest);
         tabNewTest.setText("New Test");
         tabNewTest.setClosable(false);
         tabPane.getTabs().add(tabNewTest);
         
         Tab tabExistingTest = new Tab();
         	VBox vboxExistingTest = new VBox();
         	vboxExistingTest.setPadding(new Insets(10)); // Set all sides to 10
         	vboxExistingTest.setSpacing(8);              // Gap between nodes
         	CheckBox checkBoxTest = new CheckBox("Test1");
         	vboxExistingTest.getChildren().add(checkBoxTest);
         tabExistingTest.setText("Existing Tests");
         tabExistingTest.setContent(vboxExistingTest);
         tabExistingTest.setClosable(false);
         tabPane.getTabs().add(tabExistingTest);
         
         border.setLeft(tabPane);
         //border.setLeft(vbox);

         
         
         grid.setHgap(10);
         grid.setVgap(10);
         grid.setPadding(new Insets(0, 10, 0, 10));
         border.setCenter(grid);


         Scene scene = new Scene(border);
         stage.setScene(scene);
         stage.setTitle("Load Test");
         
         stage.show();
         
         /*Scene scene  = new Scene(grid,1280,720);
         
        
         stage.setScene(scene);
         stage.show();*/
        
    }
}
