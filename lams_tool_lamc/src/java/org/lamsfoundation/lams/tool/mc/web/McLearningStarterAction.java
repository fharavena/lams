
package org.lamsfoundation.lams.tool.mc.web;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McApplicationException;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueUsr;
import org.lamsfoundation.lams.tool.mc.pojos.McSession;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.tool.mc.service.McServiceProxy;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;


/**
 * 
 * @author Ozgur Demirtas
 *
 * <lams base path>/<tool's learner url>&userId=<learners user id>&toolSessionId=123&mode=teacher

 * Tool Session:
 *
 * A tool session is the concept by which which the tool and the LAMS core manage a set of learners interacting with the tool. 
 * The tool session id (toolSessionId) is generated by the LAMS core and given to the tool.
 * A tool session represents the use of a tool for a particulate activity for a group of learners. 
 * So if an activity is ungrouped, then one tool session exist for for a tool activity in a learning design.
 *
 * More details on the tool session id are covered under monitoring.
 * When thinking about the tool content id and the tool session id, it might be helpful to think about the tool content id 
 * relating to the definition of an activity, whereas the tool session id relates to the runtime participation in the activity.

 *  
 * Learner URL:
 * The learner url display the screen(s) that the learner uses to participate in the activity. 
 * When the learner accessed this user, it will have a tool access mode ToolAccessMode.LEARNER.
 *
 * It is the responsibility of the tool to record the progress of the user. 
 * If the tool is a multistage tool, for example asking a series of questions, the tool must keep track of what the learner has already done. 
 * If the user logs out and comes back to the tool later, then the tool should resume from where the learner stopped.
 * When the user is completed with tool, then the tool notifies the progress engine by calling 
 * org.lamsfoundation.lams.learning.service.completeToolSession(Long toolSessionId, User learner).
 *
 * If the tool's content DefineLater flag is set to true, then the learner should see a "Please wait for the teacher to define this part...." 
 * style message.
 * If the tool's content RunOffline flag is set to true, then the learner should see a "This activity is not being done on the computer. 
 * Please see your instructor for details."
 *
 * ?? Would it be better to define a run offline message in the tool? We have instructions for the teacher but not the learner. ??
 * If the tool has a LockOnFinish flag, then the tool should lock learner's entries once they have completed the activity. 
 * If they return to the activity (e.g. via the progress bar) then the entries should be read only.
 * 
   <!--Learning Starter Action: initializes the Learning module -->
   <action 	path="/learningStarter" 
   			type="org.lamsfoundation.lams.tool.mc.web.McLearningStarterAction" 
   			name="McLearningForm" 
   			input=".learningStarter"> 

		<exception
	        key="error.exception.McApplication"
	        type="org.lamsfoundation.lams.tool.mc.McApplicationException"
	        handler="org.lamsfoundation.lams.tool.mc.web.CustomStrutsExceptionHandler"
	        path=".mcErrorBox"
	        scope="request"
	      />

		<exception
	        key="error.exception.McApplication"
	        type="java.lang.NullPointerException"
	        handler="org.lamsfoundation.lams.tool.mc.web.CustomStrutsExceptionHandler"
	        path=".mcErrorBox"
	        scope="request"
	      />	         			   			

	  	<forward
		    name="loadLearner"
		    path=".answers"
		    redirect="true"
	  	/>

	  	<forward
		    name="viewAnswers"
		    path=".viewAnswers"
		    redirect="true"
	  	/>

	  	<forward
		    name="redoQuestions"
		    path=".redoQuestions"
		    redirect="true"
	  	/>
	  	
	     <forward
	        name="preview"
	        path=".preview"
	        redirect="true"
	     />

	  	<forward
		    name="learningStarter"
		    path=".learningStarter"
		    redirect="true"
	  	/>
	  	
	  	<forward
		    name="errorList"
		    path=".mcErrorBox"
		    redirect="true"
	  	/>
	</action>    
 *
 */

