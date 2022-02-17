package com.client;

public class Runner
{

	
	public static void main(String[] args)
	{
		Client client = new Client();
		Thread t = new Thread(client);
		t.start();
		
	}


}
