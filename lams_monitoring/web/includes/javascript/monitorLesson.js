﻿// ********** GLOBAL VARIABLES **********
// copy of lesson/branching SVG so it does no need to be fetched every time
// HTML with SVG of the lesson
var originalSequenceCanvas = null;
// DIV container for lesson/branching SVG
// it gets accessed so many times it's worth to cache it here
var sequenceCanvas = null;
// ID of currently shown branching activity; if NULL, the whole lesson is shown
var sequenceBranchingId = null;
// info box show timeout
var sequenceInfoTimeout = 10000;
// how learners in pop up lists are currently sorted
var sortOrderAsc = {
	learnerGroup : false,
	classLearner : false,
	classMonitor : false
};
// container for learners' progress bars metadata
var bars = null;
// placeholder for single learner's progress bar and title
var learnerProgressCellsTemplate = null;
// for synchronisation purposes
var learnersRefreshInProgress = false;
var sequenceRefreshInProgress = false;

// total number of learners with ongoing progress
var numberActiveLearners = 0;
// page in Learners tab
var learnerProgressCurrentPageNumber = 1;
// search phrase in Learners tab
var learnersSearchPhrase = null;

//auto refresh all tabs every 30 seconds
var autoRefreshInterval = 30 * 1000;
var autoRefreshIntervalObject = null;
// when user is doing something, do not auto refresh
var autoRefreshBlocked = false;

// ********* GENERAL TABS FUNCTIONS *********

function initTabs(){
	$('#tabs').tabs({
		'activate' : function(event, ui) {
			var sequenceInfoDialog = $('#sequenceInfoDialog');
			if (ui.newPanel.attr('id') == 'tabSequence') {
				if (sequenceInfoDialog.length > 0
						&& !sequenceInfoDialog.dialog('option', 'showed')) {
					sequenceInfoDialog.dialog('open');
				}
			} else if (sequenceInfoDialog.dialog('isOpen')) {
				sequenceInfoDialog.dialog('close');
			}
		}
	});
}


//********** LESSON TAB FUNCTIONS **********

/**
 * Sets up lesson tab.
 */
function initLessonTab(){
	// sets export portfolio availability
	$('#exportAvailableField').change(function(){
		var checked = $(this).is(':checked');
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : 'learnerExportPortfolioAvailable',
				'learnerExportPortfolio' : checked,
				'lessonID'      : lessonId
			}
		});
	});
	
	// sets presence availability
	$('#presenceAvailableField').change(function(){
		var checked = $(this).is(':checked');
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : 'presenceAvailable',
				'presenceAvailable' : checked,
				'lessonID'      : lessonId
			},
			success : function() {
				updatePresenceAvailableCount();
				
				if (checked) {
					$('#imAvailableField').attr('disabled', null);
					alert(LABELS.LESSON_PRESENCE_ENABLE_ALERT);
				} else {
					$('#imAvailableField').attr({
						'checked'  : null,
						'disabled' : 'disabled'
					});
					alert(LABELS.LESSON_PRESENCE_DISABLE_ALERT);
				}
			}
		});
	});
	
	// sets instant messaging availability
	$('#imAvailableField').change(function(){
		var checked = $(this).is(':checked');
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : 'presenceImAvailable',
				'presenceImAvailable' : checked,
				'lessonID'      : lessonId
			},
			success : function() {
				if (checked) {
					$('#openImButton').css('display', 'inline');
					alert(LABELS.LESSON_IM_ENABLE_ALERT);
				} else {
					$('#openImButton').css('display', 'none');
					alert(LABELS.LESSON_IM_DISABLE_ALERT);
				}
			}
		});
	});
	
	// sets up calendar for schedule date choice
	$('#scheduleDatetimeField').datetimepicker({
		'minDate' : 0
	});
	
	// sets up dialog for editing class
	$('#classDialog').dialog({
		'autoOpen'  : false,
		'height'    : 360,
		'width'     : 700,
		'minWidth'  : 700,
		'modal'     : true,
		'resizable' : true,
		'show'      : 'fold',
		'hide'      : 'fold',
		'open'      : function(){
			autoRefreshBlocked = true;
			// reset sort order
			sortOrderAsc['classLearner'] = false;
			sortOrderAsc['classMonitor'] = false;
			sortDialogList('classLearner');
			sortDialogList('classMonitor');
			colorDialogList('classLearner');
			colorDialogList('classMonitor');
			
			var selectedLearners = getSelectedClassUserList('classLearnerList');
			$(this).dialog('option', 'initSelectedLearners', selectedLearners);
		},
		'close' : function(){
			autoRefreshBlocked = false;
		},
		'buttons' : [
		             {
		            	'text'   : 'Save',
		            	'id'     : 'classDialogSaveButton',
		            	'click'  : function() {
		            		var removedLearners = [],
		            			dialog = $(this),
		            			initSelectedLearners = dialog.dialog('option', 'initSelectedLearners'),
		            			learners = getSelectedClassUserList('classLearnerList'),
		            			monitors = getSelectedClassUserList('classMonitorList');
		            		
		            		// check for learners removed from lesson
		            		$.each(initSelectedLearners, function(index, selectedLearnerId){
		            			if ($.inArray(selectedLearnerId, learners) == -1) {
		            				removedLearners.push(selectedLearnerId);
		            			}
		            		});
		            		
		            		// check if monitoring user really wants to remove progress
		            		if (removedLearners.length > 0 && !confirm(LABELS.LEARNER_GROUP_REMOVE_PROGRESS)){
		            			removedLearners = [];
		            		}
		            		
		            		$.ajax({
		            			url : LAMS_URL + 'monitoring/monitoring.do',
		            			cache : false,
		            			data : {
		            				'method'    	  : 'updateLessonClass',
		            				'lessonID'  	  : lessonId,
		            				'learners'  	  : learners.join(),
		            				'monitors'  	  : monitors.join(),
		            				'removedLearners' : removedLearners.join()
		            			},
		            			success : function() {
		            				dialog.dialog('close');
		            				if (removedLearners.length > 0) {
		            					refreshMonitor();
		            				} else {
		            					refreshMonitor('lesson');
		            				}
		            			}
		            		});
						}
		             },
		             {
		            	'text'   : 'Cancel',
		            	'id'     : 'classDialogCancelButton',
		            	'click'  : function() {
							$(this).dialog('close');
						} 
		             }
		]
	});

	$('#classLearnerSortButton').click(function(){
		sortDialogList('classLearner');
	});	
	$('#classMonitorSortButton').click(function(){
		sortDialogList('classMonitor');
	});
	
	// sets up dialog for emailing learners
	$('#emailDialog').dialog({
		'autoOpen'  : false,
		'height'    : 530,
		'width'     : 700,
		'modal'     : true,
		'resizable' : false,
		'show'      : 'fold',
		'hide'      : 'fold',
		'title'     : LABELS.EMAIL_BUTTON,
		'open'      : function(){
			autoRefreshBlocked = true;
			$('#emailFrame').attr('src',
					LAMS_URL + 'emailUser.do?method=composeMail&lessonID=' + lessonId
					+ '&userID=' + $(this).dialog('option', 'userId'));
		},
		'close' : function(){
			autoRefreshBlocked = false;
		}
	});
}


