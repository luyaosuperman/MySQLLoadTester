      google.charts.load('current', {'packages':['corechart']});
      google.charts.setOnLoadCallback(initPage);

      /*var rawData = [
                     ['Year', 'Sales', 'Expenses'],
                     [2004,  1000,      400],
                     [2005,  1170,      460],
                     [2006,  660,       1120],
                     [2007,  1030,      540]
                   ]*/
      
      
      var queryDict = {}
      function parseParameters(){
    	  location.search.substr(1).split("&").forEach(function(item) {
    		  console.log("item: "+item)
    		  var key = item.split("=")[0];
    		  var value = item.split("=")[1];
    		  if (queryDict[key]==undefined){//new key, new array
    			  queryDict[key]=[value]
    		  }else{//existing key, add value to array
    			  queryDict[key].push(value)
    		  }
    	  })
    	  console.log(queryDict);
      }
      
      function initPage(){
    	  parseParameters();
    	  loadData();
    	  loadExistTests();
    	  loadItemsTests();
      }
      
      function drawChart(rawData) {

    	var data = google.visualization.arrayToDataTable(rawData);
        var options = {
          title: 'Test Result',
          curveType: 'function',
          legend: { position: 'bottom' }
        };

        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

        chart.draw(data, options);
      }
      
     function loadData() {
   	  var xhttp = new XMLHttpRequest();
   	  
   	  var rawData = null;
   	  var testIdArray = queryDict["testId"];
   	  //console.log("testIdArray: ");
   	  //console.log(testIdArray)
   	  var queryString="";
      for(var i = 0; i < testIdArray.length; i++) {
          queryString += "testId=" + testIdArray[i];

          //Append an & except after the last element
          if(i < testIdArray.length - 1) {
             queryString += "&";
          }
      }
   	  
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
     
    function createTestIdForm( data, formName, formMethod, formAction){
		var form = document.createElement('FORM');
		form.name=formName;
		form.method=formMethod;
		form.action=formAction;
		
		var tbl = document.createElement('table');
		tbl.style.width = '100%';
	    tbl.setAttribute('border', '1');
		var tbdy = document.createElement('tbody');
		//th
		var tr = document.createElement('tr');
		
		var th = document.createElement('th');
		th.appendChild(document.createTextNode("Select"))
		tr.appendChild(th)
		var properties = Object.keys(data[0])
		
		for (var i=0; i<properties.length; i++){
			var th = document.createElement('th');
			th.appendChild(document.createTextNode(properties[i]))
			tr.appendChild(th)
		}
		tbdy.appendChild(tr);
		
		//td
	    for (var i = 0; i < data.length; i++) {
	        var tr = document.createElement('tr');
	        
	        //checkbox
	        var td = document.createElement('td');
	        var checkbox = document.createElement('input');
	        checkbox.type='checkbox'
	        checkbox.name='testId';
	        checkbox.value=data[i]["id"]
	        if(queryDict['testId'].indexOf(checkbox.value)!=-1){
	        	checkbox.checked = true;
	        }
	        //checkbox.text=document.createTextNode(testList[i]["testId"])
	        td.appendChild(checkbox)
	        tr.appendChild(td)

			//content			        
	        for (var j = 0; j < properties.length; j++) {
	            var td = document.createElement('td');
	            td.appendChild(document.createTextNode(data[i][properties[j]]))
	            tr.appendChild(td)
	        }
	        tbdy.appendChild(tr);
	    }
	    tbl.appendChild(tbdy);
	    
	    form.appendChild(tbl);
	    
	    //button
	    var button = document.createElement('input');
	    button.type='submit';
	    button.value='submit';
	    form.appendChild(button);
	    
	    return form
    } 
     
    function loadExistTests(){
    	var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				var obj = JSON.parse(xhttp.responseText)["TestList"];
				//var properties = obj["properties"]
				var testList = obj["testList"]
				
			    var div = document.getElementById('existingTests');
			    div.appendChild(createTestIdForm(testList,'existingTests','GET','/get_graph'))
				
			}
		};
		xhttp.open("GET", "/get_testList", true);
		xhttp.send();
    }
    
    
    
    
    
    
    function createItemsForm( data, formName, formAction){
    	console.log(data[0])
		var form = document.createElement('FORM');
		form.name=formName;
		//form.method=formMethod;
		form.action=formAction;
		
		
		
		var tbl = document.createElement('table');
		tbl.style.width = '100%';
	    tbl.setAttribute('border', '1');
		var tbdy = document.createElement('tbody');
		//th
		var tr = document.createElement('tr');
		
		var th = document.createElement('th');
		th.appendChild(document.createTextNode("Select"))
		tr.appendChild(th)
		var properties = Object.keys(data[0])
		
		for (var i=0; i<properties.length; i++){
			var th = document.createElement('th');
			th.appendChild(document.createTextNode(properties[i]))
			tr.appendChild(th)
		}
		tbdy.appendChild(tr);
		
		//td
	    for (var i = 0; i < data.length; i++) {
	        var tr = document.createElement('tr');
	        
	        //checkbox
	        var td = document.createElement('td');
	        var checkbox = document.createElement('input');
	        checkbox.type='checkbox'
	        checkbox.name='itemid';
	        checkbox.value=data[i]["itemid"]
	        /*if(queryDict['testId'].indexOf(checkbox.value)!=-1){
	        	checkbox.checked = true;
	        }*/
	        //checkbox.text=document.createTextNode(testList[i]["testId"])
	        td.appendChild(checkbox)
	        tr.appendChild(td)

			//content			        
	        for (var j = 0; j < properties.length; j++) {
	            var td = document.createElement('td');
	            td.appendChild(document.createTextNode(data[i][properties[j]]))
	            tr.appendChild(td)
	        }
	        tbdy.appendChild(tr);
	    }
	    tbl.appendChild(tbdy);
	    
	    form.appendChild(tbl);
	    
	    //button
	    var button = document.createElement('input');
	    button.type='submit';
	    button.value='submit';
	    //button.onclick="loadZabbixHistory()"
	    form.appendChild(button);
	    
	    return form
    } 
    
    function loadItemsTests(){
    	var xhttp = new XMLHttpRequest();
    	
    	var testIdArray = queryDict["testId"];
    	var testId = testIdArray[0];
    	
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				var obj = JSON.parse(xhttp.responseText).ArrayList;
				//var properties = obj["properties"]
				console.log("testId: " + testId)
				console.log(obj)
				
			    var div = document.getElementById('zabbixItems');
			    div.appendChild(createItemsForm(obj,'zabbixItems','javascript:loadZabbixHistory()'))
				
			}
		};
		xhttp.open("GET", "/get_zabbix_items?testId="+testId, true);
		xhttp.send();
    }
      