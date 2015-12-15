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

/* $Id$ */

package org.lamsfoundation.lams.comments.dao.hibernate;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.comments.dao.ICommentLikeDAO;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CommentLikeDAO extends HibernateDaoSupport implements ICommentLikeDAO {
    private static Logger log = Logger.getLogger(CommentLikeDAO.class);
    
    private static String INSERT_LIKE = "INSERT IGNORE INTO lams_comment_likes(comment_uid, user_id, vote) VALUES (:comment,:user,:vote);";
	    
    public boolean addLike( Long commentUid, Integer userId, Integer vote) {
	int status = getSession().createSQLQuery(INSERT_LIKE)
		.setParameter("comment", commentUid)
		.setParameter("user", userId)
		.setParameter("vote", vote)
		.executeUpdate();

	log.debug("Insert returned "+status);
	return status == 1;
    }

}
