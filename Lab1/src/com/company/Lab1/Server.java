package com.company.Lab1;

import java.net.*;
import java.io.IOException;
import java.util.Scanner;
public class Server{
    public static void main(String args[]){
        try{
            DatagramSocket dsock = new DatagramSocket( 8080 , InetAddress.getByName("127.0.0.1") );
            byte[] arr = new byte[1000];
            DatagramPacket dpack_send , dpack_recv ;
            Scanner inp = new Scanner(System.in);
         
            while(true){
                clearBytes(arr);//почистить перед использованием
                dpack_recv = new DatagramPacket( arr , arr.length ); //пришел пакет
                dsock.receive(dpack_recv);//ловим
                System.out.println(new String(arr));//вывод сообщ в чат
                dpack_send =  new DatagramPacket( arr , arr.length , dpack_recv.getAddress() , dpack_recv.getPort() );
                dsock.send(dpack_send);//для дебага
            }
        }catch(IOException e){
            System.out.println(e.getStackTrace());
        }
    }
    public static void clearBytes(byte[] arr){// чистить arr
        for( int i = 0 ; i < arr.length ; i ++ )
            arr[i] = '\0' ;
    }
}