public class McLearningStarterAction extends Action implements McAppConstants {
	static Logger logger = Logger.getLogger(McLearningStarterAction.class.getName());
	 /* Since the toolSessionId is passed, we will derive toolContentId from the toolSessionId
	 * This class is used to load the default content and initialize the presentation Map for Learner mode 
	 */ 

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
  								throws IOException, ServletException, McApplicationException {
		
	    /*
	     * By now, the passed tool session id MUST exist in the db through the calling of:
	     * public void createToolSession(Long toolSessionId, Long toolContentId) by the container.
	     *  
	     * 
	     * make sure this session exists in tool's session table by now.
	     */
		
		McUtils.cleanUpSessionAbsolute(request);
		
		Map mapQuestionsContent= new TreeMap(new McComparator());
		Map mapAnswers= new TreeMap(new McComparator());

		IMcService mcService = McUtils.getToolService(request);
		logger.debug("retrieving mcService from session: " + mcService);
		if (mcService == null)
		{
			mcService = McServiceProxy.getMcService(getServlet().getServletContext());
		    logger.debug("retrieving mcService from proxy: " + mcService);
		    request.getSession().setAttribute(TOOL_SERVICE, mcService);		
		}

		McLearningForm mcLearningForm = (McLearningForm) form;
		
		/*
		 * initialize available question display modes in the session
		 */
		request.getSession().setAttribute(QUESTION_LISTING_MODE_SEQUENTIAL,QUESTION_LISTING_MODE_SEQUENTIAL);
	    request.getSession().setAttribute(QUESTION_LISTING_MODE_COMBINED, QUESTION_LISTING_MODE_COMBINED);
	    
	    request.getSession().setAttribute(QUESTIONS_WITHNO_OPTIONS, new Boolean(false));
	    
	    /*
	     * persist time zone information to session scope. 
	     */
	    McUtils.persistTimeZone(request);
	    ActionForward validateParameters=validateParameters(request, mapping);
	    logger.debug("validateParamaters: " + validateParameters);
	    if (validateParameters != null)
	    {
	    	return validateParameters;
	    }
  
	    Long toolSessionID=(Long) request.getSession().getAttribute(AttributeNames.PARAM_TOOL_SESSION_ID);
	    logger.debug("retrieved toolSessionID: " + toolSessionID);
	    
	    /* API test code from here*/
	    String createToolSession=request.getParameter("createToolSession");
		logger.debug("createToolSession: " + createToolSession);
		if ((createToolSession != null) && createToolSession.equals("1"))
		{	try
			{
				//TODO: please confirm the toolSessionName: I am wonder whether tool can call this interface itself.
				mcService.createToolSession(toolSessionID,"mcSessionName", new Long(9876));
				return (mapping.findForward(LEARNING_STARTER));
			}
			catch(ToolException e)
			{
				McUtils.cleanUpSessionAbsolute(request);
				logger.debug("tool exception"  + e);
			}
		}
		
		String removeToolSession=request.getParameter("removeToolSession");
		logger.debug("removeToolSession: " + removeToolSession);
		if ((removeToolSession != null) && removeToolSession.equals("1"))
		{	try
			{
				mcService.removeToolSession(toolSessionID);
				return (mapping.findForward(LEARNING_STARTER));
			}
			catch(ToolException e)
			{
				McUtils.cleanUpSessionAbsolute(request);
				logger.debug("tool exception"  + e);
			}
		}
		
		String learnerId=request.getParameter("learnerId");
		logger.debug("learnerId: " + learnerId);
		if (learnerId != null) 
		{	try
			{
				String nextUrl=mcService.leaveToolSession(toolSessionID, new Long(learnerId));
				logger.debug("nextUrl: "+ nextUrl);
				return (mapping.findForward(LEARNING_STARTER));
			}
			catch(ToolException e)
			{
				McUtils.cleanUpSessionAbsolute(request);
				logger.debug("tool exception"  + e);
			}
		}
		/*till here*/
	    
		
		/*
		 * by now, we made sure that the passed tool session id exists in the db as a new record
		 * Make sure we can retrieve it and the relavent content
		 */
		
		McSession mcSession=mcService.retrieveMcSession(toolSessionID);
	    logger.debug("retrieving mcSession: " + mcSession);
	    
	    if (mcSession == null)
	    {
	    	logger.debug("error: The tool expects mcSession.");
	    	persistError(request,"error.toolSession.notAvailable");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }

	    /*
	     * find out what content this tool session is referring to
	     * get the content for this tool session 
	     * Each passed tool session id points to a particular content. Many to one mapping.
	     */
		McContent mcContent=mcSession.getMcContent();
	    logger.debug("using mcContent: " + mcContent);
	    
	    if (mcContent == null)
	    {
	    	logger.debug("error: The tool expects mcContent.");
	    	persistError(request,"error.toolContent.notAvailable");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }

	    
	    /*
	     * The content we retrieved above must have been created before in Authoring time. 
	     * And the passed tool session id already refers to it.
	     */
	    setupAttributes(request, mcContent);

	    request.getSession().setAttribute(TOOL_CONTENT_ID, mcContent.getMcContentId());
	    logger.debug("using TOOL_CONTENT_ID: " + mcContent.getMcContentId());
	    
	    request.getSession().setAttribute(TOOL_CONTENT_UID, mcContent.getUid());
	    logger.debug("using TOOL_CONTENT_UID: " + mcContent.getUid());
	    
    	/* Is the request for a preview by the author?
    	Preview The tool must be able to show the specified content as if it was running in a lesson. 
		It will be the learner url with tool access mode set to ToolAccessMode.AUTHOR 
		3 modes are:
			author
			teacher
			learner
		*/
    	/* ? CHECK THIS: how do we determine whether preview is requested? Mode is not enough on its own.*/
	    
	    /*handle PREVIEW mode*/
	    String mode=(String) request.getSession().getAttribute(LEARNING_MODE);
	    logger.debug("mode: " + mode);
    	if ((mode != null) && (mode.equals("author")))
    	{
    		logger.debug("Author requests for a preview of the content.");
			logger.debug("existing mcContent:" + mcContent);
    		
			commonContentSetup(request, mcContent);
    		
	    	/*only allowing combined view in the preview mode. Might be improved to support sequential view as well. */
	    	request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_COMBINED);
	    	/* PREVIEW_ONLY for jsp*/
	    	request.getSession().setAttribute(PREVIEW_ONLY, new Boolean(true).toString());
	    	
	    	request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
    		McLearningAction mcLearningAction= new McLearningAction();
	    	return mcLearningAction.redoQuestions(request, mcLearningForm, mapping);
    	}
	    
