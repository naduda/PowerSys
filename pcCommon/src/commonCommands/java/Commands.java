package commonCommands.java;

import java.util.Map;

import topic.ReceiveProtocolTopic;
import topic.SendCommands;
import model.Tsignal;

public class Commands {

	private SendCommands sendCommands;
	private ReceiveProtocolTopic receiveProtocolTopic;
	
	public Commands(SendCommands sendCommands, ReceiveProtocolTopic receiveProtocolTopic) {
		this.sendCommands = sendCommands;
		this.receiveProtocolTopic = receiveProtocolTopic;
	}
	
	public Map<Integer, Tsignal> getSignals() {	
		sendCommands.setCommand("getSignals");
//		boolean isReceive = false;
		System.out.println(receiveProtocolTopic);
//		while (!isReceive) {
//			if (sendProtocolTopic.isSended()) {
//				System.out.println(receiveProtocolTopic.getValObject());
//			}
//		}
		return null;
	}
}
