package single;

import java.util.logging.Level;
import java.util.stream.Collectors;

import pr.common.MakeSound;
import pr.log.LogFiles;

public class AlarmActivities {
	boolean isPlay;
		
	public void play(String filePath) {
		isPlay = true;
		PlaySound ps = new PlaySound(filePath);
		ps.setName("AlarmActivities_play_Thread");
		ps.start();
	}
	
	public void clearActivities() {
		ProgramProperty.hightPriorityAlarmProperty.set(null);
		isPlay = false;
		while (Thread.getAllStackTraces().keySet().stream()
				.filter(f -> "AlarmActivities_play_Thread".equals(f.getName()))
				.collect(Collectors.toList()).size() > 0) {
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LogFiles.log.log(Level.SEVERE, "void clearActivities()", e);
			}
		}
	}
	
	private class PlaySound extends Thread {
		private String filePath;
		
		public PlaySound(String filePath) {
			this.filePath = filePath;
		}
		
		@Override
		public void run() {
			MakeSound ms = new MakeSound();
			
			try {
				while (isPlay) {
					
					ms.playSound(filePath);
					
					Thread.sleep(500);
				}
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, "void run()", e);
				isPlay = false;
			}
		}	
	}
}