    	/* by now, we know that the mode is either teacher or learner
    	 * check if the mode is teacher and request is for Learner Progress
    	 */
		String userId=request.getParameter(USER_ID);
		logger.debug("userId: " + userId);
		if ((userId != null) && (mode.equals("teacher")))
		{
			logger.debug("request is for learner progress");
			commonContentSetup(request, mcContent);
	    	
			/* LEARNER_PROGRESS for jsp*/
			request.getSession().setAttribute(LEARNER_PROGRESS_USERID, userId);
			request.getSession().setAttribute(LEARNER_PROGRESS, new Boolean(true).toString());
			McLearningAction mcLearningAction= new McLearningAction();
			/* pay attention that this userId is the learner's userId passed by the request parameter.
			 * It is differerent than USER_ID kept in the session of the current system user*/
			McQueUsr mcQueUsr=mcService.retrieveMcQueUsr(new Long(userId));
		    logger.debug("mcQueUsr:" + mcQueUsr);
		    if (mcQueUsr == null)
		    {
		    	persistError(request, "error.learner.required");
		    	McUtils.cleanUpSessionAbsolute(request);
				return (mapping.findForward(ERROR_LIST));
		    }
		    
		    /* check whether the user's session really referrs to the session id passed to the url*/
		    Long sessionUid=mcQueUsr.getMcSessionId();
		    logger.debug("sessionUid" + sessionUid);
		    McSession mcSessionLocal=mcService.getMcSessionByUID(sessionUid);
		    logger.debug("checking mcSessionLocal" + mcSessionLocal);
		    Long toolSessionId=(Long)request.getSession().getAttribute(TOOL_SESSION_ID);
		    logger.debug("toolSessionId: " + toolSessionId + " versus" + mcSessionLocal);
		    if  ((mcSessionLocal ==  null) ||
				 (mcSessionLocal.getMcSessionId().longValue() != toolSessionId.longValue()))
		    {
		    	persistError(request, "error.learner.sessionId.inconsistent");
		    	McUtils.cleanUpSessionAbsolute(request);
				return (mapping.findForward(ERROR_LIST));
		    }
			return mcLearningAction.viewAnswers(mapping, form, request, response);
		}
    	
