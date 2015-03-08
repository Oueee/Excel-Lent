package connection;

import gui.ProgressBarListener;

import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingWorker;

/**
 * Manages the species
 */
public class SpeciesManager extends SwingWorker {

	ThreadPoolExecutor es;
	ProgressBarListener listener;

	public SpeciesManager(ThreadPoolExecutor es, ProgressBarListener listener) {
		this.es = es;
		this.listener = listener;
	}

	@Override
	protected Object doInBackground() throws Exception {
		// TODO just for testing
		Thread.sleep(1000);
		return null;
	}
	
	// set the progress bar value to the percentage of completed tasks
	@Override
	protected void done() {
		double completed = (double) (es.getCompletedTaskCount()) / (double) es.getTaskCount();
		int progress = (int) Math.ceil(100 * completed);
		listener.setProgress(progress);
	}

}
