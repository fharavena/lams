package org.lamsfoundation.lams.contentrepository;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 *        @hibernate.class
 *         table="lams_cr_credential"
 *     
*/
public class CrCredential implements Serializable {

    /** identifier field */
    private Long credentialId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String password;

    /** persistent field */
    private Set crWorkspaceCredentials;

    /** full constructor */
    public CrCredential(String name, String password, Set crWorkspaceCredentials) {
        this.name = name;
        this.password = password;
        this.crWorkspaceCredentials = crWorkspaceCredentials;
    }

    /** default constructor */
    public CrCredential() {
    }

    /** 
     *            @hibernate.id
     *             generator-class="identity"
     *             type="java.lang.Long"
     *             column="credential_id"
     *             unsaved-value="0"
     *         
     */
    public Long getCredentialId() {
        return this.credentialId;
    }

    public void setCredentialId(Long credentialId) {
        this.credentialId = credentialId;
    }

    /** 
     *            @hibernate.property
     *             column="name"
     *             unique="true"
     *             length="255"
     *             not-null="true"
     *         
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** 
     *            @hibernate.property
     *             column="password"
     *             length="255"
     *             not-null="true"
     *         
     */
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** 
     * bi-directional one-to-many association to CrWorkspaceCredential 
     * 
     *            @hibernate.set
     *             lazy="true"
     *             inverse="true"
     *             cascade="none"
     *            @hibernate.collection-key
     *             column="credential_id"
     *            @hibernate.collection-one-to-many
     *             class="org.lamsfoundation.lams.contentrepository.CrWorkspaceCredential"
     *         
     */
    public Set getCrWorkspaceCredentials() {
        return this.crWorkspaceCredentials;
    }

    public void setCrWorkspaceCredentials(Set crWorkspaceCredentials) {
        this.crWorkspaceCredentials = crWorkspaceCredentials;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("credentialId", getCredentialId())
            .append("name", getName())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof CrCredential) ) return false;
        CrCredential castOther = (CrCredential) other;
        return new EqualsBuilder()
            .append(this.getCredentialId(), castOther.getCredentialId())
            .append(this.getName(), castOther.getName())
            .append(this.getPassword(), castOther.getPassword())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCredentialId())
            .append(getName())
            .append(getPassword())
            .toHashCode();
    }

}
