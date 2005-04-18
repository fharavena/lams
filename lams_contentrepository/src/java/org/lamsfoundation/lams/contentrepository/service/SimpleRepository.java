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

package org.lamsfoundation.lams.contentrepository.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.AccessDeniedException;
import org.lamsfoundation.lams.contentrepository.CrCredential;
import org.lamsfoundation.lams.contentrepository.CrNode;
import org.lamsfoundation.lams.contentrepository.CrWorkspace;
import org.lamsfoundation.lams.contentrepository.CrWorkspaceCredential;
import org.lamsfoundation.lams.contentrepository.FileException;
import org.lamsfoundation.lams.contentrepository.ICredentials;
import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.IVersionedNode;
import org.lamsfoundation.lams.contentrepository.IVersionedNodeAdmin;
import org.lamsfoundation.lams.contentrepository.InvalidParameterException;
import org.lamsfoundation.lams.contentrepository.ItemExistsException;
import org.lamsfoundation.lams.contentrepository.ItemNotFoundException;
import org.lamsfoundation.lams.contentrepository.LoginException;
import org.lamsfoundation.lams.contentrepository.NoSuchNodeTypeException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.NodeType;
import org.lamsfoundation.lams.contentrepository.PropertyName;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.RepositoryRuntimeException;
import org.lamsfoundation.lams.contentrepository.ValidationException;
import org.lamsfoundation.lams.contentrepository.WorkspaceNotFoundException;
import org.lamsfoundation.lams.contentrepository.dao.ICredentialDAO;
import org.lamsfoundation.lams.contentrepository.dao.IWorkspaceDAO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * Many methods in this class will throw a RepositoryRuntimeException
 * if the internal data is missing. This is not indicated
 * on the method signatures.
 * 
 * The methods in this class do not explicitly check that a credential
 * or ticket has been supplied. This is checked by the 
 * checkCredentialTicketBeforeAdvice advisor, for all transactioned
 * calls (see the application context file). Therefore this
 * class must be used in the Spring framework - if it is ever 
 * run separately and without suitable AOP support then 
 * each transaction method must check that the credential is okay
 * or that the ticket is a known ticket (isTicketOkay() method).
 *  
 * This class also depends on the transactions defined in the 
 * application context for the hibernate sessions to work properly.
 * If the method isn't transactioned, then there won't be a proper
 * hibernate session in the DAO and all sorts of errors will occur
 * on lazy loading (even lazy loading withing the DAO) and when we
 * write out nodes.
 * 
 * So while the only footprint you see here of Spring is the beanfactory,
 * the use of this as a singleton (generated by Spring) affects
 * more than just how the object is created.
 * 
 * @author Fiona Malikoff
 */
public class SimpleRepository implements IRepositoryAdmin, BeanFactoryAware {

	protected Logger log = Logger.getLogger(SimpleRepository.class);	

	private ICredentialDAO credentialDAO = null;
	private IWorkspaceDAO workspaceDAO = null;
	
	private BeanFactory beanFactory = null;
	
	private Set ticketIdSet = new HashSet(); // set of currently known tickets.

	public SimpleRepository () {
		log.info("Repository singleton being created.");
	}

	/* ********** Whole of repository methods - login, logout, addWorkspace, etc ****/
	
	/**
	 * @param workspaceName
	 * @return
	 * @throws WorkspaceNotFoundException
	 */
	private CrWorkspace getWorkspace(String workspaceName) throws WorkspaceNotFoundException {
		// call workspace dao to get the workspace
		CrWorkspace workspace = workspaceDAO.findByName(workspaceName);
		if ( workspace == null ) {
			throw new WorkspaceNotFoundException("Workspace "+workspaceName+" does not exist.");
		}
		return workspace;
	}

