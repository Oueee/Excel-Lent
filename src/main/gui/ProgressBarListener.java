package gui;

import javax.swing.JProgressBar;
import javax.swing.JLabel;

public class ProgressBarListener {
	
	private JProgressBar progressBar;
	private JLabel text;
	
	public ProgressBarListener(JProgressBar progressBar, JLabel text) {
		this.progressBar = progressBar;
		this.text = text;
	}
	
	public void setProgress(long CompletedTaskCount, long TaskCount) {
		double completed = ((double)CompletedTaskCount)/((double)TaskCount);
		int progress = (int) (100 * completed);
		progressBar.setValue(progress);
		text.setText("Species done: " + CompletedTaskCount + "/" + TaskCount);
	}
	
	public void setText(String msg) {
	  progressBar.setValue(0);
	  text.setText(msg);
	}
}
