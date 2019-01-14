import groovy.lang.GroovyShell;
public class GroovyLauncher {
  /**
   * This method call can be used to launcher any groovy script in the
   * project provided a runnable jar has been created and there is access
   * to the project source.
   * Suitable batch files can be found in the folder launchFiles in the
   * project source files.
   * The project source folder, runnable jar and batch files just need
   * to be placed in the same folder for them to function correctly
   *
   * @param args
   */
  public static void main(String[] args) {
    //System.out.println("Running " + args[0]);
    //System.out.print("Running, " );
    //for ( int i = 0; i < args.length; i++) System.out.print(args[i] + ", ");
    //System.out.print(" -> ");
    GroovyShell.main(args);
  }

}
