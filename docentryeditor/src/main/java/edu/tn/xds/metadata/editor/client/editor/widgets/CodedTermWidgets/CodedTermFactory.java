package edu.tn.xds.metadata.editor.client.editor.widgets.CodedTermWidgets;

import com.google.gwt.core.client.GWT;
import edu.tn.xds.metadata.editor.client.generics.GridModelFactory;
import edu.tn.xds.metadata.editor.shared.model.CodedTerm;
import edu.tn.xds.metadata.editor.shared.model.CodingScheme;
import edu.tn.xds.metadata.editor.shared.model.String256;

public enum CodedTermFactory implements GridModelFactory<CodedTerm> {
    instance;

    @Override
    public CodedTerm newInstance() {
        CodedTerm e = GWT.create(CodedTerm.class);
        e.setDisplayName(new String256("New Coded Term name"));
        e.setCodingScheme(new CodingScheme(new String256("New Coded Term coding scheme")));
        e.setCode(new String256("New Coded term code"));
        return e;
    }
}