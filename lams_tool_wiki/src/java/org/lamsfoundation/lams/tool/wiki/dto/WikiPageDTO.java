package org.lamsfoundation.lams.tool.wiki.dto;

import org.lamsfoundation.lams.tool.wiki.model.WikiPage;

public class WikiPageDTO implements Comparable<WikiPageDTO> {

    private Long uid;
    private String title;
    private String javaScriptTitle;
    private Boolean editable;
    private WikiPageContentDTO currentWikiContentDTO;

    public WikiPageDTO() {
    }

    public WikiPageDTO(WikiPage wikiPage) {
	this.uid = wikiPage.getUid();
	this.title = wikiPage.getTitle().trim();
	this.editable = wikiPage.getEditable();
	this.currentWikiContentDTO = new WikiPageContentDTO(wikiPage.getCurrentWikiContent());
	this.javaScriptTitle = this.javaScriptEscape(wikiPage.getTitle());
    }

    public Long getUid() {
	return uid;
    }

    public void setUid(Long uid) {
	this.uid = uid;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public Boolean getEditable() {
	return editable;
    }

    public void setEditable(Boolean editable) {
	this.editable = editable;
    }

    public WikiPageContentDTO getCurrentWikiContentDTO() {
	return currentWikiContentDTO;
    }
    
    public String getJavaScriptTitle() {
        return javaScriptTitle;
    }

    public void setJavaScriptTitle(String javaScriptTitle) {
        this.javaScriptTitle = javaScriptTitle;
    }

    public void setCurrentWikiContentDTO(WikiPageContentDTO currentWikiContentDTO) {
	this.currentWikiContentDTO = currentWikiContentDTO;
    }

    public int compareTo(WikiPageDTO wikiPageDTO) {
	return wikiPageDTO.getUid().compareTo(uid) * -1;
    }
    
    public String javaScriptEscape(String string)
    {
	
	String replaced = string.replaceAll("\n", "").replaceAll("\'", "`").replaceAll("\"","\\&quot;");;
	//return string.replaceAll("\'", "\\\\'").replaceAll("\"","\\\\\"");
	return replaced;
    }

}
