/* ********************************************************************************
 *  Copyright Notice
 *  =================
 * This file contains propriety information of LAMS Foundation. 
 * Copying or reproduction with prior written permission is prohibited.
 * Copyright (c) 2004 
 * Created on 2004-12-23
 ******************************************************************************** */

package org.lamsfoundation.lams.tool.mc;

import java.util.Iterator;
import java.util.List;

import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;


/*
 * 
 * @author ozgurd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class TestMcOptionsContent extends McDataAccessTestCase
{
	protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public TestMcOptionsContent(String name)
    {
        super(name);
    }
    
    /*	
    public void testCreateMcOptionsContent()
    {
    	McQueContent mcQueContent = mcQueContentDAO.getMcQueContentByUID(new Long(1));
    	McOptsContent mcOptionsContent= new McOptsContent(new Long(777), true, "red", mcQueContent, new HashSet());
    	mcOptionsContentDAO.saveMcOptionsContent(mcOptionsContent);
    	
    	McOptsContent mcOptionsContent2= new McOptsContent(new Long(888), false, "blue", mcQueContent, new HashSet());
    	mcOptionsContentDAO.saveMcOptionsContent(mcOptionsContent2);
    	
    	McOptsContent mcOptionsContent3= new McOptsContent(new Long(999), false, "yellow", mcQueContent, new HashSet());
    	mcOptionsContentDAO.saveMcOptionsContent(mcOptionsContent3);
    }
  */
    
    public void testRetrieveMcOptionsContent()
    {
    	List list=mcOptionsContentDAO.findMcOptionsContentByQueId(new Long(1));
    	System.out.print("list:" + list);
    	
    	Iterator listIterator=list.iterator();
    	while (listIterator.hasNext())
    	{
    		McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
    		System.out.println("option text:" + mcOptsContent.getMcQueOptionText());
    	}
    }
    
}



