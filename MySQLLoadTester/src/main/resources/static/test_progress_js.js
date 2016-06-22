google.charts.load('current', {'packages':['corechart']});

function getProgress(){
   	  var xhttp = new XMLHttpRequest();
   	  
   	  xhttp.onreadystatechange = function() {
	   	    if (xhttp.readyState == 4 && xhttp.status == 200) {
		   	   	 var jsonStr= xhttp.responseText;
		   	     //document.getElementById("demo").innerHTML=jsonStr;
		   	     var obj = JSON.parse(jsonStr)['TestInfo'];
		   	     var testProgress = obj['testProgress']
		   	     var testStatus = obj['testStatus']
		   	     var testId = obj['testId']
		   	     //console.log(testStatus)
		   	     
		   	     var fieldNameElement = document.getElementById("testStats")
		   	     fieldNameElement.textContent = "testStatus: " + getTestStatusText(testStatus) + " testProgress: " +  testProgress + " testId: " + testId
		   	     
			   	  var fieldNameElement = document.getElementById("progressBar")
			   	  fieldNameElement.textContent=testProgress*100+"%"
			   	  fieldNameElement.style.width=testProgress*100+"%"
			   	  if(testStatus==2){//finished, jump
			   		  window.location.replace("/get_graph?testId="+testId);
			   	  }
			   	  if (testStatus==1){
			   		  //console.log("draw")
			   		loadData(testId)
			   	  }
		   	     
	   	    }
   	  }
   	    
   	xhttp.open("GET", "/get_progress", true);
	xhttp.send();
}

function drawChart(rawData) {

	var data = google.visualization.arrayToDataTable(rawData);
    var options = {
      title: 'Test Result',
      curveType: 'function',
      legend: { position: 'bottom' }
    };

    var chart = new google.visualization.LineChart(document.getElementById('liveChart'));

    chart.draw(data, options);
  }
  
 function loadData(testId) {
	  var xhttp = new XMLHttpRequest();
	  
	  var rawData = null;
	  //var testIdArray = queryDict["testId"];
	  //var testIdArray={testId}
	  //console.log("testIdArray: ");
	  //console.log(testIdArray)
	  var queryString="testId="+testId;
	  /*for(var i = 0; i < testIdArray.length; i++) {
	      queryString += "testId=" + testIdArray[i];
	
	      //Append an & except after the last element
	      if(i < testIdArray.length - 1) {
	         queryString += "&";
	      }
	  }*/
	  
	  xhttp.onreadystatechange = function() {
	    if (xhttp.readyState == 4 && xhttp.status == 200) {
	   	 var jsonStr= xhttp.responseText;
	     //document.getElementById("demo").innerHTML=jsonStr;
	     var obj = JSON.parse(jsonStr);
	     
	     rawData = [obj["TestResult"]["columns"]];
	     //rawData=rawData.concat(obj["TestResult"]["columns"]);
	     rawData = rawData.concat(obj["TestResult"]["dataPoint"]);
	     
	     
	     drawChart(rawData);
	     
	    }
	  };
	  xhttp.open("GET", "/get_data?" + queryString, true);
	  xhttp.send();
}


function getTestStatusText(testStatus){
	switch (testStatus){
	case -1:
		return "PENDING"
		break;
	case 0:
		return "PREPARING"
		break;
	case 1:
		return "RUNNING"
		break;
	case 2:
		return "FINISHED"
		break;		
	}
}

setInterval(getProgress, 1000);