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
		   	     //console.log(testId)
		   	     
		   	     var fieldNameElement = document.getElementById("testStats")
		   	     fieldNameElement.textContent = "testStatus: " + testStatus + " testProgress: " +  testProgress + " testId: " + testId
		   	     
		   	     //var ProgressBar = require('progressbar.js');

			   	// Assuming we have an empty <div id="container"></div> in
			   	// HTML
			   	//var bar = new ProgressBar.Line('#progressBar', {easing: 'easeInOut'});
			   //	bar.animate(testProgress);  // Value from 0.0 to 1.0
		   	     
		   	  var fieldNameElement = document.getElementById("progressBar")
		   	  fieldNameElement.textContent=testProgress*100+"%"
		   	  fieldNameElement.style.width=testProgress*100+"%"
		   	     
	   	    }
   	  }
   	    
   	xhttp.open("GET", "/get_progress", true);
	xhttp.send();
}

setInterval(getProgress, 1000);