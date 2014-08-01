package commonCommands.java;

import java.util.Map;

import topic.ReceiveProtocolTopic;
import topic.SendProtocolTopic;
import model.Tsignal;

public class Commands {

	private SendProtocolTopic sendProtocolTopic;
	private ReceiveProtocolTopic receiveProtocolTopic;
	
	public Commands(SendProtocolTopic sendProtocolTopic, ReceiveProtocolTopic receiveProtocolTopic) {
		this.sendProtocolTopic = sendProtocolTopic;
		this.receiveProtocolTopic = receiveProtocolTopic;
		System.out.println("*********************");
		System.out.println(sendProtocolTopic + "/" + receiveProtocolTopic);
	}
	
	public Map<Integer, Tsignal> getSignals() {	
		sendProtocolTopic.setCommandSend("getSignals");
		System.out.print("getSignals sended ");
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
