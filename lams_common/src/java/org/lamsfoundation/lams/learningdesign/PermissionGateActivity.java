package org.lamsfoundation.lams.learningdesign;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @hibernate.class
 */
public class PermissionGateActivity extends GateActivity implements Serializable
{
    
    /** full constructor */
    public PermissionGateActivity(Long activityId,
            Integer id,
            String description,
            String title,
            Integer xcoord,
            Integer ycoord,
            Integer orderId,
            Boolean defineLater,
            java.util.Date createDateTime,
            String offlineInstructions,
            LearningLibrary learningLibrary,
            Activity parentActivity,
            Activity libraryActivity,
			Integer parentUIID,
            LearningDesign learningDesign,
            Grouping grouping,
            Integer activityTypeId,
            Transition transitionTo,
            Transition transitionFrom,
            Integer gateActivityLevelId)
    {
        super(activityId,
                id,
                description,
                title,
                xcoord,
                ycoord,
                orderId,
                defineLater,
                createDateTime,
                offlineInstructions,
                learningLibrary,
                parentActivity,
				libraryActivity,
				parentUIID,
                learningDesign,
                grouping,
                activityTypeId,
                transitionTo,
				transitionFrom,
                gateActivityLevelId);
    }
    
    /** default constructor */
    public PermissionGateActivity()
    {
    }
    
    /** minimal constructor */
    public PermissionGateActivity(Long activityId,
            Boolean defineLater,
            java.util.Date createDateTime,
            org.lamsfoundation.lams.learningdesign.LearningLibrary learningLibrary,
            org.lamsfoundation.lams.learningdesign.Activity parentActivity,
            org.lamsfoundation.lams.learningdesign.LearningDesign learningDesign,
            org.lamsfoundation.lams.learningdesign.Grouping grouping,
            Integer activityTypeId,
            Transition transitionTo,
            Transition transitionFrom,
            Integer gateActivityLevelId)
    {
        super(activityId,
                defineLater,
                createDateTime,
                learningLibrary,
                parentActivity,
                learningDesign,
                grouping,
                activityTypeId,
                transitionTo,
				transitionFrom,
                gateActivityLevelId);
    }
    public static PermissionGateActivity createCopy(PermissionGateActivity originalActivity){
    	PermissionGateActivity newPermissionGateActivity = new PermissionGateActivity();
    	
    	newPermissionGateActivity.setActivityUIID(originalActivity.getActivityUIID());
    	newPermissionGateActivity.setDescription(originalActivity.getDescription());
    	newPermissionGateActivity.setTitle(originalActivity.getTitle());
    	newPermissionGateActivity.setXcoord(originalActivity.getXcoord());
    	newPermissionGateActivity.setYcoord(originalActivity.getYcoord());
    	newPermissionGateActivity.setDefineLater(originalActivity.getDefineLater());
    	newPermissionGateActivity.setCreateDateTime(new Date());
    	newPermissionGateActivity.setOfflineInstructions(originalActivity.getOfflineInstructions());
    	newPermissionGateActivity.setLearningLibrary(originalActivity.getLearningLibrary());
    	newPermissionGateActivity.setActivityTypeId(originalActivity.getActivityTypeId());
    	
    	/**
    	 * TODO calculate how these two values would be set for COPY 
    	 * */
    	//newPermissionGateActivity.setTransitionTo();
    	//newPermissionGateActivity.setTransitionFrom();
    	
    	return newPermissionGateActivity;
    	
    }
    
    public String toString()
    {
        return new ToStringBuilder(this)
        .append("activityId", getActivityId())
        .toString();
    }
    
}