	/**
	 * @param workspaceId
	 * @return
	 * @throws WorkspaceNotFoundException
	 */
	private CrWorkspace getWorkspace(Long workspaceId) throws WorkspaceNotFoundException {
		// call workspace dao to get the workspace
		CrWorkspace workspace = (CrWorkspace) workspaceDAO.find(CrWorkspace.class, workspaceId);
		if ( workspace == null ) {
			throw new WorkspaceNotFoundException("Workspace id="+workspaceId+" does not exist.");
		}
		return workspace;
	}

	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#login(org.lamsfoundation.lams.contentrepository.ICredentials, java.lang.String)
	 */
	public ITicket login(ICredentials credentials, String workspaceName)
			throws AccessDeniedException, LoginException, WorkspaceNotFoundException {
		
		if ( workspaceDAO == null || credentialDAO == null ) 
			throw new RepositoryRuntimeException("Workspace or Credential DAO object missing. Unable to process login.");
		
		CrWorkspace workspace = getWorkspace(workspaceName);
		
		if ( ! credentialDAO.checkCredential(credentials, workspace) ) {
			throw new LoginException("Login failed. Password incorrect or not authorised to access this workspace.");
		}
		
		// okay, we should now be able to create a ticket
		// 	make ticket, create new credentials without the password 
		ITicket ticket =  new SimpleTicket(workspace.getWorkspaceId());
		ticketIdSet.add(ticket.getTicketId());
		return (ITicket) ticket;
	}


	/** Add a workspace, giving the credentials as the user of this workspace.
	 * It does not clear the password in the credentials
	 * @param credentials this user/password must already exist in the repository. Password will be checked.
	 * @param workspaceName 
	 * @throws LoginException if credentials are not authorised to add/access the new workspace.
	 * @throws ItemExistsException if the workspace already exists.
	 * @throws RepositoryCheckedException if parameters are missing.
	 */
	public void addWorkspace(ICredentials credentials, String workspaceName)
			throws AccessDeniedException, LoginException, ItemExistsException, RepositoryCheckedException {
		
		// call workspace dao to check the login and get the workspace
		if ( workspaceDAO == null || credentialDAO == null || beanFactory == null ) 
			throw new RepositoryRuntimeException("Workspace, Credential DAO or Bean Factory object missing. Unable to process login.");
		
		CrWorkspace workspace = workspaceDAO.findByName(workspaceName);
		if ( workspace != null ) {
			throw new ItemExistsException("Workspace "+workspaceName+" already exists, cannot add workspace.");
		}
		
		// check the credentials
		if ( ! credentialDAO.checkCredential(credentials ) ) {
			throw new LoginException("User not authorised to access the repository.");
		}
		
		// try to create the workspace - this should be done via the Spring bean factory.
		CrWorkspace crWorkspace = new CrWorkspace();
		crWorkspace.setName(workspaceName);
		workspaceDAO.insert(crWorkspace);
		assignCredentials(credentials, crWorkspace);
	}
		
	/**
	 * Create a new repository "user" - usually a tool.
	 * The password must be at least 6 chars.
	 * This method will not wipe out the password in the newCredential object.
	 * Possibly this should only be available to an internal management tool
	 * *** Security Risk - I'm converting two passwords to a string... ***
	 */
	public void createCredentials(ICredentials newCredential) 			
					throws AccessDeniedException, RepositoryCheckedException, ItemExistsException {
		if ( newCredential == null || newCredential.getName() == null || newCredential.getPassword() == null)
			throw new RepositoryCheckedException("Credential is null or name/password is missing - cannot create credential.");

		verifyNewPassword(newCredential.getPassword());

		// check that the user doesn't already exist
		CrCredential cred = credentialDAO.findByName(newCredential.getName());
		if ( cred != null )
			throw new ItemExistsException("Credential name "+newCredential.getName()+" already exists - cannot create credential.");
		
		// try to create the credential - this should be done via the Spring bean factory.
		cred = new CrCredential();
		cred.setName(newCredential.getName());
		cred.setPassword(new String(newCredential.getPassword()));
		credentialDAO.insert(cred);
	}

