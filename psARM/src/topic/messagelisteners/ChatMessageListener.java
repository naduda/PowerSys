package topic.messagelisteners;

import java.text.SimpleDateFormat;

import pr.model.ChatMessage;
import single.ProgramProperty;
import single.SingleObject;

public class ChatMessageListener extends AbstarctMessageListener {
	private final SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	@Override
	void runLogic(Object obj) {
		if (obj instanceof ChatMessage) {
			ChatMessage message = (ChatMessage) obj;
			if (!SingleObject.chat.isShowing()) {
				SingleObject.chat.show();
			}
			
			ProgramProperty.chatMessageProperty.set(message.getAddress() + ": " + 
					dFormat.format(message.getDt()) + "\n" + message.getTextValue());
		}
	}

}
