import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {

	private ServerSocket serverSocket = null;
	private boolean started = false;
	private List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) {
		new ChatServer().start();
	}

	private void start() {
		try {
			serverSocket = new ServerSocket(8888);
			started = true;
		} catch (SocketException e) {
			System.out
					.println("Server is already running,do not need to creat another!");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			while (started) {

				Socket socket = serverSocket.accept();
				Client client = new Client(socket);
				new Thread(client).start();
				clients.add(client);

			}
		} catch (EOFException e) {
			// System.out.println("client closed");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null)
					serverSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class Client implements Runnable {
		private Socket socket;
		private DataInputStream dataInputStream = null;
		private DataOutputStream dataOutputStream = null;
		private boolean bconnected = false;
		private String string = null;

		public Client(Socket socket) {
			this.socket = socket;
			try {
				dataInputStream = new DataInputStream(
						this.socket.getInputStream());

				dataOutputStream = new DataOutputStream(
						this.socket.getOutputStream());
				bconnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(String string) {
			try {
				dataOutputStream.writeUTF(string);
			} catch (SocketException e) {
				System.out.println("send()" + this.toString());
				clients.remove(this); // remove the closed client from the
				// list of clients!
				// e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		public void run() {
			Client client = null;
			try {
				while (bconnected) {

					string = "\n"
							+ new SimpleDateFormat("yyyy--MM--dd--HH--mm--ss’")
									.format(new Date()) + "\n"
							+ dataInputStream.readUTF() + "\n";
					// System.out.print(string);

					for (int i = 0; i < clients.size(); i++) {
						client = clients.get(i);
						client.send(string);
					}

				}

			} catch (SocketException e) {
				System.out.println(this.toString());

				System.out.println("a client quit!");
			} catch (EOFException e) {

				System.out.println("client closed!");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (socket == null)
						socket.close();
					if (dataInputStream == null)
						dataInputStream.close();
					if (dataOutputStream == null)
						dataOutputStream.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
