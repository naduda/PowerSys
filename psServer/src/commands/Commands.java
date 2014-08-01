package commands;

import javafx.concurrent.Task;
import topic.ReceiveProtocolTopic;

public class Commands {
	private boolean isRun = true;
	public ReceiveProtocolTopic protocolTopic;
	
	public Commands() {
		protocolTopic = new ReceiveProtocolTopic();
		final Task<Void> taskCommands = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				protocolTopic.run();
				return null;
			}       	
        };
        new Thread(taskCommands, "taskCommands").start();
	}
	
	public void run() {
		while (isRun) {
			if (protocolTopic.getCommand().length() > 0) {
				getResult(protocolTopic.getCommand());
			}
		}
	}
	
	public void getResult(String command) {
		System.out.println(command.toUpperCase());
		switch (command.toLowerCase()) {
		case "getsignals" :
			System.out.println("12345");
			break;
		}
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

}
