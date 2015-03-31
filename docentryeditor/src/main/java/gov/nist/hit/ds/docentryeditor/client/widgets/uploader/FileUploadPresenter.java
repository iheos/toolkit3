package gov.nist.hit.ds.docentryeditor.client.widgets.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.requestfactory.shared.Receiver;
import gov.nist.hit.ds.docentryeditor.client.MetadataEditorRequestFactory;
import gov.nist.hit.ds.docentryeditor.client.XdsParserServices;
import gov.nist.hit.ds.docentryeditor.client.XdsParserServicesAsync;
import gov.nist.hit.ds.docentryeditor.client.event.NewFileLoadedEvent;
import gov.nist.hit.ds.docentryeditor.client.event.StartEditXdsDocumentEvent;
import gov.nist.hit.ds.docentryeditor.client.generics.abstracts.AbstractPresenter;
import gov.nist.hit.ds.docentryeditor.client.parse.XdsParser;
import gov.nist.hit.ds.docentryeditor.shared.model.XdsDocumentEntry;
import gov.nist.hit.ds.docentryeditor.shared.model.XdsMetadata;

import javax.inject.Inject;

public class FileUploadPresenter extends AbstractPresenter<FileUploadView> {

    @Inject
    XdsParser xdsParser;

    // RPC services declaration
    private final static XdsParserServicesAsync xdsParserServices = GWT
            .create(XdsParserServices.class);

    public void fileUploaded(String results) {
        // remove xml file first line (xml doctype => <?xml...>)
        logger.info("... file loaded, parsing metadata...");
        String s=results.replace("&lt;","<");
        s=s.replace("&gt;",">");
        logger.info(s);
        xdsParserServices.parseXdsMetadata(s, new AsyncCallback<XdsMetadata>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.warning(throwable.getMessage());
            }

            @Override
            public void onSuccess(XdsMetadata xdsMetadata) {
                XdsDocumentEntry model = xdsMetadata.getDocumentEntries().get(0);

                logger.info("... file parsed.");
                // logger.info("Metadata file: " + model.toXML());

                getEventBus().fireEvent(new NewFileLoadedEvent(model));
                //  This is the only way I found to make it work
                getEventBus().fireEvent(new StartEditXdsDocumentEvent(model));
            }
        });
//                XdsDocumentEntry model = xdsParser.parse(results.split(";\\^;\\^;")[1]);
//                model.setFileName(new String256().setString(results.split(";\\^;\\^;")[0]));

    }

    @Override
    public void init() {
        // don't need to do anything in that particular case
    }
}