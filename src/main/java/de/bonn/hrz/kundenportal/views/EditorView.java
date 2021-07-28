package de.bonn.hrz.kundenportal.views;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.PrimeFaces;


@ManagedBean
@Named
@RequestScoped
public class EditorView {

    private String text="text1";

    private String text2="text2";
    
    public EditorView() {
    	
	}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text+"_appendix_";
        PrimeFaces.current().ajax().update("editor1");
		
    
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }
}