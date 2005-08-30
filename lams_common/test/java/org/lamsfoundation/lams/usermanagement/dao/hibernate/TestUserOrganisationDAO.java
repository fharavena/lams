/* 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

http://www.gnu.org/licenses/gpl.txt 
*/
package org.lamsfoundation.lams.usermanagement.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.test.AbstractCommonTestCase;

/**
 * @author Manpreet Minhas
 */
public class TestUserOrganisationDAO extends AbstractCommonTestCase {
	
	protected UserOrganisationDAO userOrganisationDAO;
	protected UserDAO userDAO;
	
	public TestUserOrganisationDAO(String name){
		super(name);
	}
	protected void setUp()throws Exception{
		super.setUp();
		userOrganisationDAO = (UserOrganisationDAO)context.getBean("userOrganisationDAO");
		userDAO =(UserDAO)context.getBean("userDAO");
	}

	public void testGetUserOrganisationsByUser(){
		List memberships = userOrganisationDAO.getUserOrganisationsByUser(userDAO.getUserById(new Integer(4)));
		assertEquals(memberships.size(),4);
	}

}
