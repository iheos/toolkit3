package gov.nist.toolkit.xdstools3.client.customWidgets.forms;

import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;

/**
 * Smartgwt DynamicForm with a few formatting options, for use throughout the toolkit.
 */
public class GenericForm extends DynamicForm {

    public GenericForm(){
       // setLayoutAlign(VerticalAlignment.CENTER);
        setCellPadding(10);
        //setColWidths(350);
    }

}
