package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class CLIClient {
	
	
	private void readInputAndSend(BufferedReader in, PrintWriter out, String exitStr){
		try{
			String line;
			while(!(line = in.readLine()).equals(exitStr)){
				out.println(line);
				out.flush();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public Thread aSyncReadInputAndSend(BufferedReader in, PrintWriter out, String exitStr){
		class CLIClientThread extends Thread{

			private BufferedReader in;
			private PrintWriter out;
			private String exitStr;

			public void setArgs(BufferedReader in, PrintWriter out, String exitStr){
				this.in = in;
				this.out = out;
				this.exitStr = exitStr;
			}

			@Override
			public void run(){
				readInputAndSend(in, out, exitStr);
			}
		}

		CLIClientThread cliClientThread = new CLIClientThread();
		cliClientThread.setArgs(in, out, exitStr);
		cliClientThread.start();
		
		return cliClientThread;
	}

	public void start(String ip, int port){
		try{
			Socket theServer = new Socket(ip, port);
			System.out.println("Connected to server.");
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(theServer.getInputStream()));
			
			PrintWriter outToServer = new PrintWriter(theServer.getOutputStream());
			PrintWriter outToScreen = new PrintWriter(System.out);
			
			Thread t1 = aSyncReadInputAndSend(userInput, outToServer, "exit");
			Thread t2 = aSyncReadInputAndSend(serverInput, outToScreen, "bye");	
			
			t1.join();
			t2.join();
			
			userInput.close();
			serverInput.close();
			outToServer.close();
			outToScreen.close();
			theServer.close();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			}
		catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

}