/**
 * Shows all learners in the lesson class.
 */
function showLessonLearnersDialog() {
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'    : 'getClassMembers',
			'lessonID'  : lessonId,
			'role'      : 'LEARNER',
			'classOnly' : true
		},
		success : function(response) {
			 showLearnerGroupDialog(null, LABELS.LESSON_GROUP_DIALOG_CLASS, response, false, false, true);
		}
	});
}

/**
 * Changes lesson state and updates widgets.
 */
function changeLessonState(){
	var method = null;
	var state = +$('#lessonStateField').val();
	switch (state) {
		case 3: //STARTED
			switch (lessonStateId) {
				case 4: //SUSPENDED
					method = "unsuspendLesson";
					break;
				case 6: //ARCHIVED
					method = "unarchiveLesson";
					break;
			}
			break;
		case 4: 
			method = "suspendLesson";
			break;
		case 6: 
			method = "archiveLesson";
			break;
		case 7: //FINISHED
			if (confirm(LABELS.LESSON_REMOVE_ALERT)){
				if (confirm(LABELS.LESSON_REMOVE_DOUBLECHECK_ALERT)) {
					method = "removeLesson";
				}
			}
			break;
	}
	
	if (method) {
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'    : method,
				'lessonID'  : lessonId
			},
			success : function() {
				if (state == 7) {
					// user chose to finish the lesson, close monitoring and refresh the lesson list
					window.parent.closeMonitorLessonDialog(true);
				} else {
					refreshMonitor('lesson');
				}
			}
		});
	}
}


/**
 * Updates widgets in lesson tab according to respose sent to refreshMonitor()
 */
function updateLessonTab(){
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'    : 'getLessonDetails',
			'lessonID'  : lessonId
		},
		
		success : function(response) {
			// update lesson state label
			lessonStateId = +response.lessonStateID;
			var label = null;
			switch (lessonStateId) {
				case 1:
					label = LABELS.LESSON_STATE_CREATED;
					break;
				case 2:
					label = LABELS.LESSON_STATE_SCHEDULED;
					break;
				case 3:
					label = LABELS.LESSON_STATE_STARTED;
					break;
				case 4:
					label = LABELS.LESSON_STATE_SUSPENDED;
					break;
				case 5:
					label = LABELS.LESSON_STATE_FINISHED;
					break;
				case 6:
					label = LABELS.LESSON_STATE_ARCHIVED;
					break;
				case 7:
					label = LABELS.LESSON_STATE_REMOVED;
					break;
			}
			$('#lessonStateLabel').text(label);
			
			// update available options in change state dropdown menu
			var selectField = $('#lessonStateField');
			// remove all except "Select status" option
			selectField.children('option:not([value="-1"])').remove();
			switch (lessonStateId) {
				case 3:
					$('<option />').attr('value', 4).text(LABELS.LESSON_STATE_ACTION_DISABLE).appendTo(selectField);
					$('<option />').attr('value', 6).text(LABELS.LESSON_STATE_ACTION_ARCHIVE).appendTo(selectField);
					$('<option />').attr('value', 7).text(LABELS.LESSON_STATE_ACTION_REMOVE).appendTo(selectField);
					break;
				case 4:
					$('<option />').attr('value', 3).text(LABELS.LESSON_STATE_ACTION_ACTIVATE).appendTo(selectField);
					$('<option />').attr('value', 6).text(LABELS.LESSON_STATE_ACTION_ARCHIVE).appendTo(selectField);
					$('<option />').attr('value', 7).text(LABELS.LESSON_STATE_ACTION_REMOVE).appendTo(selectField);
					break;
				case 5:
					break;
				case 6:
					$('<option />').attr('value', 3).text(LABELS.LESSON_STATE_ACTION_ACTIVATE).appendTo(selectField);
					$('<option />').attr('value', 7).text(LABELS.LESSON_STATE_ACTION_REMOVE).appendTo(selectField);
					break;
			}
			
			// show/remove widgets for lesson scheduling
			var scheduleControls = $('#scheduleDatetimeField, #scheduleLessonButton, #startLessonButton');
			var startDateField = $('#lessonStartDateSpan');
			switch (lessonStateId) {
				 case 1:
					 scheduleControls.css('display','inline');
					 startDateField.css('display','none');
					 break;
				 case 2:
					 scheduleControls.css('display','none');
					 startDateField.text(response.startDate).add('#startLessonButton').css('display','inline');
					 break;
				default: 			
					scheduleControls.css('display','none');
				 	startDateField.text(response.startDate).css('display','inline');
				 	break;
			}
		}
	});
	
	updatePresenceAvailableCount();
}


function scheduleLesson(){
	var date = $('#scheduleDatetimeField').val();
	if (date) {
		$.ajax({
			dataType : 'xml',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			data : {
				'method'          : 'startOnScheduleLesson',
				'lessonID'        : lessonId,
				'lessonStartDate' : date
			},
			success : function() {
				refreshMonitor('lesson');
			}
		});
	} else {
		alert(LABELS.LESSON_ERROR_SCHEDULE_DATE);
	}
}


function startLesson(){
	$.ajax({
		dataType : 'xml',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'          : 'startLesson',
			'lessonID'        : lessonId
		},
		success : function() {
			refreshMonitor('lesson');
		}
	});
}


/**
 * Stringifies user IDs who were selected in Edit Class dialog. 
 */
function getSelectedClassUserList(containerId) {
	var list = [];
	$('#' + containerId).children('div.dialogListItem').each(function(){
		if ($('input:checked', this).length > 0){
			list.push($(this).attr('userId'));
		}
	});
	return list;
}


