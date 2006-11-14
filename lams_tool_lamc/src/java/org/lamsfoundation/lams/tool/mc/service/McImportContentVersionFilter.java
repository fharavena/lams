package org.lamsfoundation.lams.tool.mc.service;

import org.lamsfoundation.lams.learningdesign.service.ToolContentVersionFilter;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueContent;
import org.lamsfoundation.lams.tool.mc.pojos.McUsrAttempt;

/**
 * Import filter class for different version of MC content.
 * @author steven
 *
 */
public class McImportContentVersionFilter extends ToolContentVersionFilter{

	/**
	 * Import 1.0 version content to 1.1 version tool server.
	 *
	 */
	public void up10To20061015(){
		this.removeField(McQueContent.class, "weight");
		this.removeField(McUsrAttempt.class, "timeZone");
		
	}
	
	public void up20061015To20061113(){
		// Change name to suit the version you give the tool.
	    this.removeField(McContent.class, "showMarks");
	}
}
