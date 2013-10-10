/*
 * XmlUtil.java
 *
 * Created on November 2, 2004, 11:15 AM
 */

package gov.nist.hit.ds.utilities.xml;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
	static public OMFactory om_factory = OMAbstractFactory.getOMFactory();
	static public OMNamespace xml_namespace =   XmlUtil.om_factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");
	public static QName id_qname = new QName("id");
	
	public static OMElement firstChildWithLocalName(OMElement ele, String localName) {
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				return child;
		}
		return null;
	}

	public static OMElement firstChildWithLocalNameEndingWith(OMElement ele, String localNameSuffix) {
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().endsWith(localNameSuffix))
				return child;
		}
		return null;
	}

	public static List<OMElement> childrenWithLocalName(OMElement ele, String localName) {
		List<OMElement> al = new ArrayList<OMElement>();
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				al.add(child);
		}
		return al;
	}

	public static List<String> childrenLocalNames(OMElement ele) {
		List<String> al = new ArrayList<String>();
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
				al.add(child.getLocalName());
		}
		return al;
	}

	public static OMElement firstDecendentWithLocalName(OMElement ele, String localName) {
		List<OMElement> decendents = decendentsWithLocalName(ele, localName);
		if (decendents.size() == 0) return null;
		return decendents.get(0);
	}
	
	public static List<OMElement> decendentsWithLocalName(OMElement ele, String localName) {
		return decendentsWithLocalName(ele, localName, -1);
	}

		public static List<OMElement> decendentsWithLocalName(OMElement ele, String localName, int depth) {
		List<OMElement> al = new ArrayList<OMElement>();
		if (ele == null || localName == null)
			return al;
		decendentsWithLocalName1(al, ele, localName, depth);
		return al;
	}

	private static void decendentsWithLocalName1(List<OMElement> decendents, OMElement ele, String localName, int depth) {
		if (depth == 0)
			return;
		for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				decendents.add(child);
			decendentsWithLocalName1(decendents, child, localName, depth - 1);
		}
	}
	
	public static OMElement createElement(String localName, OMNamespace ns) {
		return XmlUtil.om_factory.createOMElement(localName, ns);
	}

	public static OMElement addChild(String localName, OMNamespace ns, OMElement parent) {
		return XmlUtil.om_factory.createOMElement(localName, ns, parent);
	}
	

    static public String XmlWriter(Node node) {
    	return XmlFormatter.format(XmlFormatter.normalize(XmlUtil.getStringFromNode(node)),false);
    }
    
    static public String getStringFromNode(Node node) {
        StringBuffer sb = new StringBuffer();
        if ( node == null ) {
            return null;
        }
        
        int type = node.getNodeType();
        switch ( type ) {
            case Node.DOCUMENT_NODE:
                sb.append("");
                sb.append(XmlUtil.getStringFromNode((((Document)node).getDocumentElement())));
                break;
                
            case Node.ELEMENT_NODE:
                sb.append('<');
                
                sb.append(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                
                for ( int i = 0; i < attrs.getLength(); i++ ) {
                    sb.append(' ');
                    sb.append(attrs.item(i).getNodeName());
                    sb.append("=\"");
                    
                    sb.append(attrs.item(i).getNodeValue());
                    sb.append('"');
                }
                sb.append('>');
                sb.append("\n"); // HACK
                NodeList children = node.getChildNodes();
                if ( children != null ) {
                    int len = children.getLength();
                    for ( int i = 0; i < len; i++ ) {
                        sb.append(XmlUtil.getStringFromNode(children.item(i)));
                    }
                }
                break;
                
            case Node.TEXT_NODE:
                sb.append(node.getNodeValue());
                break;
                
        }
        
        if ( type == Node.ELEMENT_NODE ) {
            sb.append("</");
            sb.append(node.getNodeName());
            sb.append(">");
            sb.append("\n"); // HACK
        }
        
        return sb.toString();
    }
    
    /**
     * Get child of ele with matching name and id attribute.
     * @param ele
     * @param localName
     * @param id
     * @return
     */
    public static OMElement getChild(OMElement ele, String localName, String id) {
            for (Iterator<?> it=ele.getChildElements(); it.hasNext(); ) {
                    OMElement child = (OMElement) it.next();
                    if (child.getLocalName().equals(localName)) {
                            String idAttVal = child.getAttributeValue(id_qname);
                            if (idAttVal != null && idAttVal.equals(id))
                                    return child;
                    }
            }
            return null;
    }
}
