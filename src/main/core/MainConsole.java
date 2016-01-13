package core;

import javax.swing.JProgressBar;

import util.Log;
import gui.ProgressBarListener;
import javax.swing.JLabel;

public class MainConsole{
  public static void main(String[] args) {
    boolean[] finalArgs = {true, true, true, true};

    if(args.length >= 4) {
      for (int i = 0; i < 4; i++)
        finalArgs[i] = args[i].equals("1") ? true : false;
    }

    try{
      final JProgressBar progressBar = new JProgressBar(0, 100);
      final JLabel text = new JLabel ();
      final ExcelLent excellent = new ExcelLent(new ProgressBarListener(progressBar, text));

      Log.i("#########################");
      Log.i("/!\\ Stop with a ^C... /!\\");
      Log.i("#########################");

      excellent.setToDo(finalArgs[0],
                        finalArgs[1],
                        finalArgs[2],
                        finalArgs[3]);

      new Thread(excellent).start();

      while (true)
        Thread.sleep(100000);

    } catch(Exception e) {
      Log.e(e);
      Log.exit();
    }
  }
}
