package com.MysqlLoadTest.GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GraphManager {
	
	public static LineChart  lineChart = null;// = getLineChart();
	public static Label existingTestInfoLable = new Label("Existing Test");
	public static BorderPane existingTestBorder = new BorderPane();
	public static GridPane chartGrid = new GridPane();
	
	public static ArrayList<CheckBoxTestId> checkBoxList = new ArrayList<CheckBoxTestId>();

	public static LineChart getLineChart(ArrayList<Integer> testIdArray){
		
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("mili Seconds");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
                
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
        
        
        
        return lineChart;
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
					GraphManager.chartGrid.getChildren().remove(GraphManager.lineChart);
					GraphManager.lineChart = GraphManager.getLineChart(checkedTestList);
					GraphManager.chartGrid.add(GraphManager.lineChart, 0, 0);
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
	
}
