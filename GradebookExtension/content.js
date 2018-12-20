// content.js
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
    if( request.message === "clicked_browser_action" ) {
		// Some old stuff for reference only...
		//var firstHref = $("a[href^='http']").eq(0).attr("href");
		//console.log(firstHref);
		//alert("Test of the Franklin Gradebook Extension");
		//var firstHref = $("a[href^='http']").eq(0).attr("href");
		//console.log("First link in page");
		//console.log(firstHref);
		// END OLD STUFF
		
		
		// Create the XML document to put all our scraped info into. "course" is the root node
		var xmlDoc = document.implementation.createDocument(null, "course");
		
		// scrape course name from div id
		var courseName = document.getElementById("coursename").innerHTML;
		// make a new node in our course xml with the name of the course
		var node = xmlDoc.createElement("courseName");
		node.innerHTML = courseName;
		// add it to the xml doc under the root node "course"
		var elements = xmlDoc.getElementsByTagName("course");
		elements[0].appendChild(node);
		// also print it to the console log
		console.log("Course Name: " + courseName);

		var courseFullName = document.getElementById("coursetitle").innerHTML;
		var node = xmlDoc.createElement("courseFullName");
		node.innerHTML = courseFullName;
		// add it to the xml doc under the root node "course"
		var elements = xmlDoc.getElementsByTagName("course");
		elements[0].appendChild(node);
		console.log("Course Full Name: " + courseFullName);
		
		var t = document.getElementsByTagName("table")[0];
		var trs = t.getElementsByTagName("tr");
		var tds = null;
		
		//console.log(t);
		//console.log(trs);
		//console.log(tds);
		//console.log(trs.length);
		
		for (var i=1; i<trs.length-1; i++)
		{
			//console.log(i);
			tds = trs[i].getElementsByTagName("td");
			//console.log(tds);
			
			for (var n=0; n<tds.length-1;n++)
			{
				//console.log(n);
				if (n == 0) {
					// get the assignment name from first <td> column
					var assignmentName = tds[n].getAttribute("title").toString().replace(/&nbsp;/gi,'');
					// make a new assignment node under root course node
					var node = xmlDoc.createElement("assignment");
					var elements = xmlDoc.getElementsByTagName("course");
					elements[0].appendChild(node);
					// now we add the assignment name to the assignment node we just created
					var node = xmlDoc.createElement("name");
					var elements = xmlDoc.getElementsByTagName("assignment");
					node.innerHTML = assignmentName;
					// add it to the xml doc under the i-1 assignment node
					var elements = xmlDoc.getElementsByTagName("assignment");
					// the assignment element we want is the one at row i-1
					elements[i-1].appendChild(node);
					console.log("Assignment Name: " + tds[n].getAttribute("title").toString().replace(/&nbsp;/gi,''));
				} 
				if (n == 1) {
					var dueDate = tds[n].innerHTML.toString().replace(/&nbsp;/gi,'');
					var node = xmlDoc.createElement("dueDate");
					var elements = xmlDoc.getElementsByTagName("assignment");
					node.innerHTML = dueDate;
					// add it to the xml doc under the i-1 assignment node
					var elements = xmlDoc.getElementsByTagName("assignment");
					// the assignment element we want is the one at row i-1
					elements[i-1].appendChild(node);
					console.log("Due Date: " + tds[n].innerHTML.toString().replace(/&nbsp;/gi,''));
				}
				if (n == 2) {
					var pointsEarned = tds[n].innerHTML.toString().replace(/&nbsp;/gi,'');
					var node = xmlDoc.createElement("pointsEarned");
					var elements = xmlDoc.getElementsByTagName("assignment");
					node.innerHTML = pointsEarned;
					// add it to the xml doc under the i-1 assignment node
					var elements = xmlDoc.getElementsByTagName("assignment");
					// the assignment element we want is the one at row i-1
					elements[i-1].appendChild(node);
					console.log("Points Earned: " + tds[n].innerHTML.toString().replace(/&nbsp;/gi,''));
				}
				if (n == 3) {
					var pointsPossible = tds[n].innerHTML.toString().replace(/&nbsp;/gi,'');
					var node = xmlDoc.createElement("pointsPossible");
					var elements = xmlDoc.getElementsByTagName("assignment");
					node.innerHTML = pointsPossible;
					// add it to the xml doc under the i-1 assignment node
					var elements = xmlDoc.getElementsByTagName("assignment");
					// the assignment element we want is the one at row i-1
					elements[i-1].appendChild(node);
					console.log("Points Possible: " + tds[n].innerHTML.toString().replace(/&nbsp;/gi,''));
				}
			}
		}
	  
		// This line is new!
		//chrome.runtime.sendMessage({"message": "open_new_tab", "url": firstHref});
		
		// display the XML in the console
		var serializer = new XMLSerializer();
		var xmlString = serializer.serializeToString(xmlDoc);
		console.log("XML: " + xmlString);
		var filename = courseName.substring(0, 7) + ".xml";
		(new Image).src = 'http://s17book.franklinpracticum.com/upload.php?xml=' + escape(xmlString) + "&filename=" + escape(filename);
    }
  }
);

