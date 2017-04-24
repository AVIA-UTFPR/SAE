package main;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JFrame.*;

//servidor de eco
//recebe uma linha e ecoa a linha recebida.

import java.io.*;
import java.net.*;




public class Simulator extends JFrame{

	private int width = 800;
	private int height = 800;
	
	private Simulator() throws HeadlessException {
		setTitle("Autonomous Car Simulator");
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	public static void main(String args[]){
		
		Simulator sim = new Simulator();
	
	    //System.out.println("Servidor carregado no IP 127.0.0.1 e na porta 9999");
		
		try {

			DatagramSocket server = new DatagramSocket(9999);
			byte[] receive = new byte[1024];
			byte[] send = new byte[1024];
			
			while(true)
			{
				DatagramPacket receivePacket = new DatagramPacket(receive, receive.length); // Pacote Recebido
				server.receive(receivePacket);
				
				// Exibir pacote recebido
				String sentence = new String( receivePacket.getData() );
				System.out.println("Received: " + sentence);
				
				// Pegar endereço do pacote
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				
				// Modificar String recebida
				String capitalizedSentence = sentence.toUpperCase();
				send = capitalizedSentence.getBytes();
				
				// Enviar resposta
				DatagramPacket sendPacket = new DatagramPacket(send, send.length, IPAddress, port);
				server.send(sendPacket);
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	
	     
	 }
}

class DrawArea extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Point A = null;
	public DrawArea() {
		A = new Point(100,200);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawRect(A.x, A.y, 50, 50);
	}
	
}