		/* by now, we know that the mode is learner*/
	    
	    /* find out if the content is set to run offline or online. If it is set to run offline , the learners are informed about that. */
	    boolean isRunOffline=McUtils.isRunOffline(mcContent);
	    logger.debug("isRunOffline: " + isRunOffline);
	    if (isRunOffline == true)
	    {
	    	logger.debug("warning to learner: the activity is offline.");
	    	persistError(request,"label.learning.runOffline");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }

	    /* find out if the content is being modified at the moment. */
	    boolean isDefineLater=McUtils.isDefineLater(mcContent);
	    logger.debug("isDefineLater: " + isDefineLater);
	    if (isDefineLater == true)
	    {
	    	logger.debug("warning to learner: the activity is defineLater, we interpret that the content is being modified.");
	    	persistError(request,"error.defineLater");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }

	    /*
    	 * fetch question content from content
    	 */
	    mapQuestionsContent=LearningUtil.buildQuestionContentMap(request,mcContent);
	    logger.debug("mapQuestionsContent: " + mapQuestionsContent);
    	
    	request.getSession().setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestionsContent);
    	logger.debug("MAP_QUESTION_CONTENT_LEARNER: " +  request.getSession().getAttribute(MAP_QUESTION_CONTENT_LEARNER));
    	logger.debug("mcContent has : " + mapQuestionsContent.size() + " entries.");
    	request.getSession().setAttribute(TOTAL_QUESTION_COUNT, new Long(mapQuestionsContent.size()).toString());
    	
    	request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
		logger.debug("CURRENT_QUESTION_INDEX: " + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));
		logger.debug("final Options Map for the first question: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
		
		/*also prepare data into mapGeneralOptionsContent for combined answers view */
		Map mapGeneralOptionsContent=AuthoringUtil.generateGeneralOptionsContentMap(request, mcContent);
		logger.debug("returned mapGeneralOptionsContent: " + mapGeneralOptionsContent);
		request.getSession().setAttribute(MAP_GENERAL_OPTIONS_CONTENT, mapGeneralOptionsContent);
    	
    	/*
	     * verify that userId does not already exist in the db.
	     * If it does exist, that means, that user already responded to the content and 
	     * his answers must be displayed  read-only
	     * 
	     */
    	String userID=(String) request.getSession().getAttribute(USER_ID);
    	logger.debug("userID:" + userID);
	    
    	McQueUsr mcQueUsr=mcService.retrieveMcQueUsr(new Long(userID));
	    logger.debug("mcQueUsr:" + mcQueUsr);
	    
	    if (mcQueUsr != null)
	    {
	    	logger.debug("mcQueUsr is available in the db:" + mcQueUsr);
	    	Long queUsrId=mcQueUsr.getUid();
			logger.debug("queUsrId: " + queUsrId);
			
			int highestAttemptOrder=LearningUtil.getHighestAttemptOrder(request, queUsrId);
			logger.debug("highestAttemptOrder: " + highestAttemptOrder);
			if (highestAttemptOrder == 0)
				highestAttemptOrder=1;
			logger.debug("highestAttemptOrder: " + highestAttemptOrder);
			request.getSession().setAttribute(LEARNER_LAST_ATTEMPT_ORDER,new Integer(highestAttemptOrder).toString());
			
			int learnerBestMark=LearningUtil.getHighestMark(request, queUsrId);
			logger.debug("learnerBestMark: " + learnerBestMark);
			request.getSession().setAttribute(LEARNER_BEST_MARK,new Integer(learnerBestMark).toString());	
	    }
	    else
	    {
	    	logger.debug("mcQueUsr is not available in the db:" + mcQueUsr);
	    	request.getSession().setAttribute(LEARNER_LAST_ATTEMPT_ORDER,new Integer(1).toString());
	    	request.getSession().setAttribute(LEARNER_BEST_MARK,new Integer(0).toString());
	    	
	    }
	    
	    String learningMode=(String) request.getSession().getAttribute(LEARNING_MODE);
	    logger.debug("users learning mode is: " + learningMode);
	    /*if the user's session id AND user id exists in the tool tables go to redo questions.*/
	    if ((mcQueUsr != null) && learningMode.equals("learner"))
	    {
	    	Long sessionUid=mcQueUsr.getMcSessionId();
	    	logger.debug("users sessionUid: " + sessionUid);
	    	McSession mcUserSession= mcService.getMcSessionByUID(sessionUid);
	    	logger.debug("mcUserSession: " + mcUserSession);
	    	String userSessionId=mcUserSession.getMcSessionId().toString();
	    	logger.debug("userSessionId: " + userSessionId);
	    	Long toolSessionId=(Long)request.getSession().getAttribute(TOOL_SESSION_ID);
	    	logger.debug("current toolSessionId: " + toolSessionId);
	    	if (toolSessionId.toString().equals(userSessionId))
	    	{
	    		logger.debug("the user's session id AND user id exists in the tool tables go to redo questions. " + toolSessionId + " mcQueUsr: " + 
	    				mcQueUsr + " user id: " + mcQueUsr.getQueUsrId());
	    		logger.debug("the learner has already responsed to this content, just generate a read-only report. Use redo questions for this.");
		    	return (mapping.findForward(REDO_QUESTIONS));
	    	}
	    }
	    else if (learningMode.equals("teacher"))
	    {
	    	McLearningAction mcLearningAction= new McLearningAction();
	    	logger.debug("present to teacher learners progress...");
	    	return mcLearningAction.viewAnswers(mapping, form, request, response);	
	    }
	    return (mapping.findForward(LOAD_LEARNER));	
	}
	
	
	/**
	 * sets up question and candidate answers maps
	 * commonContentSetup(HttpServletRequest request, McContent mcContent)
	 * 
	 * @param request
	 * @param mcContent
	 */
	protected void commonContentSetup(HttpServletRequest request, McContent mcContent)
	{
		Map mapQuestionsContent= new TreeMap(new McComparator());
		mapQuestionsContent=LearningUtil.buildQuestionContentMap(request,mcContent);
	    logger.debug("mapQuestionsContent: " + mapQuestionsContent);
		
		request.getSession().setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestionsContent);
		logger.debug("MAP_QUESTION_CONTENT_LEARNER: " +  request.getSession().getAttribute(MAP_QUESTION_CONTENT_LEARNER));
		logger.debug("mcContent has : " + mapQuestionsContent.size() + " entries.");
		request.getSession().setAttribute(TOTAL_QUESTION_COUNT, new Long(mapQuestionsContent.size()).toString());
		
		request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
		logger.debug("CURRENT_QUESTION_INDEX: " + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));
		logger.debug("final Options Map for the first question: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
		
		/*also prepare data into mapGeneralOptionsContent for combined answers view */
		Map mapGeneralOptionsContent=AuthoringUtil.generateGeneralOptionsContentMap(request, mcContent);
		logger.debug("returned mapGeneralOptionsContent: " + mapGeneralOptionsContent);
		request.getSession().setAttribute(MAP_GENERAL_OPTIONS_CONTENT, mapGeneralOptionsContent);
	}
	
	
	/**
	 * sets up session scope attributes based on content linked to the passed tool session id
	 * setupAttributes(HttpServletRequest request, McContent mcContent)
	 * 
	 * @param request
	 * @param mcContent
	 */
	protected void setupAttributes(HttpServletRequest request, McContent mcContent)
	{
		/* returns Integer:  can be 0 or greater than 0,  0 is no passmark, otherwise there is a passmark. */
	    logger.debug("PASSMARK: " + mcContent.getPassMark());
	    if (mcContent.getPassMark() != null)
	    {
	    	int passMark=mcContent.getPassMark().intValue();
	    	request.getSession().setAttribute(PASSMARK, mcContent.getPassMark());
	    }
	    else
	    {
	    	request.getSession().setAttribute(PASSMARK, new Integer(0));
	    }

	    request.getSession().setAttribute(IS_SHOW_LEARNERS_REPORT, new Boolean(mcContent.isShowReport()).toString());
	    logger.debug("IS_SHOW_LEARNERS_REPORT: " + new Boolean(mcContent.isShowReport()).toString());

	    /* same as 1 page per question */
	    logger.debug("IS_QUESTIONS_SEQUENCED: " + mcContent.isQuestionsSequenced());
	    if (mcContent.isQuestionsSequenced())
		{
			request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_SEQUENTIAL);
		}
	    else
	    {
	    	request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_COMBINED);
	    }
	    logger.debug("QUESTION_LISTING_MODE: " + request.getSession().getAttribute(QUESTION_LISTING_MODE));
	    
	    logger.debug("IS_RETRIES: " + new Boolean(mcContent.isRetries()).toString());
	    request.getSession().setAttribute(IS_RETRIES, new Boolean(mcContent.isRetries()).toString());
	    
	    logger.debug("REPORT_TITLE_LEARNER: " + mcContent.getReportTitle());
	    request.getSession().setAttribute(REPORT_TITLE_LEARNER,mcContent.getReportTitle());
	    
	    logger.debug("IS_CONTENT_IN_USE: " + mcContent.isContentInUse());
	    request.getSession().setAttribute(IS_CONTENT_IN_USE, new Boolean(mcContent.isContentInUse()).toString());
	    
	    
	    /*
	     * Is the tool activity been checked as Run Offline in the property inspector?
	     */
	    logger.debug("IS_TOOL_ACTIVITY_OFFLINE: " + mcContent.isRunOffline());
	    request.getSession().setAttribute(IS_TOOL_ACTIVITY_OFFLINE, new Boolean(mcContent.isRunOffline()).toString());
	    
	    
	    /* the following attributes are unused for the moment.
	     * from here...
	     */
	    
		logger.debug("IS_USERNAME_VISIBLE: " + mcContent.isUsernameVisible());
	    request.getSession().setAttribute(IS_USERNAME_VISIBLE, new Boolean(mcContent.isUsernameVisible()).toString());
	    
		logger.debug("IS_SHOW_FEEDBACK: " + new Boolean(mcContent.isShowFeedback()).toString());
	    request.getSession().setAttribute(IS_SHOW_FEEDBACK, new Boolean(mcContent.isShowFeedback()).toString());
	    
	    Map mapGeneralCheckedOptionsContent= new TreeMap(new McComparator());
	    request.getSession().setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);
	    
	    Map mapLeanerCheckedOptionsContent= new TreeMap(new McComparator());
	    request.getSession().setAttribute(MAP_LEARNER_CHECKED_OPTIONS_CONTENT, mapLeanerCheckedOptionsContent);
	    
	    Map mapLeanerAssessmentResults= new TreeMap(new McComparator());
	    request.getSession().setAttribute(MAP_LEARNER_ASSESSMENT_RESULTS, mapLeanerAssessmentResults);
	    
	    Map mapLeanerFeedbackIncorrect=AuthoringUtil.buildInCorrectFeedbackMap(request, mcContent.getMcContentId());
	    request.getSession().setAttribute(MAP_LEARNER_FEEDBACK_INCORRECT, mapLeanerFeedbackIncorrect);
	    logger.debug("MAP_LEARNER_FEEDBACK_INCORRECT: " + mapLeanerFeedbackIncorrect);
	    
	    Map mapLeanerFeedbackCorrect=AuthoringUtil.buildCorrectFeedbackMap(request, mcContent.getMcContentId());
	    request.getSession().setAttribute(MAP_LEARNER_FEEDBACK_CORRECT, mapLeanerFeedbackCorrect);
	    logger.debug("MAP_LEARNER_FEEDBACK_CORRECT: " + mapLeanerFeedbackCorrect);
	    
	    Map mapQuestionWeights=LearningUtil.buildWeightsMap(request, mcContent.getMcContentId());
	    request.getSession().setAttribute(MAP_QUESTION_WEIGHTS, mapQuestionWeights);
	    logger.debug("MAP_QUESTION_WEIGHTS: " + mapQuestionWeights);
	    /* .. till here */
	}
	
	
	protected ActionForward validateParameters(HttpServletRequest request, ActionMapping mapping)
	{
		/*
	     * obtain and setup the current user's data 
	     */
		
	    String userID = "";
	    /* get session from shared session.*/
	    HttpSession ss = SessionManager.getSession();
	    /* get back login user DTO*/
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if ((user == null) || (user.getUserID() == null))
	    {
	    	logger.debug("error: The tool expects userId");
	    	persistError(request,"error.learningUser.notAvailable");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }else
	    	userID = user.getUserID().toString();
	    
	    logger.debug("retrieved userId: " + userID);
    	request.getSession().setAttribute(USER_ID, userID);
		
	    
	    /*
	     * process incoming tool session id and later derive toolContentId from it. 
	     */
    	String strToolSessionId=request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	    long toolSessionId=0;
	    if ((strToolSessionId == null) || (strToolSessionId.length() == 0)) 
	    {
	    	persistError(request, "error.toolSessionId.required");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }
	    else
	    {
	    	try
			{
	    		toolSessionId=new Long(strToolSessionId).longValue();
		    	logger.debug("passed TOOL_SESSION_ID : " + new Long(toolSessionId));
		    	request.getSession().setAttribute(TOOL_SESSION_ID,new Long(toolSessionId));	
			}
	    	catch(NumberFormatException e)
			{
	    		persistError(request, "error.sessionId.numberFormatException");
	    		logger.debug("add error.sessionId.numberFormatException to ActionMessages.");
	    		McUtils.cleanUpSessionAbsolute(request);
				return (mapping.findForward(ERROR_LIST));
			}
	    }
	    
	    /*mode can be learner, teacher or author */
	    String mode=request.getParameter(MODE);
	    logger.debug("mode: " + mode);
	    
	    if ((mode == null) || (mode.length() == 0)) 
	    {
	    	persistError(request, "error.mode.required");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }
	    
	    if ((!mode.equals("learner")) && (!mode.equals("teacher")) && (!mode.equals("author")))
	    {
	    	persistError(request, "error.mode.invalid");
	    	McUtils.cleanUpSessionAbsolute(request);
			return (mapping.findForward(ERROR_LIST));
	    }
		logger.debug("session LEARNING_MODE set to:" + mode);
	    request.getSession().setAttribute(LEARNING_MODE, mode);
	    
	    return null;
	}

	
	/**
     * persists error messages to request scope
     * @param request
     * @param message
     */
	public void persistError(HttpServletRequest request, String message)
	{
		ActionMessages errors= new ActionMessages();
		errors.add(Globals.ERROR_KEY, new ActionMessage(message));
		logger.debug("add " + message +"  to ActionMessages:");
		saveErrors(request,errors);	    	    
	}
}  
