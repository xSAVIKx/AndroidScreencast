package com.github.xsavikx.android.screencast.api.injector;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

public class Injector {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(Injector.class);
  private static final int PORT = 2436;
  private static final String LOCALHOST = "127.0.0.1";
  private static final String LOCAL_AGENT_JAR_LOCATION = "./MyInjectEventApp.jar";

  private static final String REMOTE_AGENT_JAR_LOCATION = "/data/local/tmp/MyInjectEventApp.jar";
  private static final String AGENT_MAIN_CLASS = "com.github.xsavikx.android.screencast.client.Main";
  IDevice device;

  public static Socket s;

  OutputStream os;

  Thread agentInitThread;

  public ScreenCaptureThread screencapture;

  /**
   * @return true if there was a client running
   */
  private static boolean killRunningAgent() {
    if (logger.isDebugEnabled()) {
      logger.debug("killRunningAgent() - start");
    }

    try {
      Socket s = new Socket(LOCALHOST, PORT);
      OutputStream os = s.getOutputStream();
      os.write("quit\n".getBytes());
      os.flush();
      os.close();
      s.close();

      if (logger.isDebugEnabled()) {
        logger.debug("killRunningAgent() - end");
      }
      return true;
    } catch (Exception ex) {
      logger.warn("killRunningAgent() - exception ignored", ex);

      // ignor
    }

    if (logger.isDebugEnabled()) {
      logger.debug("killRunningAgent() - end");
    }
    return false;
  }

  public Injector(IDevice d) throws IOException {
    this.device = d;
    this.agentInitThread = new AgentInitThread(device, s, os);
    this.screencapture = new ScreenCaptureThread(device);
  }

  public void close() {
    if (logger.isDebugEnabled()) {
      logger.debug("close() - start");
    }

    try {
      if (os != null) {
        os.write("quit\n".getBytes());
        os.flush();
        os.close();
      }
      s.close();
    } catch (Exception ex) {
      logger.warn("close() - exception ignored", ex);

      // ignored
    }
    screencapture.interrupt();
    try {
      s.close();
    } catch (Exception ex) {
      logger.warn("close() - exception ignored", ex);

      // ignored
    }
    try {
      synchronized (device) {
        /*
         * if(device != null) device.removeForward(PORT, PORT);
         */
      }
    } catch (Exception ex) {
      logger.warn("close() - exception ignored", ex);

      // ignored
    }

    if (logger.isDebugEnabled()) {
      logger.debug("close() - end");
    }
  }

  private void injectData(String data) {
    if (logger.isDebugEnabled()) {
      logger.debug("injectData(String) - start");
    }

    if (logger.isInfoEnabled()) {
      logger.info("injectData(String) - String data=" + data);
    }

    try {
      if (os == null) {
        if (logger.isInfoEnabled()) {
          logger.info("injectData(String) - Injector is not running yet");
        }
        if (logger.isDebugEnabled()) {
          logger.debug("injectData(String) - end");
        }
        return;
      }
      os.write((data + "\n").getBytes());
      os.flush();
    } catch (Exception sex) {
      try {
        s = new Socket(LOCALHOST, PORT);
        os = s.getOutputStream();
        os.write((data + "\n").getBytes());
        os.flush();
      } catch (Exception ex) {
        logger.warn("injectData(String) - exception ignored", ex);

        // ignored
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("injectData(String) - end");
    }
  }

  public void injectKeycode(int keyCode) {
    if (logger.isDebugEnabled()) {
      logger.debug("injectKeycode( int) - start");
    }

    String cmdList = "key/" + keyCode;
    injectData(cmdList);

    if (logger.isDebugEnabled()) {
      logger.debug("injectKeycode(int, int) - end");
    }
  }

  public void injectSwipe(int fromX, int fromY, int toX, int toY) {
    if (logger.isDebugEnabled()) {
      logger.debug("injectSwipe(int, int, int, int) - start");
    }
    String cmdList = String.format("swipe/%d/%d/%d/%d", fromX, fromY, toX, toY);
    injectData(cmdList);
    if (logger.isDebugEnabled()) {
      logger.debug("injectSwipe(int, int, int, int) - end");
    }
  }

  public void injectTap(int x, int y) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("injectTap(int, int) - start");
    }
    String cmdList1 = String.format("tap/%d/%d", x, y);

    injectData(cmdList1);

