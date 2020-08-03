package testing;

import java.io.IOException;

/**
 *
 * @author Bosco
 */
public class EjecutarUnExe {

  public static void main(String args[]) {
    Runtime r = Runtime.getRuntime();
    Process p = null;
    // Windows
    //String cmd[] = { "notepad", "C:/Java programs/CharDemo.java" };
    
    // Linux
    String cmd[] = { "mysqldump", "-u root -pbendicion sai > /home/bosco/dumps/Dumptest2.sql" };
    try {
        p = r.exec(cmd);
        p.waitFor();
    } catch (IOException | InterruptedException e) {
        System.out.println("error executing " + cmd[0]);
    }
  System.out.println(cmd[0] + " returned " + p.exitValue());
  }
}