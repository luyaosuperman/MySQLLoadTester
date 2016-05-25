package com.MysqlLoadTest.GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GraphManager {
	
	private static Logger log = LogManager.getLogger(GraphManager.class); 
	
	public static LineChart<Number,Number>  lineChart = null;// = getLineChart();
	public static Label existingTestInfoLable = new Label("Existing Test");
	public static BorderPane existingTestBorder = new BorderPane();
	public static GridPane chartGrid = new GridPane();
	
	public static ToggleGroup chartGroup = new ToggleGroup();
	public static RadioButton chartByTime = new RadioButton();
	public static RadioButton chartByRow = new RadioButton();
	
	public static ArrayList<CheckBoxTestId> checkBoxList = new ArrayList<CheckBoxTestId>();
	
	public static ArrayList<Integer> testIdArrayCache = null;
	
	public static void getLineChart(){
		if (testIdArrayCache != null) {getLineChart(testIdArrayCache);}
	}
	
	public static void getLineChart(ArrayList<Integer> testIdArray){
		
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        //log.info("DataManager.chartType: "+ DataManager.chartType);
        
        testIdArrayCache = testIdArray;
        
		if      (DataManager.chartType == DataManager.CHARTBYTIME){xAxis.setLabel("mili Seconds");}
		else if (DataManager.chartType == DataManager.CHARTBYROW) {xAxis.setLabel("rows");}
		
		//log.info("DataManager.chartType: "+ DataManager.chartType);
        
        //creating the chart
        chartGrid.getChildren().remove(lineChart);
        
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("query per second ");
        //defining a series

        
        for (int testId: testIdArray) {
        	
            XYChart.Series series = new XYChart.Series();
            TestInfo testInfo = DataManager.getTestInfo(testId);
            ArrayList<long[]> TestData = DataManager.getTestData(testId); 
            series.setName(testInfo.getComment());
            //populating the series with data
            	for (long[] point: TestData){
            		series.getData().add(new XYChart.Data(point[0], point[1]));
            	}
				lineChart.getData().add(series);
			}
        
        GraphManager.chartGrid.add(GraphManager.lineChart, 0, 0);
        
        //return lineChart;
	}
	
	public static VBox getExistingTestVbox(){
		VBox vboxExistingTest = new VBox();
		checkBoxList.clear();
     	vboxExistingTest.setPadding(new Insets(10)); // Set all sides to 10
     	vboxExistingTest.setSpacing(8);              // Gap between nodes
     	//CheckBox checkBoxTest = new CheckBox("Test1");
     	Connection connect = ConnectionManager.getConnection("testreport");
     	PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "
					+ "id,timestamp,testType,threads,runCount,comment "
					+ "from testinfo;");
			rs = preparedStatement.executeQuery();
			while (rs.next()){
				int testId = rs.getInt(1);
				String comment = rs.getString(6);
				CheckBoxTestId checkBox = new CheckBoxTestId();//("Test: " + testId + " " + comment);
				checkBox.setText("Test: " + testId + " " + comment);
				checkBox.setTestId(testId);
				
				checkBox.setSelected(false);
				checkBox.setOnMouseMoved( e -> {
					existingTestInfoLable.setText(checkBox.getText());
				});
				
				checkBox.setOnMouseClicked( e -> {
					ArrayList<Integer> checkedTestList = new ArrayList<Integer>();
					for (CheckBoxTestId c: checkBoxList){
						if( c.isSelected() ){ checkedTestList.add(c.getTestId()); }
					}
					GraphManager.getLineChart(checkedTestList);
				});
				
				checkBoxList.add(checkBox);
				vboxExistingTest.getChildren().add(checkBox);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
     	//vboxExistingTest.getChildren().add(checkBoxTest);
     	return vboxExistingTest;
	}
	
	public static VBox getVboxExistingTestInfo(){
	 	VBox vboxExistingTestInfo = new VBox();
	 	vboxExistingTestInfo.setPadding(new Insets(10)); // Set all sides to 10
	 	vboxExistingTestInfo.setSpacing(8);              // Gap between nodes
	 	
	 	vboxExistingTestInfo.getChildren().add(GraphManager.existingTestInfoLable);
	 	
	 	chartByRow.setToggleGroup(chartGroup);
	 	chartByRow.setText("Chart By Row Count");
	 	chartByRow.setUserData("chartByRow");
	 	vboxExistingTestInfo.getChildren().add(GraphManager.chartByRow);

	 	chartByTime.setToggleGroup(chartGroup);
	 	chartByTime.setText("Chart by time elapsed");
	 	chartByTime.setSelected(true);
	 	chartByTime.setUserData("chartByTime");
	 	vboxExistingTestInfo.getChildren().add(GraphManager.chartByTime);
	 	
	 	chartGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
	 		public void changed (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
	 			if (chartGroup.getSelectedToggle() != null){
	 				String buttonText = chartGroup.getSelectedToggle().getUserData().toString();
	 				//log.info("buttonText: "+ buttonText);
	 				if (buttonText == "chartByRow"){DataManager.chartType = DataManager.CHARTBYROW;}
	 				else if (buttonText == "chartByTime"){DataManager.chartType = DataManager.CHARTBYTIME;}
	 			}
	 			getLineChart();
	 			//log.info("DataManager.chartType: "+ DataManager.chartType);
	 				
	 		}
	 		
	 	});
	 	
	 	
	 	
	 	return vboxExistingTestInfo;
	}
}
