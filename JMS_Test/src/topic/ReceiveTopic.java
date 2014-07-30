package topic;

import javafx.application.Platform;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import model.Alarm;
import model.DvalTI;
import model.DvalTS;
import ui.Main;
import ui.MainStage;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.jms.Session;
import com.sun.messaging.jms.TopicConnection;

public class ReceiveTopic implements MessageListener {

	private TopicSession session = null;
	private TopicConnection connection = null;
	private ConnectionFactory factory;
	private boolean isRun = true;
	private String ip = "";
	
	public void setRun(boolean r) {
		isRun = r;
	}
	
	public ReceiveTopic() {
		run();		
	}
	
	public ReceiveTopic(String ip) {
		this.ip = ip;
		run();
	}
	
	public void run() {
		try {
			factory = new com.sun.messaging.ConnectionFactory();
			String prop = ip.length() == 0 ? "mq://127.0.0.1:7676,mq://127.0.0.1:7676" : "mq://" + ip + ":7676,mq://" + ip + ":7676";
			factory.setProperty(ConnectionConfiguration.imqAddressList, prop);
			connection = (TopicConnection) factory.createTopicConnection("admin","admin");
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			
			Topic tDvalTI = session.createTopic("DvalTI");
			TopicSubscriber subscriberDvalTI = session.createSubscriber(tDvalTI);						
			subscriberDvalTI.setMessageListener(this);
			
			Topic tDvalTS = session.createTopic("DvalTS");
			TopicSubscriber subscriberDvalTS = session.createSubscriber(tDvalTS);						
			subscriberDvalTS.setMessageListener(this);
			
			Topic tAlarms = session.createTopic("Alarms");
			TopicSubscriber subscribertAlarms = session.createSubscriber(tAlarms);						
			subscribertAlarms.setMessageListener(this);
			
			int k = 0;
			while (isRun) {
				Thread.sleep(60000);
				System.out.println((++k) + " min");
			}
		} catch (Exception e) {
			System.err.println("ReceiveTopic ");
			e.printStackTrace();
		} finally {
			try {
				session.close();
		        connection.close();
		    } catch (Exception e) {
		    	System.out.println("Can't close JMS connection/session " + e.getMessage());
		    }
		}
	}	
	
	@Override
	public void onMessage(Message msg) {
		try {
			if (msg instanceof ObjectMessage) {				
				Object obj = ((ObjectMessage)msg).getObject();
		    	if (obj.getClass().getName().toLowerCase().endsWith("dvalti")) {
		    		if (Main.mainScheme != null) {
		    			new Thread(new Runnable() {
		    	            @Override public void run() {
		    	                Platform.runLater(new Runnable() {
		    	                    @Override public void run() {	    			
		    			    			MainStage.controller.updateTI((DvalTI) obj);
		    	                    }
		    	                });
		    	            }
		    	        }, "Update TI").start();		    					    			
		    		}
		    	} else if (obj.getClass().getName().toLowerCase().endsWith("dvalts")) {
		    		if (Main.mainScheme != null) {
		    			new Thread(new Runnable() {
		    	            @Override public void run() {
		    	                Platform.runLater(new Runnable() {
		    	                    @Override public void run() {
		    	                    	MainStage.controller.updateTS((DvalTS) obj);
		    	                    }
		    	                });
		    	            }
		    	        }, "Update TS").start();
		    			
			    	}
		    	} else if (obj.getClass().getName().toLowerCase().endsWith("alarm")) {
		    		if (Main.mainScheme != null) {
		    			new Thread(new Runnable() {
		    	            @Override public void run() {
		    	                Platform.runLater(new Runnable() {
		    	                    @Override public void run() {
		    	                    	Alarm a = (Alarm) obj;
		    	                    	if (a.getConfirmdt() == null) {
		    	                    		MainStage.controller.getAlarmsController().addAlarm(a);
		    	                    	} else {
		    	                    		MainStage.controller.getAlarmsController().updateAlarm(a);
		    	                    	}
		    	                    }
		    	                });
		    	            }
		    	        }, "Update Alarms").start();
		    			
			    	}
		    	}
		        
		     }
		 } catch (Exception e){
		      System.out.println("Error while consuming a message: " + e.getMessage());
		      e.printStackTrace();
		 }
	}
	
}