    if (logger.isDebugEnabled()) {
      logger.debug("injectTap(int, int) - end");
    }
  }

  public void injectTrackball(float amount) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("injectTrackball(float) - start");
    }

    long downTime = 0;
    long eventTime = 0;
    float x = 0;
    float y = amount;
    int metaState = -1;

    String cmdList1 = String.format("trackball/{0}/{1}/{2}/{3}/{4}/{5}", downTime, eventTime,
        ConstEvtMotion.ACTION_MOVE, x, y, metaState);
    injectData(cmdList1);
    String cmdList2 = String.format("trackball/{0}/{1}/{2}/{3}/{4}/{5}", downTime, eventTime,
        ConstEvtMotion.ACTION_CANCEL, x, y, metaState);
    injectData(cmdList2);

    if (logger.isDebugEnabled()) {
      logger.debug("injectTrackball(float) - end");
    }
  }

  public void start() {
    if (logger.isDebugEnabled()) {
      logger.debug("start() - start");
    }

    agentInitThread.start();

    if (logger.isDebugEnabled()) {
      logger.debug("start() - end");
    }
  }

  class AgentInitThread extends Thread {
    private final IDevice device;

    public AgentInitThread(IDevice device, Socket s, OutputStream os) {
      super("Agent Init");
      this.device = device;
    }

    private void connectToAgent() {
      if (logger.isDebugEnabled()) {
        logger.debug("connectToAgent() - start");
      }

      for (int i = 0; i < 10; i++) {
        try {
          s = new Socket(LOCALHOST, PORT);
          break;
        } catch (Exception s) {
          logger.error("connectToAgent()", s);

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            logger.error("connectToAgent()", e);

            if (logger.isDebugEnabled()) {
              logger.debug("connectToAgent() - end");
            }
            return;
          }
        }
      }
      if (logger.isInfoEnabled()) {
        logger.info("connectToAgent() - Connected to agent socket");
      }
      screencapture.start();
      try {
        os = s.getOutputStream();
      } catch (IOException e) {
        logger.error("connectToAgent()", e);

        throw new RuntimeException(e);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("connectToAgent() - end");
      }
    }

    private void init() throws UnknownHostException, IOException, InterruptedException, TimeoutException,
        AdbCommandRejectedException {
      if (logger.isDebugEnabled()) {
        logger.debug("init() - start");
      }

      device.createForward(PORT, PORT);

      if (killRunningAgent())
        if (logger.isInfoEnabled()) {
          logger.info("init() - Old client closed");
        }

      uploadAgent();

      Thread threadRunningAgent = new AgentRunningThread(device);
      threadRunningAgent.start();
      Thread.sleep(10000);
      connectToAgent();
      if (logger.isInfoEnabled()) {
        logger.info("init() - Connected to agent!");
      }

      if (logger.isDebugEnabled()) {
        logger.debug("init() - end");
      }
    }

    @Override
    public void run() {
      if (logger.isDebugEnabled()) {
        logger.debug("run() - start");
      }
      try {
        init();
      } catch (Exception e) {
        logger.error("run()", e);

        throw new RuntimeException(e);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("run() - end");
      }

    }

    private void uploadAgent() throws IOException {
      if (logger.isDebugEnabled()) {
        logger.debug("uploadAgent() - start");
      }

      try {
        // File tempFile = File.createTempFile("agent", ".jar");
        String cmd = String.format("adb -s %s push %s %s", device.getSerialNumber(), LOCAL_AGENT_JAR_LOCATION + " ",
            REMOTE_AGENT_JAR_LOCATION);
        if (logger.isInfoEnabled()) {
          logger.info("uploadAgent() - String cmd=" + cmd);
        }

        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        int exitValue = p.exitValue();
        if (logger.isInfoEnabled()) {
          logger.info("uploadAgent() - ADB push exitValue=" + exitValue);
        }
      } catch (InterruptedException ex) {
        logger.error("uploadAgent()", ex);

        throw new RuntimeException(ex);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("uploadAgent() - end");
      }
    }
  }

  class AgentRunningThread extends Thread {
    private final IDevice device;
    private final static String APP_PROCESS_COMMAND = "su -c 'export CLASSPATH=%s; exec app_process /system/bin %s %s'";
    private final static String DALVIK_VM_COMMAND = "su -c '/system/bin/dalvikvm -classpath %s %s %s'";

    public AgentRunningThread(IDevice device) {
      super("Agent runner");
      this.device = device;
    }

    private void launchProg(String cmdList) throws IOException, TimeoutException, AdbCommandRejectedException,
        ShellCommandUnresponsiveException {
      if (logger.isDebugEnabled()) {
        logger.debug("launchProg(String) - start");
      }
      String fullCmd = String.format(APP_PROCESS_COMMAND, REMOTE_AGENT_JAR_LOCATION, AGENT_MAIN_CLASS, cmdList);
      if (logger.isInfoEnabled()) {
        logger.info("launchProg(String) - String fullCmd=" + fullCmd);
      }
      try {
        device.executeShellCommand(fullCmd, new MultiLineReceiverPrinter());
      } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException e) {
        logger.error("launch with app_process failed, try with dalvikvm ...");
        fullCmd = String.format(DALVIK_VM_COMMAND, REMOTE_AGENT_JAR_LOCATION, AGENT_MAIN_CLASS, cmdList);
        device.executeShellCommand(fullCmd, new MultiLineReceiverPrinter());
      }

      if (logger.isDebugEnabled()) {
        logger.debug("launchProg(String) - end");
      }
    }

    @Override
    public void run() {
      if (logger.isDebugEnabled()) {
        logger.debug("run() - start");
      }

      try {
        launchProg("" + PORT);
      } catch (IOException | TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException e) {
        logger.error("run()", e);

        e.printStackTrace();
      }

      if (logger.isDebugEnabled()) {
        logger.debug("run() - end");
      }
    }
  }
}
