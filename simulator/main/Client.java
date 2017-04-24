package main;

import java.io.*;
import java.net.*;

public class Client {


    public static void main(String[] args) {

    	try {
    		// Pegar informação
    		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			String sentence = inFromUser.readLine();
    		
			DatagramSocket client = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			
			// Transformar String em Bytes
			sendData = sentence.getBytes();
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
			client.send(sendPacket);
			
			// Receber informação do servidor
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			client.receive(receivePacket);
			
			// Exibir resposta do servidor
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
			
			client.close();
    	}
    	catch (Exception e) {
			System.out.println(e);
    	}
        
    }
}