	/**
	 * Update a credential. Name cannot change, so really only the password changes
	 * The password must be at least 6 chars.
	 * Possibly this should only be available to an internal management tool
	 * *** Security Risk - I'm converting the password to a string... ***
	 * @throws LoginException if the oldCredential fails login test (e.g. wrong password)
	 * @throws RepositoryCheckedException if one of the credentials objects are missing
	 * @throws RepositoryRuntimeException if an internal error occurs.
	 */
	public void updateCredentials(ICredentials oldCredential, ICredentials newCredential)
					throws AccessDeniedException, LoginException, RepositoryCheckedException, RepositoryRuntimeException 
	{
				///throws RepositoryCheckedException {
		if ( workspaceDAO == null || credentialDAO == null ) 
			throw new RepositoryRuntimeException("Workspace or Credential DAO object missing. Cannot update credentials.");

		if ( oldCredential == null || newCredential == null )
			throw new RepositoryCheckedException("Credentials missing. Cannot update credentials.");

		if ( ! credentialDAO.checkCredential(oldCredential) ) 
			throw new LoginException("Old password wrong. Cannot update credentials.");
		
		char[] newPassword = newCredential.getPassword(); 
		if ( newPassword != null ) {
			// if there isn't a new password then there isn't anything to change...
			verifyNewPassword(newPassword);
			CrCredential cred = credentialDAO.findByName(oldCredential.getName());
			cred.setPassword(new String(newPassword));
			credentialDAO.update(cred);
		}
	}

	/**
	 * Checks that a password meets our password criteria. This could be implemented
	 * as a Strategy, but that's overkill!
	 * 
	 * Checks that the password is six or more characters.
	 * @param password
	 * @throws RepositoryCheckedException if 
	 */
	private void verifyNewPassword(char[] password ) throws RepositoryCheckedException {
		if ( password != null && password.length < 6 )
			throw new RepositoryCheckedException("Password invalid - must be 6 or more characters. Cannot create credential.");
	}

	/**
	 * Assign credentials to a workspace. 
	 * Will check the credentials to ensure they are in the database. 
	 * Possibly this should only be available to an internal management tool
	 */
	public void assignCredentials(ICredentials credentials, String workspaceName)
						throws AccessDeniedException, RepositoryCheckedException, WorkspaceNotFoundException, LoginException {

		if ( workspaceDAO == null) 
			throw new RepositoryRuntimeException("Workspace DAO object missing. Cannot assign credentials.");

		if ( credentials == null || workspaceName == null )
			throw new RepositoryCheckedException("Credentials or workspace is missing. Cannot assign credentials.");

		if ( ! credentialDAO.checkCredential(credentials) ) 
			throw new LoginException("Credentials are not authorised to have access to the repository/workspace.");
		
		// call workspace dao to get the workspace
		CrWorkspace workspace = getWorkspace(workspaceName);
		if ( workspace == null ) {
			throw new WorkspaceNotFoundException("Workspace "+workspaceName+" does not exist.");
		}
		
		assignCredentials(credentials, workspace);
	}

