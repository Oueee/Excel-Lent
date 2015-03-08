package gui;

import javax.swing.JProgressBar;

public class ProgressBarListener {
	
	private JProgressBar progressBar;
	
	public ProgressBarListener(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}
}