package com.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.gson.Gson;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;

public class Client implements Runnable 
{
	private JFrame frame;
	private JPanel panel;
	private JButton send;
	private SpringLayout layout;
	private JTextField messageField, usernameField;
	private JTextArea chatArea;
	private Socket connectionToTheServer;
	private OutputStream out;
	private PrintStream ps;
	private InputStream in;
	private BufferedReader br;
	private JButton connect;
	private Gson gson;
	private Map<String,String> sendMsgMap,receiveMsgMap;
	private JLabel usernameLabel;
	
	
	public Client()
	{
		gson = new Gson();
		sendMsgMap = new HashMap<>();
		
		setupLabels();
		setupButtons();
		setupFields();
		setupPanel();
		setupFrame();
		
		
	}
	private void setupLabels()
	{
		usernameLabel = new JLabel("Username:");
		
	}
	private void setupFrame()
	{
		frame = new JFrame();
		frame.setContentPane(panel);
		frame.setSize(500,500);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				try
				{
					connectionToTheServer.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					
				}
				finally
				{
					System.exit(0);
				}
				
			}

		});
	}
	private void setupPanel()
	{
		layout = new SpringLayout();
		panel = new JPanel();
		
		
		
		panel.add(send);
		panel.add(connect);
		panel.setLayout(layout);
		panel.add(messageField);
		panel.add(usernameField);
		panel.add(usernameLabel);
		panel.add(chatArea);
		
		layout.putConstraint(SpringLayout.EAST, send, -25, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, send, 0, SpringLayout.NORTH, messageField);
			
		layout.putConstraint(SpringLayout.NORTH, messageField, 50, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, messageField, 25, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, messageField, -10, SpringLayout.WEST, send);
		layout.putConstraint(SpringLayout.SOUTH, messageField, -400, SpringLayout.SOUTH, panel);
		
		
		layout.putConstraint(SpringLayout.NORTH, chatArea, 25, SpringLayout.SOUTH, messageField);
		layout.putConstraint(SpringLayout.WEST, chatArea, 0, SpringLayout.WEST, messageField);
		layout.putConstraint(SpringLayout.EAST, chatArea, 0, SpringLayout.EAST, send);
		layout.putConstraint(SpringLayout.SOUTH, chatArea, -25, SpringLayout.SOUTH, panel);
		
		
		layout.putConstraint(SpringLayout.NORTH, connect, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, connect, 0, SpringLayout.WEST, messageField);
		
		
		layout.putConstraint(SpringLayout.NORTH, usernameField, 0, SpringLayout.NORTH, connect);
		layout.putConstraint(SpringLayout.EAST, usernameField, 0, SpringLayout.EAST, send);
		
		layout.putConstraint(SpringLayout.NORTH, usernameLabel, 0, SpringLayout.NORTH, usernameField);
		layout.putConstraint(SpringLayout.SOUTH, usernameLabel, 0, SpringLayout.SOUTH, usernameField);
		layout.putConstraint(SpringLayout.EAST, usernameLabel, -5, SpringLayout.WEST, usernameField);
	
		
	}
	
	private void setupButtons()
	{
		send = new JButton("Send");
		connect = new JButton("Connect");
		
		send.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					sendMsgMap.put("message", messageField.getText());
					
					sendMessage(gson.toJson(sendMsgMap));
					messageField.setText("");
						
				}
			
			});
		
		connect.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				connect();
					
			}
		
		});
	}
	
	private void setupFields()
	{
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		
		messageField = new JTextField();
		messageField.setColumns(10);
		
		usernameField = new JTextField();
		usernameField.setColumns(10);
		usernameField.setText("User");
		sendMsgMap.put("username", usernameField.getText());
		usernameField.getDocument().addDocumentListener(new DocumentListener()
			{

				@Override
				public void insertUpdate(DocumentEvent e)
				{
					sendMsgMap.put("username", usernameField.getText());
					
				}

				@Override
				public void removeUpdate(DocumentEvent e)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void changedUpdate(DocumentEvent e)
				{
					
					
				}
			});
	}
	
	private void sendMessage(String message)
	{
		ps.println(message);
	}
	
	private void connect()
	{
		try
		{
			connectionToTheServer = new Socket("127.0.0.1", 3306);
			out = connectionToTheServer.getOutputStream();
			ps = new PrintStream(out, true);
			in = connectionToTheServer.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
		}
		catch (Exception e)
		{
			System.out.println("Error connecting to Server");
		}
	}
	
	private void receiveInfo()
	{
		
		try
		{
			in = connectionToTheServer.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			String message = br.readLine();
			receiveMsgMap = gson.fromJson(message, HashMap.class);
			chatArea.append(receiveMsgMap.get("username") + ": ");
			chatArea.append(receiveMsgMap.get("message") + "\n");
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run()
	{
		for (int x = 0; x < 100; x++)
		{
			try
			{
				Thread.sleep(50);
				receiveInfo();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
			}
			if (x == 99)
			{
				x = 0;
			}

		}
		
	}
}