	/**
	 * Assign credentials to a workspace. Assume credentials are already checked. 
	 * Possibly this should only be available to an internal management tool. Workspace
	 * is expected to be attached to a session. 
	 * *** Security Risk - I'm converting the password to a string by reading it in from the database... ***
	 */
	private void assignCredentials(ICredentials credentials, CrWorkspace workspace)
						throws RepositoryCheckedException {

		if ( workspaceDAO == null || credentialDAO == null ) 
			throw new RepositoryRuntimeException("Workspace or Credential DAO object missing. Cannot assign credentials.");

		if ( credentials == null || workspace == null )
			throw new RepositoryCheckedException("Credentials or workspace is missing. Cannot assign credentials.");

		CrCredential crCredential = credentialDAO.findByName(credentials.getName());
		if ( crCredential == null )
			throw new RepositoryCheckedException("Credential object cannot be found in database. Cannot assign credentials.");

		CrWorkspaceCredential wc = new CrWorkspaceCredential();
		wc.setCrCredential(crCredential);
		wc.setCrWorkspace(workspace);

		Set wcSet = workspace.getCrWorkspaceCredentials();
		if ( wcSet == null ) {
			log.debug("Creating new wc set for workspace "+workspace.getName());
			wcSet = new HashSet();
			wcSet.add(wc);
			workspace.setCrWorkspaceCredentials(wcSet);
		} else {
			Iterator iter = wcSet.iterator();
			CrWorkspaceCredential found = null;
			while ( iter.hasNext() && found == null ) {
				CrWorkspaceCredential item = (CrWorkspaceCredential) iter.next();
				if ( item.getCrCredential() != null && 
					 item.getCrCredential().getCredentialId().equals(crCredential.getCredentialId()) &&
					 item.getCrWorkspace() != null && 
					 item.getCrWorkspace().getWorkspaceId().equals(workspace.getWorkspaceId()) ) {
					found = item;
				}
			}
			if ( found == null ) {
				// not already in the set, so we can add!
				wcSet.add(wc);
			}
		}

		workspaceDAO.insert(wc);
	}

	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#logout(org.lamsfoundation.lams.contentrepository.ITicket)
	 */
	public void logout(ITicket ticket) throws AccessDeniedException {
		ticketIdSet.remove(ticket.getTicketId());
		ticket.clear();
	}

	/* ********** Node related methods, requiring ticket for access ****/

	/** Is this ticket okay? 
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#addFileItem(org.lamsfoundation.lams.contentrepository.ITicket, java.io.InputStream, java.lang.String, java.lang.String, java.lang.String)
	 */
    public boolean isTicketOkay(ITicket ticket) {
		return ( ticket != null &&  ticketIdSet.contains(ticket.getTicketId()) );
    }

	/** Get an existing SimpleVersionedNode. Reads the node from the database.
	 * Does not cache the node. If versionId is null, then gets the latest version.
	 * @throws ItemNotFoundException*/
	private SimpleVersionedNode getNode(Long workspaceId, Long uuid, Long versionId) throws ItemNotFoundException {
		SimpleVersionedNode dbNode = (SimpleVersionedNode) beanFactory.getBean("node", SimpleVersionedNode.class);
		dbNode.loadData(workspaceId, uuid,versionId);
		return dbNode;
	}
	
    /**
     * Create a file node. Does not save the node.
	 */
	private SimpleVersionedNode createFileNode(CrWorkspace workspace, InputStream istream, String filename, 
					String mimeType, String versionDescription,
					String relPath, SimpleVersionedNode packageNode)
				throws InvalidParameterException, FileException, ValidationException {
		try {

    		SimpleVersionedNode initialNodeVersion = (SimpleVersionedNode) beanFactory.getBean("node", 
    				SimpleVersionedNode.class);
    		initialNodeVersion.initialiseNode(relPath, NodeType.FILENODE, (CrWorkspace) workspace, packageNode);
        	initialNodeVersion.setFile(istream, filename, mimeType);
        	return initialNodeVersion;
        	
    	} catch ( NoSuchNodeTypeException e) {
    		// if this is thrown, then it is bug - nothing external should cause it.
    		throw new RepositoryRuntimeException("Internal error: unable to add file. "
    				+e.getMessage(), e);
		}
	}


	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#addFileItem(org.lamsfoundation.lams.contentrepository.ITicket, java.io.InputStream, java.lang.String, java.lang.String, java.lang.String)
	 */
	public NodeKey addFileItem(ITicket ticket, InputStream istream,
			String filename, String mimeType, String versionDescription)
			throws FileException, AccessDeniedException,
			InvalidParameterException {
		
    	try { 
    		CrWorkspace workspace = getWorkspace(ticket.getWorkspaceId());
    		SimpleVersionedNode initialNodeVersion = createFileNode(workspace, istream, filename, mimeType, versionDescription, null, null);
       		initialNodeVersion.save(versionDescription, null);
    		return initialNodeVersion.getNodeKey();
		} catch ( ValidationException e) {
			// if this is thrown, then it is bug - nothing external should cause it.
		    throw new RepositoryRuntimeException("Internal error: unable to add file. "
		    				+e.getMessage(), e);
		} catch ( WorkspaceNotFoundException e ) {
			// if this is thrown, then it is bug - ticket shouldn't contain a workspace that doesn't exist.
			throw new RepositoryRuntimeException("Internal error: unable to add file. "
					+e.getMessage(), e);
		}
    }
    
