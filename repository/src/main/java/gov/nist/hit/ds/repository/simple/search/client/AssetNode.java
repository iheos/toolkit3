package gov.nist.hit.ds.repository.simple.search.client;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AssetNode implements IsSerializable, Serializable {

	/**
	 * AssetNode is essentially a light-weight DTO that carries out key asset information from the Asset type object
	 * 
	 * @author Sunil.Bhaskarla
	 */
	private static final long serialVersionUID = -46123676112710466L;
	
	private String repId;
	private String assetId;
	private String type;
	private String displayName;
	private String description;
	private String mimeType;
	private String reposSrc;
	
	private List<AssetNode> children = new ArrayList<AssetNode>();
	
	public AssetNode() {
		
	}
	
	/**
	 * @param repId
	 * @param assetId
	 * @param type
	 * @param displayName
	 * @param description
	 */
	public AssetNode(String repId, String assetId, String type,
			String displayName, String description, String mimeType, String src) {
		super();
		this.repId = repId;
		this.assetId = assetId;
		this.type = type;
		this.displayName = displayName;
		this.description = description;
		this.mimeType = mimeType;
		this.reposSrc = src;
	}

	public List<AssetNode> getChildren() {
		return children;
	}
	
	public void addChild(AssetNode a) {
		children.add(a);
	}
	
	public String getRepId() {
		return repId;
	}
	public void setRepId(String repId) {
		this.repId = repId;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getReposSrc() {
		return reposSrc;
	}

	public void setReposSrc(String reposSrc) {
		this.reposSrc = reposSrc;
	}

	
}
