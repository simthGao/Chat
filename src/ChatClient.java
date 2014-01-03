import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient extends Frame {
	private Socket socket = null;
	private TextField textField = new TextField();
	private TextArea textArea = new TextArea();
	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;
	private boolean bconnected = false;

	public static void main(String[] args) {
		new ChatClient().lanchFram();

	}

	public void lanchFram() {
		this.setTitle("ChatClient");
		this.setLocation(300, 300);
		this.setSize(300, 300);
		this.add(this.textField, BorderLayout.SOUTH);
		this.add(this.textArea, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				try {
					dataOutputStream.close();
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

		});

		textField.addActionListener(new TextListenner());
		this.setVisible(true);
		this.connect();
	}

	public void connect() {
		try {
			socket = new Socket("127.0.0.1", 8888);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			bconnected = true;
			System.out.print("connected!");
			new Thread(new ChatServer()).start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class TextListenner implements ActionListener {

		String string1 = "";

		public void actionPerformed(ActionEvent e) {

			String string = textField.getText().trim();
			string1 += "\n"
					+ new SimpleDateFormat("yyyy--MM--dd--HH--mm--ss’")
							.format(new Date()) + "\n" + string + "\n";
			// textArea.setText(string1);
			// textArea.append(string1);
			textField.setText("");

			try {

				dataOutputStream.writeUTF(string);
				dataOutputStream.flush();

			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

	}

	class ChatServer implements Runnable {

		public void run() {
			try {
				while (true) {

					String string = dataInputStream.readUTF();
					// textArea.setText(string);
					textArea.append(string);

				}
			} catch (SocketException e) {
				System.out.println("quit!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
