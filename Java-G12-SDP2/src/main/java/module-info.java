module be.hogent.Java_G12_SDP_22 { 
    requires javafx.controls; 
	requires jakarta.persistence; 
	requires lombok; 
	requires org.kordamp.ikonli.javafx; 
	requires org.kordamp.ikonli.bootstrapicons; 
	requires org.apache.pdfbox; 
	requires javafx.swing; 
	requires javafx.media; 
	requires jbcrypt; 
	 
	exports domain.report; 
    exports domain.maintenance; 
    exports domain.site; 
    exports domain.user; 
    exports domain.machine; 
    exports domain.notifications; 
    exports domain; 
    exports main; 
    
    opens images;
    opens css;
 
    opens domain to org.eclipse.persistence.core, jakarta.persistence; 
    opens domain.machine to org.eclipse.persistence.core, jakarta.persistence; 
    opens domain.report to org.eclipse.persistence.core, jakarta.persistence; 
    opens domain.maintenance to org.eclipse.persistence.core, jakarta.persistence; 
    opens domain.site to org.eclipse.persistence.core, jakarta.persistence; 
    opens domain.user to org.eclipse.persistence.core, jakarta.persistence; 
    opens domain.notifications to org.eclipse.persistence.core, jakarta.persistence; 
} 
