package de.hrz.kundenportal.views;

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
    	/*
		while(true) 
		{
			Thread x=new Thread() {@Override
			public void run() {
				try {
					//FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("foo:bar");
					PrimeFaces.current().ajax().update("editor2");
					
					text+="_nextup_";
					
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			x.start();
		}
		*/
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