function openChatWindow(){
	// variables are set in JSP page
	window.open(LAMS_URL + 'learning/lessonChat.jsp?lessonID=' + lessonId 
			+ '&presenceEnabledPatch=true&presenceImEnabled=true&presenceShown=true&createDateTime='
			+ createDateTimeStr
			,'Chat'
			,'width=650,height=350,resizable=no,scrollbars=no,status=no,menubar=no,toolbar=no');
}


function showEmailDialog(userId){
	$('#emailDialog').dialog('option',{
		'userId'        : userId
	}).dialog('open');
}


function closeEmailDialog(){
	$('#emailFrame').attr('src', null);
	$('#emailDialog').dialog('close');
}


function updatePresenceAvailableCount(){
	var checked = $('#presenceAvailableField').is(':checked');
	var counter = $('#presenceAvailableCount');
	if (checked) {
		$.ajax({
			dataType : 'json',
			url : LAMS_URL + 'PresenceChat.do',
			cache : false,
			data : {
				'method'    : 'getChatContent',
				'lessonID'      : lessonId
			},
			success : function(result) {
				$('span', counter).text(result.roster.length);
				counter.css('display', null);
			}
		});

	} else {
		counter.css('display', 'none');
	}
}


function selectLearnerURL(){
	$('#learnerURLField').select().focus().blur(function(){
		$('#copyLearnerURL').hide();
	});
	$('#copyLearnerURL').show();
}

//********** SEQUENCE TAB FUNCTIONS **********

/**
 * Sets up the sequence tab.
 */
function initSequenceTab(){
    // initialise lesson dialog
	$('#learnerGroupDialog').dialog({
			'autoOpen'  : false,
			'height'    : 360,
			'width'     : 330,
			'minWidth'  : 330,
			'modal'     : true,
			'resizable' : true,
			'show'      : 'fold',
			'hide'      : 'fold',
			'open'      : function(){
				autoRefreshBlocked = true;
				// reset sort order
				sortOrderAsc['learnerGroup'] = false;
				sortDialogList('learnerGroup');
				colorDialogList('learnerGroup');
				// until operator selects an user, buttons remain disabled
				$('button.learnerGroupDialogSelectableButton').blur().removeClass('ui-state-hover')
					.attr('disabled', 'disabled');
			},
			'close' 	: function(){
				autoRefreshBlocked = false;
			},
			'buttons' : [
			             {
			            	'text'   : LABELS.FORCE_COMPLETE_BUTTON,
			            	'id'     : 'learnerGroupDialogForceCompleteButton',
			            	'class'  : 'learnerGroupDialogSelectableButton',
			            	'click'  : function() {
			            		var selectedLearner = $('#learnerGroupList div.dialogListItemSelected');
			            		// make sure there is only one selected learner
			            		if (selectedLearner.length == 1) {
			            			// go to "force complete" mode, similar to draggin user to an activity
			            			var activityId = $(this).dialog('option', 'activityId');
			            			var dropArea = sequenceCanvas.add('#completedLearnersContainer');
			            			dropArea.css('cursor', 'url('
			            					+ LAMS_URL + 'images/icons/user.png),pointer')
			            				.one('click', function(event) {
			            					dropArea.off('click').css('cursor', 'default');
			            					forceComplete(activityId, selectedLearner.attr('userId'), 
			            							selectedLearner.text(), event.pageX, event.pageY);
			            				});
				            		$(this).dialog('close');
				            		alert(LABELS.FORCE_COMPLETE_CLICK.replace('[0]', selectedLearner.text()));
			            		}
							}
			             },
			             {
			            	'text'   : LABELS.VIEW_LEARNER_BUTTON,
			            	'id'     : 'learnerGroupDialogViewButton',
			            	'class'  : 'learnerGroupDialogSelectableButton',
			            	'click'  : function() {
			            		var selectedLearner = $('#learnerGroupList div.dialogListItemSelected');
			            		if (selectedLearner.length == 1) {
			            			// open pop up with user progress in the given activity
			            			openPopUp(selectedLearner.attr('viewUrl'), "LearnActivity", 600, 800, true);
			            		}
							}
			             },
			             {
			            	'text'   : LABELS.EMAIL_BUTTON,
			            	'id'     : 'learnerGroupDialogEmailButton',
			            	'class'  : 'learnerGroupDialogSelectableButton',
			            	'click'  : function() {
			            		var selectedLearner = $('#learnerGroupList div.dialogListItemSelected');
			            		if (selectedLearner.length == 1) {
			            			showEmailDialog(selectedLearner.attr('userId'));
			            		}
			            	}	
			             },
			             {
			            	'text'   : LABELS.CLOSE_BUTTON,
			            	'id'     : 'learnerGroupDialogCloseButton',
			            	'click'  : function() {
								$(this).dialog('close');
							} 
			             }
			]
		});
	
	$('#learnerGroupSortButton').click(function(){
		sortDialogList('learnerGroup');
	});
	
	// small info box on Sequence tab, activated when the tab is showed
	$('#sequenceInfoDialog').dialog({
		'autoOpen'   : false,
		'height'     : 35,
		'width'      : 290,
		'modal'      : false,
		'resizable'  : false,
		'show'       : 'fold',
		'hide'       : 'fold',
		'dialogClass': 'dialog-no-title',
		'position'   : {my: "left top",
					   at: "left top+10",
					   of: '#sequenceTopButtonsContainer'
				      },
		'open'      : function(){
			var dialog = $(this);
			// show only once in this Monitor
			dialog.dialog('option', 'showed', true);
			// close after given time
			setTimeout(function(){
				dialog.dialog('close');
			}, sequenceInfoTimeout);
		}
	});
	
	$('#forceBackwardsDialog').dialog({
		'autoOpen'  : false,
		'modal'     : true,
		'resizable' : false,
		'minWidth'  : 350,
		'show'      : 'fold',
		'hide'      : 'fold',
		'title'		: LABELS.FORCE_COMPLETE_BUTTON,
		'open'      : function(){
			autoRefreshBlocked = true;
		},
		'close' 	: function(){
			autoRefreshBlocked = false;
		},
		'buttons' : [
		             {
		            	'text'   : LABELS.FORCE_COMPLETE_REMOVE_CONTENT_NO,
		            	'click'  : function() {
		            		$(this).dialog('close');
		            		forceCompleteExecute($(this).dialog('option', 'learnerId'),
       							 				 $(this).dialog('option', 'activityId'),
       							 				 false);
						}
		             },
		             {
		            	'text'   : LABELS.FORCE_COMPLETE_REMOVE_CONTENT_YES,
		            	'click'  : function() {
							$(this).dialog('close');
		            		forceCompleteExecute($(this).dialog('option', 'learnerId'),
		            							 $(this).dialog('option', 'activityId'),
		            							 true);
						}
		             },
		             {
		            	'text'   : LABELS.CLOSE_BUTTON,
		            	'click'  : function() {
							$(this).dialog('close');
						} 
		             }
		]
	});
}
	


