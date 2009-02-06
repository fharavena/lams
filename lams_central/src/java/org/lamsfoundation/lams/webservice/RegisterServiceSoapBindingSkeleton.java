/**
 * RegisterServiceSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.lamsfoundation.lams.webservice;


public class RegisterServiceSoapBindingSkeleton implements org.lamsfoundation.lams.webservice.Register, org.apache.axis.wsdl.Skeleton {
    private org.lamsfoundation.lams.webservice.Register impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        addCreateUser();
        addCreateGroup();
        addAddUserToGroup();
        addAddUserToGroupLessons();
        addAddUserToSubgroup();
        addAddUserToSubgroupLessons();
    }
    
    private static void addCreateUser() {
    	org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "firstName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "lastName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "email"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "serverId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datetime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hash"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("createUser", _params, new javax.xml.namespace.QName("", "createUserReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "createUser"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("createUser") == null) {
            _myOperations.put("createUser", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("createUser")).add(_oper);
    }
    
    private static void addCreateGroup() {
	org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "description"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "owner"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "serverId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datetime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hash"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("createGroup", _params, new javax.xml.namespace.QName("", "createGroupReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "createGroup"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("createGroup") == null) {
            _myOperations.put("createGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("createGroup")).add(_oper);
    }
    
    private static void addAddUserToGroup() {
    	org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "serverId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datetime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hash"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "countryIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "langIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isTeacher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "boolean"), java.lang.Boolean.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("addUserToGroup", _params, new javax.xml.namespace.QName("", "addUserToGroupReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "addUserToGroup"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("addUserToGroup") == null) {
            _myOperations.put("addUserToGroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("addUserToGroup")).add(_oper);
    }
    
    private static void addAddUserToGroupLessons() {
    	org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "serverId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datetime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hash"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "countryIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "langIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "asStaff"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "boolean"), java.lang.Boolean.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("addUserToGroupLessons", _params, new javax.xml.namespace.QName("", "addUserToGroupLessonsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "addUserToGroupLessons"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("addUserToGroupLessons") == null) {
            _myOperations.put("addUserToGroupLessons", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("addUserToGroupLessons")).add(_oper);
    }
    
    private static void addAddUserToSubgroup() {
    	org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "serverId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datetime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hash"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "countryIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "langIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "subgroupId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "subgroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isTeacher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "boolean"), java.lang.Boolean.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("addUserToSubgroup", _params, new javax.xml.namespace.QName("", "addUserToSubgroupReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "addUserToSubgroup"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("addUserToSubgroup") == null) {
            _myOperations.put("addUserToSubgroup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("addUserToSubgroup")).add(_oper);
    }
    
    private static void addAddUserToSubgroupLessons() {
    	org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "serverId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datetime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hash"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "courseName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "countryIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "langIsoCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "subgroupId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "subgroupName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "asStaff"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "boolean"), java.lang.Boolean.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("addUserToSubgroupLessons", _params, new javax.xml.namespace.QName("", "addUserToSubgroupLessonsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "addUserToSubgroupLessons"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("addUserToSubgroupLessons") == null) {
            _myOperations.put("addUserToSubgroupLessons", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("addUserToSubgroupLessons")).add(_oper);
    }

    public RegisterServiceSoapBindingSkeleton() {
        this.impl = new org.lamsfoundation.lams.webservice.RegisterServiceSoapBindingImpl();
    }

    public RegisterServiceSoapBindingSkeleton(org.lamsfoundation.lams.webservice.Register impl) {
        this.impl = impl;
    }
    
    public boolean createUser(String username, String password, String firstName, String lastName, String email, String serverId, String datetime, String hash) throws java.rmi.RemoteException
    {
        boolean ret = impl.createUser(username, password, firstName, lastName, email, serverId, datetime, hash);
        return ret;
    }
    
    public int createGroup(String name, String code, String description, String owner, String serverId, String datetime, String hash) throws java.rmi.RemoteException
    {
        int ret = impl.createGroup(name, code, description, owner, serverId, datetime, hash);
        return ret;
    }
    
    public boolean addUserToGroup(String username, String serverId, String datetime, String hash, 
    		String courseId, String courseName, String countryIsoCode, String langIsoCode, Boolean isTeacher) throws java.rmi.RemoteException {
		boolean ret = impl.addUserToGroup(username, serverId, datetime, hash, 
				courseId, courseName, countryIsoCode, langIsoCode, isTeacher);
		return ret;
	}
	
	public boolean addUserToGroupLessons(String username, String serverId, String datetime, String hash,
			String courseId, String courseName, String countryIsoCode, String langIsoCode, Boolean asStaff) throws java.rmi.RemoteException {
		boolean ret = impl.addUserToGroupLessons(username, serverId, datetime, hash, 
				courseId, courseName, countryIsoCode, langIsoCode, asStaff);
		return ret;
	}

	public boolean addUserToSubgroup(String username, String serverId, String datetime, String hash, 
			String courseId, String courseName, String countryIsoCode, String langIsoCode, String subgroupId, String subgroupName, Boolean isTeacher) throws java.rmi.RemoteException {
		boolean ret = impl.addUserToSubgroup(username, serverId, datetime, hash, 
				courseId, courseName, countryIsoCode, langIsoCode, 
				subgroupId, subgroupName, isTeacher);
		return ret;
	}
	
	public boolean addUserToSubgroupLessons(String username, String serverId, String datetime, String hash,
			String courseId, String courseName, String countryIsoCode, String langIsoCode, String subgroupId, String subgroupName, Boolean asStaff) throws java.rmi.RemoteException {
		boolean ret = impl.addUserToSubgroupLessons(username, serverId, datetime, hash, 
				courseId, courseName, countryIsoCode, langIsoCode, 
				subgroupId, subgroupName, asStaff);
		return ret;
	}
}
