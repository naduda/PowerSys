package pr.iec104;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ASDU implements Serializable {
	private static final long serialVersionUID = 1L;
	private IdentBlock identBlock;
	private List<InfoObject> infoObjects = new ArrayList<>();
	
	public ASDU() {
		super();
	}
	
	public ASDU(byte[] bytes) {
		this();
		setIdentBlock(new IdentBlock(bytes));
		if (identBlock.isBlockKeyValue()) {
			int infoBlockSize = (bytes.length - 6) / identBlock.getValuesSize();
			System.out.println("infoBlockSize = " + infoBlockSize);
			
			for (int i = 0; i < identBlock.getValuesSize(); i++) {
				byte[] infoBlockArray = new byte[infoBlockSize];
				for (int j = 0; j < infoBlockSize; j++) {
					infoBlockArray[j] = bytes[infoBlockSize * i + 6 + j];
				}
				infoObjects.add(new InfoObject(infoBlockArray, EType.getForInt(0xFF & identBlock.getIdentType())));
			}
			infoObjects.forEach(o -> {
				CP56Time2a tt = new CP56Time2a(o.getTimeArray());
				System.out.println(tt.getDate() + " - ioa = " + o.getAddress() + 
						" [" + Integer.toHexString(o.getAddress()) + "]; value = " + o.getValue());
			});
		}
		System.out.println(getIdentBlock());
	}

	public IdentBlock getIdentBlock() {
		return identBlock;
	}

	public void setIdentBlock(IdentBlock identBlock) {
		this.identBlock = identBlock;
	}

	public List<InfoObject> getInfoObjects() {
		return infoObjects;
	}

	public void setInfoObjects(List<InfoObject> infoObjects) {
		this.infoObjects = infoObjects;
	}
}