/**
 * Updates learner progress in sequence tab according to respose sent to refreshMonitor()
 */
function updateSequenceTab() {
	if (sequenceRefreshInProgress) {
		return;
	}
	sequenceRefreshInProgress = true;
	
	if (originalSequenceCanvas) {
		// put bottom layer, LD SVG
		sequenceCanvas.html(originalSequenceCanvas);
	} else {
		// fetch SVG just once, since it is immutable
		$.ajax({
			dataType : 'text',
			url : LAMS_URL + 'home.do',
			async : false,
			cache : false,
			data : {
				'method'    : 'createLearningDesignThumbnail',
				'svgFormat' : 1,
				'ldId'      : ldId,
				'branchingActivityID' : sequenceBranchingId
			},
			success : function(response) {
				originalSequenceCanvas = response;
				sequenceCanvas = $('#sequenceCanvas')
					// remove previously set padding and dimensions, if any
					.removeAttr('style')
					.html(originalSequenceCanvas)
					// if it was faded out by showBranchingSequence()
					.fadeIn();
				
				var canvasHeight = sequenceCanvas.height();
				var canvasWidth = sequenceCanvas.width();
				var svg = $('svg', sequenceCanvas);
				var canvasPaddingTop = canvasHeight/2 - svg.attr('height')/2;
				var canvasPaddingLeft = canvasWidth/2 - svg.attr('width')/2;

				if (canvasPaddingTop > 0) {
					sequenceCanvas.css({
						'padding-top' : canvasPaddingTop,
						'height'      : canvasHeight - canvasPaddingTop		
					});
				}
				if (canvasPaddingLeft > 0) {
					sequenceCanvas.css({
						'padding-left' : canvasPaddingLeft,
						'width'        : canvasWidth - canvasPaddingLeft		
					});
				}
			}
		});
	}
	
	// clear all completed learner icons except the door
	$('#completedLearnersContainer :not(img#completedLearnersDoorIcon)').remove();
	
	var sequenceTopButtonsContainer = $('#sequenceTopButtonsContainer');
	if ($('img#sequenceCanvasLoading', sequenceTopButtonsContainer).length == 0){
		$('#sequenceCanvasLoading')
				.clone().appendTo(sequenceTopButtonsContainer)
				.css('display', 'block');
	}
	
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'    : 'getLessonProgress',
			'lessonID'  : lessonId,
			'branchingActivityID' : sequenceBranchingId
		},		
		success : function(response) {
			// remove the loading animation
			$('img#sequenceCanvasLoading', sequenceTopButtonsContainer).remove();

			var learnerCount = 0;
			$.each(response.activities, function(){
				if (this.learners) {
					// are there any learners in this or any activity?
					learnerCount += this.learners.length;
					// put learner icons on each activity shape
					addLearnerIcons(this);
				}
			});
			
			if (learnerCount > 0) {
				// IMPORTANT! Reload SVG, otherwise added icons will not get displayed
				sequenceCanvas.html(sequenceCanvas.html());
			}
			
			var completedLearners = response.completedLearners;
			var learnerTotalCount = learnerCount + (completedLearners ? completedLearners.length : 0 );
			$('#learnersStartedPossibleCell').text(learnerTotalCount + ' / ' + response.numberPossibleLearners);
			addCompletedLearnerIcons(completedLearners, learnerTotalCount);
			
			$.each(response.activities, function(activityIndex, activity){
				addLearnerIconsHandlers(activity);
				
				if (activity.url || activity.isBranching) {
					// find activity group, if it is not hidden
					$('g#' + activity.id, sequenceCanvas)
						.css('cursor', 'pointer')
						.dblclick(
							// different behaviour for regular/branching activities
							activity.isBranching ? 
							function(){
								showBranchingSequence(activity.id);
							}
							:
							function(){
								// double click on activity shape to open Monitoring for this activity
								openPopUp(LAMS_URL + activity.url, "MonitorActivity", 720, 900, true, true);
							}
					);
				}
			});	
			
			sequenceRefreshInProgress = false;
		}
	});
}


/**
 * Forces given learner to move to activity indicated on SVG by coordinated (drag-drop)
 */
function forceComplete(currentActivityId, learnerId, learnerName, x, y) {
	autoRefreshBlocked = true;
	// check all activities and "users who finished lesson" bar
	$('rect[id^="act"], g polygon', sequenceCanvas).add('#completedLearnersContainer').each(function(){
		// find which activity learner was dropped on
		var act = $(this);
		var actX = act.offset().left;
		var actY = act.offset().top;
		var actWidth = act.width();
		var actHeight = act.height();
		if (!actWidth) {
			actWidth = +act.attr('width');
			actHeight = +act.attr('height');
		}
		if (!actWidth && act.is('polygon')){
			// just for Gate activity
			var polygonPoints = act.attr('points').split(' ');
			actWidth = +polygonPoints[5].split(',')[0] - +polygonPoints[2].split(',')[0];
			actHeight = +polygonPoints[0].split(',')[1] - +polygonPoints[3].split(',')[1];
		}
		var actEndX = actX + actWidth;
		var actEndY = actY + actHeight;
		
		if (x >= actX && x<= actEndX && y>= actY && y<=actEndY) {
			var targetActivityId = null;
			var executeForceComplete = false;
			
			if (act.attr('id') == 'completedLearnersContainer') {
				executeForceComplete =  currentActivityId && confirm(LABELS.FORCE_COMPLETE_END_LESSON_CONFIRM
						.replace('[0]',learnerName));
			} else {
				var targetActivityId = act.parent().attr('id');
				if (currentActivityId != targetActivityId) {
					
					var precedingActivityId = currentActivityId,
						targetActivityName = act.is('polygon') ? "Gate" 
							: act.siblings('text[id^="TextElement"]').text();
					
					// find out if we are moving learner forward or backwards
					while (precedingActivityId){
						// find transition line and extract activity IDs from them
						var transitionLine = $('line[id$="to_' 
								+ precedingActivityId + '"]:not([id^="arrow"])'
								, sequenceCanvas);
						precedingActivityId = transitionLine.length == 1 ? 
								transitionLine.attr('id').split('_')[0] : null;
						if (targetActivityId == precedingActivityId) {
							break;
						}
					};
					
					// check if the target activity was found or we are moving the learner from end of lesson
					if (!currentActivityId || precedingActivityId) {
						// move the learner backwards
						$('#forceBackwardsDialog').text(LABELS.FORCE_COMPLETE_REMOVE_CONTENT
									.replace('[0]', learnerName).replace('[1]', targetActivityName))
									.dialog('option', {
										'learnerId' : learnerId,
										'activityId': targetActivityId
									})
									.dialog('open');
						// so autoRefreshBlocked = false is not set
						return;
					} else {
						// move the learner forward
						executeForceComplete = confirm(LABELS.FORCE_COMPLETE_ACTIVITY_CONFIRM
									.replace('[0]', learnerName).replace('[1]', targetActivityName));
					}
				}
			}
			
			if (executeForceComplete) {
				
				forceCompleteExecute(learnerId, targetActivityId, false);
			}
			// we found our target, stop iteration
			return false;
		}
	});
	
	autoRefreshBlocked = false;
}


