package com.gzmelife.app.dao;

import java.io.Serializable;

public class Share_data_entity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ImageURL;
	private String Title;
	private String Text;
	private String TargeURL;
	
	public String getImageURL() { 
        return ImageURL; 
    } 
    public void setImageURL(String ImageURL) { 
        this.ImageURL = ImageURL; 
    } 
    
    public String getTitle() { 
        return Title; 
    } 
    public void setTitle(String Title) { 
        this.Title = Title; 
    } 
    
    public String getText() { 
        return Text; 
    } 
    public void setText(String Text) { 
        this.Text = Text; 
    } 
    
    public String getTargeURL() { 
        return TargeURL; 
    } 
    public void setTargeURL(String TargeURL) { 
        this.TargeURL = TargeURL; 
    } 
}
