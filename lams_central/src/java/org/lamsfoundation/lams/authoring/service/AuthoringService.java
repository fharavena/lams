/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.authoring.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDHexGenerator;
import org.lamsfoundation.lams.authoring.IObjectExtractor;
import org.lamsfoundation.lams.dao.hibernate.BaseDAO;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.ActivityOrderComparator;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.learningdesign.LearningDesign;
import org.lamsfoundation.lams.learningdesign.License;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.learningdesign.Transition;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.ActivityDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.GroupDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.GroupingDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.LearningDesignDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.LearningLibraryDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.LicenseDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.TransitionDAO;
import org.lamsfoundation.lams.learningdesign.dto.AuthoringActivityDTO;
import org.lamsfoundation.lams.learningdesign.dto.DesignDetailDTO;
import org.lamsfoundation.lams.learningdesign.dto.LearningDesignDTO;
import org.lamsfoundation.lams.learningdesign.dto.ValidationErrorDTO;
import org.lamsfoundation.lams.learningdesign.exception.LearningDesignException;
import org.lamsfoundation.lams.learningdesign.service.ILearningDesignService;
import org.lamsfoundation.lams.tool.Tool;
import org.lamsfoundation.lams.tool.ToolContentIDGenerator;
import org.lamsfoundation.lams.tool.dao.hibernate.ToolDAO;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.service.ILamsCoreToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.WorkspaceFolder;
import org.lamsfoundation.lams.usermanagement.exception.UserException;
import org.lamsfoundation.lams.usermanagement.exception.WorkspaceFolderException;
import org.lamsfoundation.lams.util.Configuration;
import org.lamsfoundation.lams.util.ConfigurationKeys;
import org.lamsfoundation.lams.util.FileUtilException;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.util.wddx.WDDXProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * @author Manpreet Minhas 
 */
public class AuthoringService implements IAuthoringService, BeanFactoryAware {
	
	protected Logger log = Logger.getLogger(AuthoringService.class);	

	/** Required DAO's */
	protected LearningDesignDAO learningDesignDAO;
	protected LearningLibraryDAO learningLibraryDAO;
	protected ActivityDAO activityDAO;
	protected BaseDAO baseDAO;
	protected TransitionDAO transitionDAO;
	protected ToolDAO toolDAO;
	protected LicenseDAO licenseDAO;
	protected GroupingDAO groupingDAO;
	protected GroupDAO groupDAO;
	protected ILamsCoreToolService lamsCoreToolService;
	protected ILearningDesignService learningDesignService;
	protected MessageService messageService;
	
	protected ToolContentIDGenerator contentIDGenerator;
	
	/** The bean factory is used to create ObjectExtractor objects */
	protected BeanFactory beanFactory;
	
	public AuthoringService(){
		
	}
	