/**
 * Tell server to force complete the learner.
 */
function forceCompleteExecute(learnerId, activityId, removeContent) {
	$.ajax({
		dataType : 'text',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'     		 : 'forceComplete',
			'lessonID'   		 : lessonId,
			'learnerID'  		 : learnerId,
			'activityID' 		 : activityId,
			'removeContent'		 : removeContent
		},
		success : function(response) {
			// inform user of result
			alert(response);
									
			// progress changed, show it to monitor
			refreshMonitor('sequence');
		}
	});
}


/**
 * Draw user icons on top of activities.
 */
function addLearnerIcons(activity) {
	var isGate = false,
		actX = null,
		actY = null,
		activityGroup = $('g#' + activity.id, sequenceCanvas),
		activityShape = $('rect[id="act' + activity.id + '"]', activityGroup);
	if (activityShape.length == 0){
		// is it Gate activity?
		activityShape = $('polygon', activityGroup);
		if (activityShape.length > 0){
			isGate = true;
			var polygonPoints = activityShape.attr('points').split(' ');
			var polygonStartPoints = polygonPoints[4].split(',');
			actX = +polygonStartPoints[0];
			actY = +polygonStartPoints[1] - 10;
		} else {
			// unknown or invisible shape (System Gate?)
			return;
		}
	} else {
		actX = +activityShape.attr('x') + 1;
		actY = +activityShape.attr('y') + 1;
	}
	
	// add group of users icon
	var actRightBorder = actX + (isGate? 40 : +activityShape.attr('width')),
		groupTitle = activity.learners.length + ' ' + LABELS.LEARNER_GROUP_COUNT
		+ ' ' + LABELS.LEARNER_GROUP_SHOW,
	// if icons do not fit in shape anymore, show a group icon
		element = appendXMLElement('image', {
		'id'         : 'act' + activity.id + 'learnerGroup',
		'x'          : actRightBorder - 19,
		'y'          : actY + 1,
		'height'     : 16,
		'width'      : 16,
		'xlink:href' : LAMS_URL + 'images/icons/group.png'
	}, null, activityGroup[0]);
	appendXMLElement('title', null, groupTitle, element);
	// add a small number telling how many learners are in the group
	element = appendXMLElement('text', {
		'id'         : 'act' + activity.id + 'learnerGroupText',
		'x'          : actRightBorder - 10,
		'y'          : actY + 24,
		'text-anchor': 'middle',
		'font-family': 'Verdana',
		'font-size'  : 8
	}, activity.learners.length, activityGroup[0]);
	appendXMLElement('title', null, groupTitle, element);

	var actTooltip = LABELS.LEARNER_GROUP_LIST_TITLE;
	// draw single user icons for the first few
	if (!isGate) {
		$.each(activity.learners, function(learnerIndex, learner){
			var learnerDisplayName = getLearnerDisplayName(learner);
			actTooltip += '\n' + learnerDisplayName;
			
			if (learnerIndex < 7) {
				element = appendXMLElement('image', {
					'id'         : 'act' + activity.id + 'learner' + learner.id,
					'x'          :  actX + learnerIndex*15,
					'y'          :  actY,
					'height'     : 16,
					'width'      : 16,
					'xlink:href' : LAMS_URL + 'images/icons/user.png'
				}, null, activityGroup[0]);
				appendXMLElement('title', null, learnerDisplayName, element);
			}
		});
	}
	
	appendXMLElement('title', null, actTooltip, activityGroup[0]);
}


/**
 * After SVG refresh, add click/dblclick/drag handlers to user icons.
 */
function addLearnerIconsHandlers(activity) {
	if (!activity.learners) {
		return;
	}
	
	var activityGroup = $('g#' + activity.id, sequenceCanvas);
	if (activityGroup.length == 0) {
		// the activity is probably hidden (branching child, system gate)
		return;
	}
	// gate activity does not allows users' view
	var usersViewable = $('polygon', activityGroup).length == 0;
	
	$.each(activity.learners, function(learnerIndex, learner){
		var learnerIcon = $('image[id="act' + activity.id + 'learner' + learner.id + '"]'
				,activityGroup);
		learnerIcon .css('cursor', 'pointer')
		  // drag learners to force complete activities
		  .draggable({
			'appendTo'    : '#tabSequence',
			'containment' : '#tabSequence',
		    'distance'    : 20,
		    'scroll'      : false,
		    'cursorAt'	  : {'left' : 10, 'top' : 15},
			'helper'      : function(event){
				// copy of the icon for dragging
				return $('<img />').attr('src', LAMS_URL + 'images/icons/user.png');
			},
			'start' : function(){
				autoRefreshBlocked = true;
			},
			'stop' : function(event, ui) {
				// jQuery droppable does not work for SVG, so this is a workaround
				forceComplete(activity.id, learner.id, getLearnerDisplayName(learner, true),
						      ui.offset.left, ui.offset.top);
			}
		});
		
		if (usersViewable) {
			learnerIcon.dblclick(function(event){
				 // double click on learner icon to see activity from his perspective
				event.stopPropagation();
				var url = LAMS_URL + 'monitoring/monitoring.do?method=getLearnerActivityURL&userID=' 
					               + learner.id + '&activityID=' + activity.id + '&lessonID=' + lessonId;
				openPopUp(url, "LearnActivity", 600, 800, true);
			});
		}
	});
	
	
	var learnerGroupIcon = $('*[id^="act' + activity.id + 'learnerGroup"]', activityGroup);
	// 0 is for no group icon, 2 is for icon + digits
	if (learnerGroupIcon.length == 2) {
		var activityName = $('text[id^="TextElement"]', activityGroup).text();
		learnerGroupIcon.dblclick(function(event){
			 // double click on learner icon to see activity from his perspective
			event.stopPropagation();
			showLearnerGroupDialog(activity.id, activityName, activity.learners, true, usersViewable);
		})
	}
}