   /**
     * Process files in the package.
     *  
	 * @param dirPath: the directory from which to get files. Mandatory.
	 * @param packageNode: node representing the package. Mandatory.
	 * @return nodeSet: set of child nodes for the package node 
	 * @throws InvalidParameterException
	 * @throws FileException
	 */
	private List processPackageFilesSaveNode(CrWorkspace workspace, String dirPath, SimpleVersionedNode packageNode, String versionDescription) 
				throws InvalidParameterException, FileException, ValidationException {
		
    	File directory = new File(dirPath);
    	if ( ! directory.exists() || ! directory.isDirectory() || ! directory.canRead() ) {
    		throw new FileException("Directory "+dirPath+" does not exist, is not a directory or cannot be read.");
    	}

    	// set up the path to be removed from file paths, to make a relative path.
    	// this is a directory but we may need to add the directory separator on the end
    	String removePathToMakeRelPath = directory.getAbsolutePath();
    	if ( removePathToMakeRelPath.charAt(removePathToMakeRelPath.length()-1) != File.separatorChar )
    		removePathToMakeRelPath += File.separatorChar;
		
    	List nodeList = new ArrayList();
    	processDirectory(workspace, removePathToMakeRelPath, directory, packageNode, versionDescription, nodeList);
    	return nodeList;
	}

