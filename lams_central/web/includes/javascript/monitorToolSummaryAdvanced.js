
// Toggles whether to display advanced options in monitor summary for tools
// TODO remove method once bootstrapping is completed
function toggleAdvancedOptionsVisibility(div, img, imageUrl)
{
	var treeClosedIcon = imageUrl + "/images/tree_closed.gif"; // 
	var treeOpenIcon = imageUrl + "/images/tree_open.gif";

	if (div.style.display == "block")
	{
		div.style.display = "none";
		img.src = treeClosedIcon;
	}
	else if (div.style.display == "none")
	{
		div.style.display = "block";
		img.src = treeOpenIcon;
	}
}

//check if jquery is loaded
if ((typeof jQuery != 'undefined') && (typeof submissionDeadlineSettings != 'undefined')) {
	//support for setting up submission deadline
	$(function(){
		$("#datetime").datetimepicker();
		if (submissionDeadlineSettings.submissionDeadline != "") {
			var date = new Date(eval(submissionDeadlineSettings.submissionDeadline));

			if ( typeof submissionDeadlineSettings.submissionDateString != 'undefined' ) {
				$("#dateInfo").html(submissionDeadlineSettings.submissionDateString );
			} else {
				$("#dateInfo").html( formatDate(date) );
			}

			// show the date area straight away as it has a date set!
			$("#restrictUsageDiv").addClass("show");
		}
	});	
	
	// fallback routine for when Java formatted dates are not available
	function formatDate(date) {
		var currHour = "" + date.getHours();
		if (currHour.length == 1) {
			currHour = "0" + currHour;
		}			
		var currMin = "" + date.getMinutes();
		if (currMin.length == 1) {
			currMin = "0" + currMin;
		}
		return $.datepicker.formatDate( 'mm/dd/yy', date ) + " " + currHour + ":" + currMin;
	}

	function setSubmissionDeadline() {
		// Need to get the timestamp in milliseconds since midnight Jan 1, 1970. 
		var moment = $("#datetime").data("DateTimePicker").date(); 
		if (moment == null) {
			return;
		}

		var reqIDVar = new Date();
		var parameterDelimiter = (submissionDeadlineSettings.setSubmissionDeadlineUrl.indexOf("?") == -1) ? "?" : "&"; 
		
		var url = submissionDeadlineSettings.setSubmissionDeadlineUrl + parameterDelimiter + "toolContentID=" + submissionDeadlineSettings.toolContentID + "&submissionDeadline=" +
					+ moment.valueOf() + "&reqID=" + reqIDVar.getTime();

		$.ajax({
			url : url,
			success : function(data) {
				$.growlUI(submissionDeadlineSettings.messageNotification, submissionDeadlineSettings.messageRestrictionSet);
				$("#datetimeDiv").hide();
				if ( data != '' ) {
					$("#dateInfo").html( data );
				} else {
					$("#dateInfo").html( formatDate(date) );
				}
				$("#dateInfoDiv").show();
			}
		});
	}
	function removeSubmissionDeadline() {
		var reqIDVar = new Date();
		var parameterDelimiter = (submissionDeadlineSettings.setSubmissionDeadlineUrl.indexOf("?") == -1) ? "?" : "&"; 
		
		var url = submissionDeadlineSettings.setSubmissionDeadlineUrl + parameterDelimiter + "toolContentID=" + submissionDeadlineSettings.toolContentID + "&submissionDeadline=" +
				"&reqID=" + reqIDVar.getTime();
		
		$.ajax({
			url : url,
			success : function() {
				$.growlUI(submissionDeadlineSettings.messageNotification, submissionDeadlineSettings.messageRestrictionRemoved);
				$("#dateInfoDiv").hide();
				$("#datetimeDiv").show();
				$("#datetime").val("");
			}
		});
	}
}