/**
 * Add learner icons in "finished lesson" bar.
 */
function addCompletedLearnerIcons(learners, learnerTotalCount) {
	var iconsContainer = $('#completedLearnersContainer');
	var completedLearnerCount = (learners ? learners.length : 0 );
	// show (current/total) label
	$('<span />').attr({
		'title' : LABELS.LEARNER_FINISHED_COUNT
			.replace('[0]', completedLearnerCount).replace('[1]', learnerTotalCount)
	}).text('(' + completedLearnerCount + '/' + learnerTotalCount + ')')
	  .appendTo(iconsContainer);
	
	if (learners) {
		// create learner icons, along with handlers
		$.each(learners, function(learnerIndex, learner){
			// maximum 55 icons in the bar
			if (learnerIndex < 55) {
				// make an icon for each learner
				$('<img />').attr({
					'src' : LAMS_URL + 'images/icons/user.png',
					'title'      : getLearnerDisplayName(learner)
				}).css('cursor', 'pointer')
				// drag learners to force complete activities
				  .draggable({
					'appendTo'    : '#tabSequence',
					'containment' : '#tabSequence',
				    'distance'    : 20,
				    'scroll'      : false,
				    'cursorAt'	  : {'left' : 10, 'top' : 15},
					'helper'      : function(event){
						// copy of the icon for dragging
						return $('<img />').attr('src', LAMS_URL + 'images/icons/user.png');
					},
					'start' : function(){
						autoRefreshBlocked = true;
					},
					'stop' : function(event, ui) {
						// jQuery droppable does not work for SVG, so this is a workaround
						forceComplete(null, learner.id, getLearnerDisplayName(learner, true),
								      ui.offset.left, ui.offset.top);
					}
				})
				.appendTo(iconsContainer);
			}
		});
		
		// show a group icon
		$('<img />').attr({
			'src' : LAMS_URL + 'images/icons/group.png',
			'title'      : LABELS.LEARNER_GROUP_SHOW
		}).css('cursor', 'pointer')
		  .dblclick(function(){
			showLearnerGroupDialog(null, LABELS.LEARNER_FINISHED_DIALOG_TITLE, learners, true, false);
		}).appendTo(iconsContainer);
	}
}


/**
 * Shows Edit Class dialog for class manipulation.
 */
function showClassDialog(){
	var learners = [];
	var monitors = [];
	
	// fetch available and alredy participation learners and monitors
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		async : false,
		data : {
			'method'    : 'getClassMembers',
			'lessonID'  : lessonId,
			'role'      : 'LEARNER',
			'classOnly' : false
		},
		success : function(response) {
			learners = response;
		}
	});
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		async : false,
		data : {
			'method'    : 'getClassMembers',
			'lessonID'  : lessonId,
			'role'      : 'MONITOR',
			'classOnly' : false
		},
		success : function(response) {
			monitors = response;
		}
	});
	
	// fill lists
	fillClassDialogList('classLearner', learners, false);
	fillClassDialogList('classMonitor', monitors, true);

	$('#classDialog')
		.dialog('option',
			{
			 'title' : LABELS.LESSON_EDIT_CLASS
			})
		.dialog('open');
}


/**
 * Fills class member list with user information.
 */
function fillClassDialogList(listId, users, disableCreator) {
	var list = $('#' + listId + 'List').empty();
	var selectAllInitState = true;
	
	$.each(users, function(userIndex, user) {
		var checkbox = $('<input />').attr({
	    	 'type' : 'checkbox'
	      }).change(function(){
	    	var itemState = $(this).is(':checked');
	    	if (itemState) {
	    		var selectAllState = true;
	    		$('input', list).each(function(){
	    			if (!$(this).is(':checked')) {
	    				selectAllState = false;
	    				return false;
	    			}
	    		});
	    		
	    		if (selectAllState) {
	    			$('#' + listId + 'SelectAll').attr('checked', 'checked');
	    		}
	    	} else {
	    		$('#' + listId + 'SelectAll').attr('checked', null);
	    	}
	    	
	      });
		if (user.classMember) {
			checkbox.attr('checked', 'checked');
			if (disableCreator && user.lessonCreator) {
				// user creator must not be deselected
				checkbox.attr('disabled', 'disabled');
			}
		} else {
			selectAllInitState = false;
		}
		
		var userDiv = $('<div />').attr({
			'userId'  : user.id
			})
          .addClass('dialogListItem')
	      .html(getLearnerDisplayName(user))
	      .prepend(checkbox)
	      .appendTo(list);
		
		if (disableCreator && user.lessonCreator) {
			userDiv.addClass('dialogListItemDisabled');
		} else {
			userDiv.click(function(event){
				if (event.target == this) {
		    		checkbox.attr('checked', checkbox.is(':checked') ? null : 'checked');
		    	}
		    })
		}
	});	

	$('#' + listId + 'SelectAll').attr('checked', selectAllInitState ? 'checked' : null);
}


/**
 * Opens Authoring for live edit.
 */
function openLiveEdit(){
	if (confirm(LABELS.LIVE_EDIT_CONFIRM)) {
		$.ajax({
			dataType : 'text',
			url : LAMS_URL + 'monitoring/monitoring.do',
			cache : false,
			async : false,
			data : {
				'method'    : 'startLiveEdit',
				'ldId'      : ldId
			},
			success : function(response) {
				if (response) {
					alert(response);
				} else {
					openPopUp(LAMS_URL + 'home.do?method=author&layout=editonfly&learningDesignID=' + ldId,
							'LiveEdit', 600, 800, false);
					window.parent.closeMonitorLessonDialog();
				}
			}
		});	
	}
}


/**
 * Replaces canvas with the given branchin activity contents
 */
function showBranchingSequence(branchingActivityId){
	sequenceBranchingId = branchingActivityId;
	originalSequenceCanvas = null;
	$('#closeBranchingButton').show();
	sequenceCanvas.fadeOut(function(){
		sequenceCanvas.html(null);
		updateSequenceTab();
	});
}


/**
 * Shows Learning Design in canvas.
 */
function closeBranchingSequence(){
	showBranchingSequence(null);
	$('#closeBranchingButton').hide();
}

//********** LEARNERS TAB FUNCTIONS **********