	/**********************************************
	 * Setter Methods
	 * *******************************************/
	/**
	 * Set i18n MessageService
	 */
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	/**
	 * @param groupDAO The groupDAO to set.
	 */
	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}
	public void setGroupingDAO(GroupingDAO groupingDAO) {
		this.groupingDAO = groupingDAO;
	}
	
	/**
	 * @param transitionDAO The transitionDAO  to set
	 */
	public void setTransitionDAO(TransitionDAO transitionDAO) {
		this.transitionDAO = transitionDAO;
	}
	/**
	 * @param learningDesignDAO The learningDesignDAO to set.
	 */
	public void setLearningDesignDAO(LearningDesignDAO learningDesignDAO) {
		this.learningDesignDAO = learningDesignDAO;
	}	
	/**
	 * @param learningLibraryDAO The learningLibraryDAO to set.
	 */
	public void setLearningLibraryDAO(LearningLibraryDAO learningLibraryDAO) {
		this.learningLibraryDAO = learningLibraryDAO;
	}
	/**
	 * @param baseDAO The baseDAO to set.
	 */
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	/**
	 * @param activityDAO The activityDAO to set.
	 */
	public void setActivityDAO(ActivityDAO activityDAO) {
		this.activityDAO = activityDAO;
	}	
	/**
	 * @param toolDAO The toolDAO to set 
	 */
	public void setToolDAO(ToolDAO toolDAO) {
		this.toolDAO = toolDAO;
	}
	/**
	 * @param licenseDAO The licenseDAO to set
	 */
	public void setLicenseDAO(LicenseDAO licenseDAO) {
		this.licenseDAO = licenseDAO;
	}	
	
	public ILamsCoreToolService getLamsCoreToolService() {
		return lamsCoreToolService;
	}

	public void setLamsCoreToolService(ILamsCoreToolService lamsCoreToolService) {
		this.lamsCoreToolService = lamsCoreToolService;
	}

	public ILearningDesignService getLearningDesignService() {
		return learningDesignService;
	}
	
	/**
	 * @param learningDesignService The Learning Design Validator Service
	 */
	public void setLearningDesignService(ILearningDesignService learningDesignService) {
		this.learningDesignService = learningDesignService;
	}	

    /**
     * @param contentIDGenerator The contentIDGenerator to set.
     */
    public void setContentIDGenerator(ToolContentIDGenerator contentIDGenerator)
    {
        this.contentIDGenerator = contentIDGenerator;
    }
    
	/**
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getLearningDesign(java.lang.Long)
	 */
	public LearningDesign getLearningDesign(Long learningDesignID){
		return learningDesignDAO.getLearningDesignById(learningDesignID);
	}
	
	/**
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#saveLearningDesign(org.lamsfoundation.lams.learningdesign.LearningDesign)
	 */
	public void saveLearningDesign(LearningDesign learningDesign){
		learningDesignDAO.insertOrUpdate(learningDesign);
	}
	/**
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getAllLearningDesigns()
	 */
	public List getAllLearningDesigns(){
		return learningDesignDAO.getAllLearningDesigns();		
	}
	
	/**
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getAllLearningLibraries()
	 */
	public List getAllLearningLibraries(){
		return learningLibraryDAO.getAllLearningLibraries();		
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**********************************************
	 * Utility/Service Methods
	 * *******************************************/
	
	/**
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getLearningDesignDetails(java.lang.Long)
	 */
	public String getLearningDesignDetails(Long learningDesignID)throws IOException{
		FlashMessage flashMessage= null;
		LearningDesignDTO learningDesignDTO = learningDesignService.getLearningDesignDTO(learningDesignID);
		if(learningDesignDTO==null)
			flashMessage = FlashMessage.getNoSuchLearningDesignExists("getLearningDesignDetails",learningDesignID);
		else{
			flashMessage = new FlashMessage("getLearningDesignDetails",learningDesignDTO);
		}
		return flashMessage.serializeMessage();
	}	
	public LearningDesign copyLearningDesign(Long originalDesignID,Integer copyType,
									Integer userID, Integer workspaceFolderID, boolean setOriginalDesign) 
																	throws UserException, LearningDesignException, 
											 							      WorkspaceFolderException, IOException{
		
		LearningDesign originalDesign = learningDesignDAO.getLearningDesignById(originalDesignID);
		if(originalDesign==null)
			throw new LearningDesignException(messageService.getMessage("no.such.learningdesign.exist",new Object[]{originalDesignID}));
		
		User user = (User)baseDAO.find(User.class,userID);
		if(user==null)
			throw new UserException(messageService.getMessage("no.such.user.exist",new Object[]{userID}));
		
		WorkspaceFolder workspaceFolder = (WorkspaceFolder)baseDAO.find(WorkspaceFolder.class,workspaceFolderID);
		if(workspaceFolder==null)
			throw new WorkspaceFolderException(messageService.getMessage("no.such.workspace.exist",new Object[]{workspaceFolderID}));
		
		return copyLearningDesign(originalDesign,copyType,user,workspaceFolder, setOriginalDesign);
	}
	
    /**
     * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#copyLearningDesign(org.lamsfoundation.lams.learningdesign.LearningDesign, java.lang.Integer, org.lamsfoundation.lams.usermanagement.User, org.lamsfoundation.lams.usermanagement.WorkspaceFolder)
     */
    public LearningDesign copyLearningDesign(LearningDesign originalLearningDesign,Integer copyType,User user, WorkspaceFolder workspaceFolder, boolean setOriginalDesign)
    	throws LearningDesignException
    {
    	LearningDesign newLearningDesign  = LearningDesign.createLearningDesignCopy(originalLearningDesign,copyType, setOriginalDesign);
    	newLearningDesign.setUser(user);    	
    	newLearningDesign.setWorkspaceFolder(workspaceFolder);    	
    	learningDesignDAO.insert(newLearningDesign);
    	updateDesignActivities(originalLearningDesign,newLearningDesign); 
    	updateDesignTransitions(originalLearningDesign,newLearningDesign);
    	// set first activity assumes that the transitions are all set up correctly.
    	newLearningDesign.setFirstActivity(newLearningDesign.calculateFirstActivity());
    	newLearningDesign.setLearningDesignUIID(originalLearningDesign.getLearningDesignUIID());
    	
    	copyLearningDesignToolContent(newLearningDesign, originalLearningDesign, copyType);
    	
        return newLearningDesign;
    }
    
    /**
     * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#copyLearningDesignToolContent(org.lamsfoundation.lams.learningdesign.LearningDesign, org.lamsfoundation.lams.learningdesign.LearningDesign, java.lang.Integer)
     */
    public LearningDesign copyLearningDesignToolContent(LearningDesign design, LearningDesign originalLearningDesign, Integer copyType ) throws LearningDesignException {
    	
    	// copy the tool content
        // unfortuanately, we have to reaccess the activities to make sure we get the
        // subclass, not a hibernate proxy.
        for (Iterator i = design.getActivities().iterator(); i.hasNext();)
        {
            Activity currentActivity = (Activity) i.next();
            if (currentActivity.isToolActivity())
            {
                try {
                	ToolActivity toolActivity = (ToolActivity) activityDAO.getActivityByActivityId(currentActivity.getActivityId());
                	// copy the content, but don't set the define later flags if it is preview
                    Long newContentId = lamsCoreToolService.notifyToolToCopyContent(toolActivity, copyType.intValue() != LearningDesign.COPY_TYPE_PREVIEW);
                    toolActivity.setToolContentId(newContentId);
                    
                } catch (DataMissingException e) {
                    String error = "Unable to initialise the lesson. Data is missing for activity "+currentActivity.getActivityUIID()
                            +" in learning design "+originalLearningDesign.getLearningDesignId()
                            +" default content may be missing for the tool. Error was "
                            +e.getMessage();
                    log.error(error,e);
                    throw new LearningDesignException(error,e);
                } catch (ToolException e) {
                    String error = "Unable to initialise the lesson. Tool encountered an error copying the data is missing for activity "
                            +currentActivity.getActivityUIID()
                            +" in learning design "+originalLearningDesign.getLearningDesignId()
                            +" default content may be missing for the tool. Error was "
                            +e.getMessage();
                    log.error(error,e);
                    throw new LearningDesignException(error,e);
                }

            }
        }
        
        return design;
    }
    
    /**
     * Updates the Activity information in the newLearningDesign based 
     * on the originalLearningDesign
     * 
     * @param originalLearningDesign The LearningDesign to be copied
     * @param newLearningDesign The copy of the originalLearningDesign
     */
    private void updateDesignActivities(LearningDesign originalLearningDesign, LearningDesign newLearningDesign){
    	TreeSet<Activity> newActivities = new TreeSet<Activity>(new ActivityOrderComparator());   
    	HashMap<Integer, Grouping> newGroupings = new HashMap<Integer, Grouping>();    // key is UIID
    	
    	Set oldParentActivities = originalLearningDesign.getParentActivities();
    	if ( oldParentActivities != null ) {
	    	Iterator iterator = oldParentActivities.iterator();    	
	    	while(iterator.hasNext()){
	    		processActivity((Activity)iterator.next(), newLearningDesign, newActivities, newGroupings, null);
	    	}
    	}
    	
    	// Go back and find all the grouped activities and assign them the new groupings, based on the UIID. Can't
    	// be done as we go as the grouping activity may be processed after the grouped activity.
    	for ( Activity activity : newActivities ) {
    		Integer groupingUIID = activity.getGroupingUIID();
    		if ( groupingUIID != null ) {
   				activity.setGrouping(newGroupings.get(groupingUIID));
    		}
    	}
    	
    	// The activities collection in the learning design may already exist (as we have already done a save on the design).
    	// If so, we can't just override the existing collection as the cascade causes an error.
    	if ( newLearningDesign.getActivities() != null ) {
    		newLearningDesign.getActivities().clear();
    		newLearningDesign.getActivities().addAll(newActivities);
    	} else {
    		newLearningDesign.setActivities(newActivities);
    	}
    }
    
    /** As part of updateDesignActivities(), process an activity and, via recursive calls, the activity's child activities. Need to keep track
     * of any new groupings created so we can go back and update the grouped activities with their new groupings at the end.
     * 
     * @param activity Activity to process. May not be null.
     * @param newLearningDesign The new learning design. May not be null.
     * @param newActivities Temporary set of new activities - as activities are processed they are added to the set. May not be null.
     * @param newGroupings Temporary set of new groupings. Key is the grouping UUID. May not be null.
     * @param parentActivity This activity's parent activity (if one exists). May be null.
     */
    private void processActivity(Activity activity, LearningDesign newLearningDesign, Set<Activity> newActivities, Map<Integer, Grouping> newGroupings, Activity parentActivity) {
		Activity newActivity = getActivityCopy(activity, newGroupings);
		newActivity.setLearningDesign(newLearningDesign);
		if ( parentActivity != null ) {
			newActivity.setParentActivity(parentActivity);
			newActivity.setParentUIID(parentActivity.getActivityUIID());
		}
		activityDAO.insert(newActivity);
		newActivities.add(newActivity);
		
		Set oldChildActivities = getChildActivities((Activity)activity);
		if ( oldChildActivities != null ) {
			Iterator childIterator = oldChildActivities.iterator();
			while(childIterator.hasNext()){
				processActivity((Activity)childIterator.next(), newLearningDesign, newActivities, newGroupings, newActivity);
			}
		}
    }

	/**
     * Updates the Transition information in the newLearningDesign based 
     * on the originalLearningDesign
     * 
     * @param originalLearningDesign The LearningDesign to be copied 
     * @param newLearningDesign The copy of the originalLearningDesign
     */
    public void updateDesignTransitions(LearningDesign originalLearningDesign, LearningDesign newLearningDesign){
    	HashSet newTransitions = new HashSet();
    	Set oldTransitions = originalLearningDesign.getTransitions();
    	Iterator iterator = oldTransitions.iterator();
    	while(iterator.hasNext()){
    		Transition transition = (Transition)iterator.next();
    		Transition newTransition = Transition.createCopy(transition);    		
    		Activity toActivity = null;
        	Activity fromActivity=null;
    		if(newTransition.getToUIID()!=null) {
    			toActivity = activityDAO.getActivityByUIID(newTransition.getToUIID(),newLearningDesign);
    			toActivity.setTransitionTo(newTransition);
    		}
    		if(newTransition.getFromUIID()!=null) {
    			fromActivity = activityDAO.getActivityByUIID(newTransition.getFromUIID(),newLearningDesign);
    			fromActivity.setTransitionFrom(newTransition);
    		}
    		newTransition.setToActivity(toActivity);
    		newTransition.setFromActivity(fromActivity);
    		newTransition.setLearningDesign(newLearningDesign);
    		transitionDAO.insert(newTransition);
    		newTransitions.add(newTransition);
    	}
    	
    	// The transitions collection in the learning design may already exist (as we have already done a save on the design).
    	// If so, we can't just override the existing collection as the cascade causes an error.
    	if ( newLearningDesign.getTransitions() != null ) {
    		newLearningDesign.getTransitions().clear();
    		newLearningDesign.getTransitions().addAll(newTransitions);
    	} else {
        	newLearningDesign.setTransitions(newTransitions);
    	}

    }
    /**
     * Determines the type of activity and returns a deep-copy of the same
     * 
     * @param activity The object to be deep-copied
     * @param newGroupings Temporary set of new groupings. Key is the grouping UUID. May not be null.
     * @return Activity The new deep-copied Activity object
     */
    private Activity getActivityCopy(final Activity activity, Map<Integer, Grouping> newGroupings){
    	if ( Activity.GROUPING_ACTIVITY_TYPE == activity.getActivityTypeId().intValue() ) {
    		GroupingActivity newGroupingActivity = (GroupingActivity) activity.createCopy();
    		// now we need to manually add the grouping to the session, as we can't easily
    		// set up a cascade
    		Grouping grouping = newGroupingActivity.getCreateGrouping();
    		if ( grouping != null ) {
    			groupingDAO.insert(grouping);
    			newGroupings.put(grouping.getGroupingUIID(), grouping);
    		}
    		return newGroupingActivity;
    	}
    	else 
    		return activity.createCopy();    	
    } 
    /**
     * Returns a set of child activities for the given parent activitity
     * 
     * @param parentActivity The parent activity 
     * @return HashSet Set of the activities that belong to the parentActivity 
     */
    private HashSet getChildActivities(Activity parentActivity){
    	HashSet childActivities = new HashSet();
    	List list = activityDAO.getActivitiesByParentActivityId(parentActivity.getActivityId());
    	if(list!=null)
    		childActivities.addAll(list);
    	return childActivities;
    }		
	/**
	 * This method saves a new Learning Design to the database.
	 * It received a WDDX packet from flash, deserializes it
	 * and then finally persists it to the database.
	 * 
	 * Note: it does not validate the design - that must be done
	 * separately.
	 * 
	 * @param wddxPacket The WDDX packet received from Flash
	 * @return Long learning design id 
	 * @throws Exception
	 */
	public Long storeLearningDesignDetails(String wddxPacket) throws Exception {

		Hashtable table = (Hashtable)WDDXProcessor.deserialize(wddxPacket);
		IObjectExtractor extractor = (IObjectExtractor) beanFactory.getBean(IObjectExtractor.OBJECT_EXTRACTOR_SPRING_BEANNAME);
		LearningDesign design = extractor.extractSaveLearningDesign(table);	
		
		if(extractor.getMode().intValue() == 1)
			copyLearningDesignToolContent(design, design, design.getCopyTypeID());
		
		return design.getLearningDesignId();
	}

	
	/** 
	 * Validate the learning design, updating the valid flag appropriately.
	 * 
	 * This needs to be run in a separate transaction to storeLearningDesignDetails to 
	 * ensure the database is fully updated before the validation occurs (due to some
	 * quirks we are finding using Hibernate)
	 * 
	 * @param learningDesignId
	 * @throws Exception
	 */
	public Vector<ValidationErrorDTO> validateLearningDesign(Long learningDesignId) {
		LearningDesign learningDesign = learningDesignDAO.getLearningDesignById(learningDesignId);
		Vector<ValidationErrorDTO> listOfValidationErrorDTOs = learningDesignService.validateLearningDesign(learningDesign);
		Boolean valid = listOfValidationErrorDTOs.size() > 0 ? Boolean.FALSE : Boolean.TRUE;
		learningDesign.setValidDesign(valid);
		learningDesignDAO.insertOrUpdate(learningDesign);
		return listOfValidationErrorDTOs;
	}
	
	public Vector<AuthoringActivityDTO> getToolActivities(Long learningDesignId) {
		LearningDesign learningDesign = learningDesignDAO.getLearningDesignById(learningDesignId);
		Vector<AuthoringActivityDTO> listOfAuthoringActivityDTOs = new Vector<AuthoringActivityDTO>();
		
		for (Iterator i = learningDesign.getActivities().iterator(); i.hasNext();)
        {
            Activity currentActivity = (Activity) i.next();
            if (currentActivity.isToolActivity())
            {
            	try {
            		ToolActivity toolActivity = (ToolActivity) activityDAO.getActivityByActivityId(currentActivity.getActivityId());
            		AuthoringActivityDTO activityDTO = new AuthoringActivityDTO(toolActivity);
            		listOfAuthoringActivityDTOs.add(activityDTO);
            	} catch (ToolException e) {
                        String error = ""
                                +e.getMessage();
                        log.error(error,e);
                        throw new LearningDesignException(error,e);
                }
            }
        }
		
		return listOfAuthoringActivityDTOs;
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getAllLearningDesignDetails()
	 */
	public String getAllLearningDesignDetails()throws IOException{
		Iterator iterator= getAllLearningDesigns().iterator();
		ArrayList arrayList = createDesignDetailsPacket(iterator);
		FlashMessage flashMessage = new FlashMessage("getAllLearningDesignDetails",arrayList);		
		return flashMessage.serializeMessage();
	}
	/**
	 * This is a utility method used by the method 
	 * <code>getAllLearningDesignDetails</code> to pack the 
	 * required information in a data transfer object.
	 * 	  
	 * @param iterator 
	 * @return Hashtable The required information in a Hashtable
	 */
	private ArrayList createDesignDetailsPacket(Iterator iterator){
	    ArrayList arrayList = new ArrayList();
		while(iterator.hasNext()){
			LearningDesign learningDesign = (LearningDesign)iterator.next();
			DesignDetailDTO designDetailDTO = learningDesign.getDesignDetailDTO();
			arrayList.add(designDetailDTO);
		}
		return arrayList;
	}
	/**
	 * (non-Javadoc)
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getLearningDesignsForUser(java.lang.Long)
	 */
	public String getLearningDesignsForUser(Long userID) throws IOException{
		List list = learningDesignDAO.getLearningDesignByUserId(userID);
		ArrayList arrayList = createDesignDetailsPacket(list.iterator());
		FlashMessage flashMessage = new FlashMessage("getLearningDesignsForUser",arrayList);
		return flashMessage.serializeMessage();
	}	
	/**
	 * (non-Javadoc)
	 * @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getAllLearningLibraryDetails()
	 */
	public String getAllLearningLibraryDetails()throws IOException{
		FlashMessage flashMessage = new FlashMessage("getAllLearningLibraryDetails",learningDesignService.getAllLearningLibraryDetails());
		return flashMessage.serializeMessage();
	}
	
	/** @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getToolContentID(java.lang.Long) */

	public String getToolContentID(Long toolID) throws IOException
	{
	   Tool tool = toolDAO.getToolByID(toolID);
	   if (tool == null)
	   {
	       log.error("The toolID "+ toolID + " is not valid. A Tool with tool id " + toolID + " does not exist on the database.");
	       return FlashMessage.getNoSuchTool("getToolContentID", toolID).serializeMessage();
	   }
	   
	   Long newContentID = contentIDGenerator.getNextToolContentIDFor(tool);
	   FlashMessage flashMessage = new FlashMessage("getToolContentID", newContentID);
	   
	   return flashMessage.serializeMessage();
	}
	
	/** @see org.lamsfoundation.lams.authoring.service.IAuthoringService#copyToolContent(java.lang.Long) */
	public String copyToolContent(Long toolContentID) throws IOException
	{ 
		Long newContentID = lamsCoreToolService.notifyToolToCopyContent(toolContentID);
		FlashMessage flashMessage = new FlashMessage("copyToolContent", newContentID);
		return flashMessage.serializeMessage();
	}

	/** @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getAvailableLicenses() */
	public Vector getAvailableLicenses() {
		List licenses = licenseDAO.findAll(License.class);
		Vector licenseDTOList = new Vector(licenses.size());
		Iterator iter = licenses.iterator(); 
		while ( iter.hasNext() ) {
			License element = (License) iter.next();
			licenseDTOList.add(element.getLicenseDTO(Configuration.get(ConfigurationKeys.SERVER_URL)));
		}
		return licenseDTOList;
	}

	/** Delete a learning design from the database. Does not remove any content stored in tools - 
	 * that is done by the LamsCoreToolService */
	public void deleteLearningDesign(LearningDesign design) {
		if ( design == null ) {
			log.error("deleteLearningDesign: unable to delete learning design as design is null.");
			return;
		}
		
		// remove all the tool content for the learning design
		Set acts = design.getActivities();
		Iterator iter = acts.iterator();
		while (iter.hasNext()) {
			Activity activity = (Activity) iter.next();
            if (activity.isToolActivity())
            {
                try {
                	ToolActivity toolActivity = (ToolActivity) activityDAO.getActivityByActivityId(activity.getActivityId());
					lamsCoreToolService.notifyToolToDeleteContent(toolActivity);
				} catch (ToolException e) {
					log.error("Unable to delete tool content for activity"+activity
							+" as activity threw an exception",e);
				}
			}
		}
			
		// remove the learning design 
		learningDesignDAO.delete(design);
	}
	
	/** @see org.lamsfoundation.lams.authoring.service.IAuthoringService#generateUniqueContentFolder() */
	public String generateUniqueContentFolder() throws FileUtilException, IOException {
		
		String newUniqueContentFolderID = null;
		Properties props = new Properties();
		
		IdentifierGenerator uuidGen = new UUIDHexGenerator();
		( (Configurable) uuidGen).configure(Hibernate.STRING, props, null);
		
		// lowercase to resolve OS issues
		newUniqueContentFolderID = ((String) uuidGen.generate(null, null)).toLowerCase();
		
		FlashMessage flashMessage = new FlashMessage("createUniqueContentFolder", newUniqueContentFolderID);
		
		return flashMessage.serializeMessage();
	}
	
	/** @see org.lamsfoundation.lams.authoring.service.IAuthoringService#getHelpURL() */
	public String getHelpURL() throws Exception {
		
		FlashMessage flashMessage =null;
		
		String helpURL = Configuration.get(ConfigurationKeys.HELP_URL);
		if(helpURL != null)
			flashMessage = new FlashMessage("getHelpURL", helpURL);
		else 
			throw new Exception();
		
		return flashMessage.serializeMessage();
	}
	

	public MessageService getMessageService() {
		return messageService;
	}


}