	/** 
	 * Process Directory of files. This method is called recursively to process
     * files in the initial directory and in all subdirectories of this directory.
     * 
     * @param removePathToMakeRelPath: string to remove from a files absolute
     * path to create relPath, the path relative to the package node. This is
     * the absolute path to the directory that contains all the files for the package.
     * This value stays the same across all recursive calls. Mandatory.
	 * @param dirFile: the directory from which to get files. Initially this will
	 * be the directory that contains the package but as recursive calls are made,
	 * this value will change. Mandatory.
	 * @param packageNode: node representing the package. Mandatory.
	 * @param versionDescription: version description for node. Optional.
	 * This value stays the same across all recursive calls.
	 * @param nodeSet: set of nodes to update with the new nodes. Passed in through
	 * the recursion for efficiency (rather than keep creating new collections). Must
	 * not be null.
	 * @throws FileException
	 */
	private void processDirectory(CrWorkspace workspace, String removePathToMakeRelPath, File dirFile, SimpleVersionedNode packageNode, String versionDescription, List nodeList) 
			throws InvalidParameterException, FileException, ValidationException {

		if ( ! dirFile.exists() || ! dirFile.isDirectory() || ! dirFile.canRead() ) {
			throw new FileException("Directory "+dirFile.getAbsolutePath()+" does not exist, is not a directory or cannot be read.");
		}

		File file = null; // declare outside try so available in catch
		try {

			File[] files = dirFile.listFiles();
			for ( int i=0; i<files.length; i++ ) {
				file = files[i];

				log.debug("Processing file "+file.getAbsolutePath());
				log.debug("Name is "+file.getName());
	
				if ( file.isDirectory() ) {
					
					// recurse to get files in this directory
					processDirectory(workspace, removePathToMakeRelPath, file, packageNode, versionDescription, nodeList);
					
				} else {
					
					// get the name and relative path (from the package directory)
					// for this node. convert any \ in the relative path to /
					// as / is needed on retrieval.
					String filename = file.getName();
					String filePath = file.getPath();
					String relPath = StringUtils.replace(filePath,removePathToMakeRelPath,"");
					if ( filePath.length() == relPath.length() ) {
						// path hasn't shortened so something has gone wrong!
						throw new FileException("Unable to determine relative path of file. "
								+"Path to package is "+removePathToMakeRelPath
								+"Path to file is "+filePath
								+"Attempted relPath is "+relPath);
					}
					relPath = relPath.replace(File.separatorChar,'/');
					
					// Open the file ready for reading then create
					// the file node. Mime type is unknown.
					FileInputStream istream = new FileInputStream(file);
					IVersionedNodeAdmin newNode = createFileNode(workspace, istream, filename, null, versionDescription,
								relPath, packageNode);
					nodeList.add(newNode);
				}
			}
			
	    } catch ( FileNotFoundException fe) {
    		// how can this be when we just read them in? Maybe a privilege problem
	    	String message = "FileNotFoundException thrown while trying to read file in package. File path=\""
	    			+(file!=null?file.getAbsolutePath():"")+"\""; 
    		log.error(message,fe);
	    	throw new FileException("Internal error: unable to add package. "+message, fe);
	    } catch ( FileException e) {
	       	// catch this so we can document it against the file details (unknown
	    	// further down in the guts) - make sure we rethrow it.
	    	String message = "FileException thrown while trying to read file in package. File path=\""
    			+(file!=null?file.getAbsolutePath():"")+"\""; 
    		log.error(message,e);
	    	throw e;
    	}
	    
	}

	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#addPackageItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.String, java.lang.String, java.lang.String)
	 */
	public NodeKey addPackageItem(ITicket ticket, String dirPath,
			String startFile, String versionDescription)
			throws AccessDeniedException, InvalidParameterException,
			FileException {
		

		CrWorkspace workspace = null;
		try {
			workspace = getWorkspace(ticket.getWorkspaceId());
		} catch ( WorkspaceNotFoundException e ) {
			// if this is thrown, then it is bug - ticket shouldn't contain a workspace that doesn't exist.
			throw new RepositoryRuntimeException("Internal error: unable to add file. "
					+e.getMessage(), e);
		}

    	SimpleVersionedNode packageNode = null;
		try {

    		packageNode = (SimpleVersionedNode) beanFactory.getBean("node",	SimpleVersionedNode.class);
			packageNode.initialiseNode(null, NodeType.PACKAGENODE, (CrWorkspace) workspace, null);
	    	packageNode.setProperty(PropertyName.INITIALPATH, startFile);
    	} catch ( NoSuchNodeTypeException e) {
    		// if this is thrown, then it is bug - nothing external should cause it.
    		throw new RepositoryRuntimeException("Internal error: unable to add package." 
    				+e.getMessage(), e);
    	}
    	
		try { 
			// presave it to set the id. It will be resaved at the end of processing
			// the child nodes to update everything.
			packageNode.save(versionDescription, null);
			List nodeList = processPackageFilesSaveNode(workspace, dirPath, packageNode, versionDescription );    	
			packageNode.save(versionDescription, nodeList);
    	} catch ( ValidationException e) {
    		// if this is thrown, then it is bug - nothing external should cause it.
    		throw new RepositoryRuntimeException("Internal error: unable to add package."
    				+e.getMessage(), e);
		} 
		return packageNode.getNodeKey();

	}
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#getFileItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.Long)
	 */
	public IVersionedNode getFileItem(ITicket ticket, Long uuid, Long version)
			throws AccessDeniedException, ItemNotFoundException, FileException {
		
	   	return getNode(ticket.getWorkspaceId(), uuid, version);
 	}
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#getFileItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public IVersionedNode getFileItem(ITicket ticket, Long uuid, Long version ,
			String relPath) throws AccessDeniedException,
			ItemNotFoundException, FileException {

		long start = System.currentTimeMillis();
		String key = "getFileItem "+uuid;

		IVersionedNode latestNodeVersion = getNode(ticket.getWorkspaceId(),uuid,version);
		log.error(key+" latestNodeVersion "+(System.currentTimeMillis()-start));
		
		if ( relPath == null ) {
			
			// return the package node - getFile() on the package node
			// returns the input stream for the initial path node.
			return latestNodeVersion;
			
		} else {
    	
   			// find the node indicated by the relPath
			IVersionedNode childNode = latestNodeVersion.getNode(relPath); 
			log.error(key+" latestNodeVersion.getNode "+(System.currentTimeMillis()-start));
   			return childNode;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#getFileItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public List getPackageNodes(ITicket ticket, Long uuid, Long version) throws AccessDeniedException,
			ItemNotFoundException, FileException {

		long start = System.currentTimeMillis();
		IVersionedNodeAdmin latestNodeVersion = getNode(ticket.getWorkspaceId(),uuid,version);
		log.error("getPackageNodes latestNodeVersion "+(System.currentTimeMillis()-start));
		
		Set childNodes = latestNodeVersion.getChildNodes();
		int childNodesSize = childNodes != null ? childNodes.size() : 0;
		log.error("getPackageNodes getChildNodes "+(System.currentTimeMillis()-start));
		
		ArrayList list = new ArrayList(1+childNodesSize);
		list.add(latestNodeVersion);
		list.addAll(childNodes);

		log.error("getPackageNodes end "+(System.currentTimeMillis()-start));
		return list;
	}

	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#getNodeList(org.lamsfoundation.lams.contentrepository.ITicket)
	 */
	public SortedMap getNodeList(ITicket ticket) throws AccessDeniedException {
		
		Long workspaceId = ticket.getWorkspaceId();
		List nodes = workspaceDAO.findWorkspaceNodes(workspaceId);

		if ( log.isDebugEnabled() ) {
			log.debug("Workspace "+workspaceId+" has "+nodes.size()+" nodes.");
		}
		
		TreeMap map = new TreeMap();
		Iterator iter = nodes.iterator();
		while (iter.hasNext()) {
			CrNode node = (CrNode) iter.next();
			map.put(node.getNodeId(), node.getVersionHistory());
		}
		
    	return map;
	}
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#getVersionHistory(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long)
	 */
	public SortedSet getVersionHistory(ITicket ticket, Long uuid)
			throws ItemNotFoundException, AccessDeniedException {
		
	  	IVersionedNode node = getNode(ticket.getWorkspaceId(),uuid, null);
    	return node.getVersionHistory();
	}
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#updateFileItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.String, java.io.InputStream, java.lang.String, java.lang.String)
	 */
	public NodeKey updateFileItem(ITicket ticket, Long uuid, String filename,
			InputStream istream, String mimeType, String versionDescription)
			throws AccessDeniedException, ItemNotFoundException, FileException,
			InvalidParameterException {
	   	
		// check that the previous version was a file node - error otherwise
	   	SimpleVersionedNode latestNodeVersion = getNode(ticket.getWorkspaceId(),uuid,null);
	   	if ( ! latestNodeVersion.isNodeType( NodeType.FILENODE) )
	   		throw new InvalidParameterException("Node is not a file node - it is a "+latestNodeVersion.getNodeType()
	   				+". Unable to update as a file.");
		
		SimpleVersionedNode newNodeVersion = (SimpleVersionedNode) beanFactory.getBean("node", 
				SimpleVersionedNode.class);
		newNodeVersion.initialiseNewVersionOfNode(latestNodeVersion);
		newNodeVersion.setFile(istream, filename, mimeType);
		try {
			newNodeVersion.save(versionDescription, null);
    	} catch ( ValidationException e) {
    		// if this is thrown, then it is bug - nothing external should cause it.
    		throw new RepositoryRuntimeException("Internal error: unable to update file."
    				+e.getMessage(), e);
    	}    	
		return newNodeVersion.getNodeKey();
 	}
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#updatePackageItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public NodeKey updatePackageItem(ITicket ticket, Long uuid, String dirPath,
			String startFile, String versionDescription)
			throws AccessDeniedException, ItemNotFoundException, FileException,
			InvalidParameterException {
		
		// check that the previous version was a package node - error otherwise
	   	SimpleVersionedNode latestNodeVersion = getNode(ticket.getWorkspaceId(),uuid,null);
	   	if ( ! latestNodeVersion.isNodeType( NodeType.PACKAGENODE) )
	   		throw new InvalidParameterException("Node is not a package node - it is a "+latestNodeVersion.getNodeType()
	   				+". Unable to update as a package.");
		
		SimpleVersionedNode newPackageNode = (SimpleVersionedNode) beanFactory.getBean("node", 
				SimpleVersionedNode.class);
		newPackageNode.initialiseNewVersionOfNode(latestNodeVersion);
		newPackageNode.setProperty(PropertyName.INITIALPATH, startFile);
		
		try { 
			CrWorkspace workspace = getWorkspace(ticket.getWorkspaceId());
			
			// presave it to set the id. It will be resaved at the end of processing
			// the child nodes update everything.
			newPackageNode.save(versionDescription, null);
			List nodeList = processPackageFilesSaveNode(workspace, dirPath, newPackageNode, versionDescription);
			newPackageNode.save(versionDescription, nodeList);
    	} catch ( ValidationException e) {
    		// if this is thrown, then it is bug - nothing external should cause it.
    		throw new RepositoryRuntimeException("Internal error: unable to add package."
    				+e.getMessage(), e);
		} catch ( WorkspaceNotFoundException e ) {
			// if this is thrown, then it is bug - ticket shouldn't contain a workspace that doesn't exist.
			throw new RepositoryRuntimeException("Internal error: unable to add file. "
					+e.getMessage(), e);
		}
		return newPackageNode.getNodeKey();
 	}

	
	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#updatePackageItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String[] deleteNode(ITicket ticket, Long uuid) 
		throws AccessDeniedException, InvalidParameterException, ItemNotFoundException {
		
		if ( uuid == null )
			throw new InvalidParameterException("UUID is required for deleteItem.");

		// get the first version of the node and delete from there.
	   	SimpleVersionedNode latestNodeVersion = getNode(ticket.getWorkspaceId(),uuid,new Long(1));
	   	if ( latestNodeVersion.hasParentNode() ) {
	   		throw new InvalidParameterException("You cannot delete a node that is in a package (ie has a parent). "
	   				+"Please delete the parent. Node UUID "+uuid);
	   	}
	   	List problemPaths = latestNodeVersion.deleteNode(); 
	   	return problemPaths != null ? 
	   			(String[]) problemPaths.toArray(new String[problemPaths.size()]) : 
	   			new String[0];

 	}

	/* (non-Javadoc)
	 * @see org.lamsfoundation.lams.contentrepository.IRepository#updatePackageItem(org.lamsfoundation.lams.contentrepository.ITicket, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String[] deleteVersion(ITicket ticket, Long uuid, Long version) 
		throws AccessDeniedException, InvalidParameterException, ItemNotFoundException {
		
		if ( uuid == null || version == null )
			throw new InvalidParameterException("Both uuid and version are required for deleteVersion.");
		
		// get the first version of the node and delete from there.
	   	SimpleVersionedNode nodeVersion = getNode(ticket.getWorkspaceId(),uuid,version);
	   	List problemPaths = nodeVersion.deleteVersion();
	   	return problemPaths != null ? 
	   			(String[]) problemPaths.toArray(new String[problemPaths.size()]) : 
	   			new String[0];
		
 	}

	
	/* ********** setters and getters for DAOs *******************/
	/**
	 * @return Returns the workspaceDAO.
	 */
	public IWorkspaceDAO getWorkspaceDAO() {
		return workspaceDAO;
	}
	/**
	 * @param workspaceDAO The workspaceDAO to set.
	 */
	public void setWorkspaceDAO(IWorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}
	/**
	 * @return Returns the credentialDAO.
	 */
	public ICredentialDAO getCredentialDAO() {
		return credentialDAO;
	}
	/**
	 * @param credentialDAO The credentialDAO to set.
	 */
	public void setCredentialDAO(ICredentialDAO credentialDAO) {
		this.credentialDAO = credentialDAO;
	}
	
	/* **** Method for BeanFactoryAware interface *****************/
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;	
	}
}