/**
 * Handler for shift page numbers bar.
 */
function learnersPageShift(increment){
	var pageNumberCell = $('#learnersPageLeft').next();
	if (pageNumberCell.hasClass('learnersHeaderPageCell')) {
		var startIndex = +pageNumberCell.text() + (increment ? 10 : -10);
		var endIndex = startIndex + 9;
		shiftLearnerProgressPageHeader(startIndex, endIndex);
	}
}


/**
 * Do the actual shifting of page numbers bar.
 */
function shiftLearnerProgressPageHeader(startIndex, endIndex) {
	var pageLeftCell = $('#learnersPageLeft');
	var pageCount = Math.ceil(numberActiveLearners / 10);
	$('#tabLearnerControlTable td.learnersHeaderPageCell').remove();
	
	if (startIndex < 1) {
		startIndex = 1;
		endIndex = 10;
	}
	if (endIndex > pageCount) {
		// put a bit more on the left, since right end of scale is too short
		startIndex -= endIndex - pageCount;
		if (startIndex < 1) {
			startIndex = 1;
		}
		endIndex = pageCount;
	}

	for (var pageIndex = endIndex; pageIndex >= startIndex; pageIndex--) {
		// produce the page number cells
		var pageHeaderCell = 
			$('<td class="learnersHeaderCell learnersHeaderPageCell"></td>')
			.text(pageIndex)
			.insertAfter(pageLeftCell)
			.click(function(){
				loadLearnerProgressPage(+$(this).text());
			});
		if (pageIndex == learnerProgressCurrentPageNumber){
			// highlight the currently selected one
			pageHeaderCell.addClass('selectedLearnersHeaderPageCell');
		}
	}
}

/**
 * After page change, refresh values in the control bar.
 */
function updateLearnerProgressHeader(pageNumber) {
	var controlRow = $('#tabLearnerControlTable tr');
	if (numberActiveLearners < 10 
			&& (!learnersSearchPhrase || learnersSearchPhrase == '')) {
		// do not show the bar at all
		$('.learnersHeaderCell', controlRow).hide();
		return;
	}
	// show the bar
	$('.learnersHeaderCell', controlRow).show();
	
	var pageCount = Math.ceil(numberActiveLearners / 10);
	if (!pageNumber) {
		pageNumber = 1;
	} else if (pageNumber > pageCount) {
		pageNumber = pageCount;
	}
	learnerProgressCurrentPageNumber = pageNumber;
	// update "Page X / Y" field
	$('#learnersPageCounter').html(pageNumber + '&nbsp;/&nbsp;' + pageCount + '&nbsp;');
	
	// remove arrows for shifting page numbers, if they are not needed
	if (pageCount < 10) {
		$('td.learnersPageShifter', controlRow).hide();
	}
	
	// calculate currently visible page numbers
	var pageStartIndex = pageNumber - 5;
	var pageEndIndex = pageNumber + 5 - Math.min(pageStartIndex,0);
	shiftLearnerProgressPageHeader(pageStartIndex, pageEndIndex, pageNumber);
}

/**
 * Load the give page of learners' progress.
 */
function loadLearnerProgressPage(pageNumber){
	// prevent double refresh at the same time
	if (learnersRefreshInProgress) {
		return;
	}
	learnersRefreshInProgress = true;
	
	if (!learnerProgressCellsTemplate) {
		// fill the placeholder, after all required variables were initialised
		learnerProgressCellsTemplate =
		  '<tr><td class="progressBarLabel" id="progressBarLabel;00;"><div>;11;</div>'
		+ '<a class="button" title="' 
		+ LABELS.EXPORT_PORTFOLIO_LEARNER_TOOLTIP + '" href="#" onClick="javascript:openPopUp(\''
		+ LAMS_URL + 'learning/exportWaitingPage.jsp?mode=learner&role=teacher&lessonID='
		+ lessonId + '&userID=;00;\',\'ExportPortfolio\',240,640,true)">'
		+ LABELS.EXPORT_PORTFOLIO
		+ '</a><a class="button" title="'
		+ LABELS.TIME_CHART_TOOLTIP + '" href="#" onClick="javascript:openPopUp(\''
		+ LAMS_URL + 'monitoring/monitoring.do?method=viewTimeChart&lessonID='
		+ lessonId + '&learnerID=;00;\',\'TimeChart\',600,800,true)">'
		+ LABELS.TIME_CHART 
		+ '</a><a class="button" href="#" onClick="javascript:showEmailDialog(;00;)">'
		+ LABELS.EMAIL_BUTTON
		+ '</a></td></tr><tr><td class="progressBarCell" id="progressBar;00;"></td></tr>';
	}
	
	// remove existing progress bars
	$('#tabLearnersTable').html(null);
	bars = {};
	var isProgressSorted = $('#orderByCompletionCheckbox:checked').length > 0;
	// either go to the given page or refresh the current one
	pageNumber = pageNumber || learnerProgressCurrentPageNumber;
	
	$.ajax({
		dataType : 'json',
		url : LAMS_URL + 'monitoring/monitoring.do',
		cache : false,
		data : {
			'method'           : 'getLearnerProgressPage',
			'lessonID'         : lessonId,
			'searchPhrase'     : learnersSearchPhrase,
			'pageNumber'       : pageNumber,
			'isProgressSorted' : isProgressSorted
			
		},
		
		success : function(response) {
			numberActiveLearners = response.numberActiveLearners;
			updateLearnerProgressHeader(pageNumber);
			
			if (response.learners) {
				$.each(response.learners, function(){
					var barId = 'bar' + this.id;
					// create a new bar metadata entry
					bars[barId] = {
						'userId'      : this.id,
						'containerId' : 'progressBar' + this.id
					};
					
					// prepare HTML for progress bar
					var learnerProgressCellsInstance = learnerProgressCellsTemplate
						.replace(/;00;/g, this.id)
						.replace(/;11;/g, getLearnerDisplayName(this));
					$(learnerProgressCellsInstance).appendTo('#tabLearnersTable');
					
					// request data to build progress bar SVG
					fillProgressBar(barId);
				});
			}
			
			learnersRefreshInProgress = false;
		}
	});

}


/**
 * Refreshes the existing progress bars. 
 */
function updateLearnersTab(){
	// prevent double refresh
	if (learnersRefreshInProgress) {
		return;
	}
	learnersRefreshInProgress = true;
	
	for (var barId in bars) {
		fillProgressBar(barId);
	}
	learnersRefreshInProgress = false;
}


/**
 * Run search for the phrase which user provided in text field.
 */
