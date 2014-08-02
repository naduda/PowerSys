package commands;

import topic.ReceiveProtocolTopic;
import topic.SendCommandResult;
import topic.SendTopic;

public class CommandResult implements Runnable {
	private boolean isRun = true;
	private ReceiveProtocolTopic protocolTopic = new ReceiveProtocolTopic();
	private SendCommandResult sendCommandResult;
	
	public CommandResult(SendTopic sTopic) {
        new Thread(protocolTopic, "taskCommands").start();
        sendCommandResult = new SendCommandResult(sTopic.factory, sTopic.jConn, "ProtocolTopic");
	}
	
	@Override
	public void run() {
		new Thread(sendCommandResult, "sendCommandResult_Thread").start();
		while (isRun) {
			if (protocolTopic.getCommand() != null) {
				sendCommandResult.setResult("RESULT_TO_" + protocolTopic.getCommand().toUpperCase());
				getResult(protocolTopic.getCommand());
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getResult(String command) {
		System.out.print(command.toUpperCase() + "   -->   ");
		switch (command.toLowerCase()) {
		case "getsignals" :
			System.out.println("12345");
			break;
		}
		protocolTopic.setCommand(null);
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

}
