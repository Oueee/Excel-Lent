package core;

import javax.swing.JProgressBar;

import util.Log;
import gui.ProgressBarListener;

public class MainConsole{
  public static void main(String[] args) {
    if(args.length < 3) {
      Log.e("\narguments to give: 0|1 0|1 0|1\n1 to do it, 0 to do not.\nviruses eukaryotes prokaryotes");
      Log.e("Enjoy ;)");
      Log.exit();
    }

    try{
      final JProgressBar progressBar = new JProgressBar(0, 100);
      final ExcelLent excellent = new ExcelLent(new ProgressBarListener(progressBar));

      Log.i("#########################");
      Log.i("/!\\ Stop with a ^C... /!\\");
      Log.i("#########################");

      excellent.setToDo(args[0].equals("1") ? true : false,
                        args[1].equals("1") ? true : false,
                        args[2].equals("1") ? true : false);

      new Thread(excellent).start();

      while (true)
        Thread.sleep(100000);

    } catch(Exception e) {
      Log.e(e);
      Log.exit();
    }
  }
}