function learnersRunSearchPhrase(){
	var searchPhraseField = $('#learnersSearchPhrase');
	learnersSearchPhrase = searchPhraseField.val();
	if (learnersSearchPhrase && learnersSearchPhrase.trim() != '') {
		var pageNumber = parseInt(learnersSearchPhrase);
		// must be a positive integer
		if (isNaN(pageNumber) || !isFinite(pageNumber) || pageNumber < 0){
			// if it was not a number, run a normal search
			loadLearnerProgressPage(1);
		} else {
			// it was a number, reset the field and go to the given page
			learnersSearchPhrase = null;
			searchPhraseField.val(null);
			loadLearnerProgressPage(pageNumber);
		}
	} else {
		learnersSearchPhrase = null;
	}
}


/**
 * Clears previous run search for phrase.
 */
function learnersClearSearchPhrase(){
	learnersSearchPhrase = null;
	$('#learnersSearchPhrase').val(null);
	loadLearnerProgressPage(1);
}

//********** COMMON FUNCTIONS **********

/**
 * Updates all changeable elements of monitoring screen.
 */
function refreshMonitor(tabName, isAuto){
	if (autoRefreshIntervalObject && !isAuto) {
		clearInterval(autoRefreshIntervalObject);
		autoRefreshIntervalObject = null;
	}

	if (!autoRefreshIntervalObject) {
		autoRefreshIntervalObject = setInterval(function(){
			if (!autoRefreshBlocked) {
				refreshMonitor(null, true);
			}
		}, autoRefreshInterval);
	}
	
	if (!tabName) {
		// update Lesson tab widgets (state, number of learners etc.)
		updateLessonTab();
		// update learner progress in Sequence tab
		updateSequenceTab();
		// update learner progress in Learners tab
		loadLearnerProgressPage();
	} else if (tabName == 'lesson') {
		updateLessonTab();
	} else if (tabName == 'sequence'){
		updateLessonTab();
		updateSequenceTab();
	} else if (tabName == 'learners'){
		updateLessonTab();
		updateLearnersTab();
	}
}


/**
 * Show a dialog with user list and optional Force Complete and View Learner buttons.
 */
function showLearnerGroupDialog(activityId, dialogTitle, learners, allowForceComplete, allowView, allowEmail) {
	var learnerGroupList = $('#learnerGroupList').empty();
	var learnerGroupDialog = $('#learnerGroupDialog');
	
	if (learners) {
		$.each(learners, function(learnerIndex, learner) {
			var viewUrl = LAMS_URL + 'monitoring/monitoring.do?method=getLearnerActivityURL&userID=' 
            				       + learner.id + '&activityID=' + activityId + '&lessonID=' + lessonId,
				learnerDiv = $('<div />').attr({
									'userId'  : learner.id,
									'viewUrl'    : viewUrl
									})
			                      .addClass('dialogListItem')
							      .html(getLearnerDisplayName(learner))
							      .appendTo(learnerGroupList);
			
			if (allowForceComplete || allowView || allowEmail) {
				learnerDiv.click(function(){
			    	  // select a learner
			    	  $(this).addClass('dialogListItemSelected')
			    	  	.siblings('div.dialogListItem')
			    	  	.removeClass('dialogListItemSelected');
				    	// enable buttons
				    	$('button.learnerGroupDialogSelectableButton')
				    		.attr('disabled', null);
			    });
				if (allowView){
					learnerDiv.dblclick(function(){
						// same as clicking View Learner button
						openPopUp(viewUrl, "LearnActivity", 600, 800, true);
					});
				}
			}
		});
	}
	
	// show buttons depending on parameters
	$('button#learnerGroupDialogForceCompleteButton')
		.css('display', allowForceComplete ? 'inline' : 'none');
	$('button#learnerGroupDialogViewButton')
		.css('display', allowView ? 'inline' : 'none');
	$('button#learnerGroupDialogEmailButton')
		.css('display', allowEmail ? 'inline' : 'none');

	learnerGroupDialog
		.dialog('option', 
			{
			 'title' : dialogTitle,
			 'activityId' : activityId
			})
		.dialog('open');	
}


/**
 * Formats learner name.
 */
function getLearnerDisplayName(learner, raw) {
	return raw ? learner.firstName + ' ' + learner.lastName + ' (' + learner.login + ')'
			   : escapeHtml(learner.firstName) + ' ' + escapeHtml(learner.lastName) + ' (' + escapeHtml(learner.login) + ')';
}


/**
 * Escapes HTML tags to prevent XSS injection.
 */
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/**
 * Change order of learner sorting in group dialog.
 */
function sortDialogList(listId) {
	var list = $('#' + listId + 'List');
	var items = list.children('div.dialogListItem');
	var orderAsc = sortOrderAsc[listId];
	if (items.length > 1) {
		items.each(function(){
			$(this).detach();
		}).sort(function(a, b){
			var keyA = $(a).text().toLowerCase();
			var keyB = $(b).text().toLowerCase();
			var result = keyA > keyB ? 1 : keyA < keyB ? -1 : 0;
			return orderAsc ? -result : result;
		}).each(function(){
			$(this).appendTo(list);
		});
		
		var button = $('#' + listId + 'SortButton');
		if (orderAsc) {
			button.html('▼');
			sortOrderAsc[listId] = false;
		} else {
			button.html('▲');
			sortOrderAsc[listId] = true;
		}
	}
}

function selectAllInDialogList(listId) {
	var targetState = $('#' + listId + 'SelectAll').is(':checked') ? 'checked' : null;
	$('#' + listId + 'List input').each(function(){
		if (!$(this).is(':disabled')) {
			$(this).attr('checked', targetState);
		}		
	});
}


function colorDialogList(listId) {
	$('#' + listId + 'List div.dialogListItem').each(function(userIndex, userDiv){
		// every odd learner has different background
		$(userDiv).css('background-color', userIndex % 2 ? '#dfeffc' : 'inherit');
	});
}


/**
 * Makes a XML element with given attributes.
 * jQuery does not work well with SVG in Chrome, so all this manipulation need to be done manually. 
 */
function appendXMLElement(tagName, attributesObject, content, target) {
	var elementText = '<' + tagName + (content ? '>' + content + '</' + tagName + '>'
											   : ' />');
	var element = $.parseXML(elementText).firstChild;
	if (attributesObject) {
		for (attrKey in attributesObject) {
			element.setAttribute(attrKey, attributesObject[attrKey]);
		}
	}

	target.appendChild(element);
	return element;
}