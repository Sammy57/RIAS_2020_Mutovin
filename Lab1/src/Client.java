import java.net.*;
import java.io.IOException;
import java.util.Scanner;



public class Client{

    public static void main(String args[]){

        try{

            DatagramSocket dsock = new DatagramSocket();
            byte[] arr = new byte[1000]; //это будет сообщение держать

            DatagramPacket dpack_send ,dpack_recv ;

            Scanner inp = new Scanner(System.in);
            String myName = new String();
            System.out.print("Enter name : ");
            myName = (inp.nextLine() + " : "); // здесь задается имя юзера
            while(true){

                clearBytes(arr);            // Очистить переменную с сообщением 
                arr = (myName + inp.nextLine()).getBytes(); //ввод сообщения с консоли (+ имя которое выбрал юзер)
                dpack_send =  new DatagramPacket( arr , arr.length ,  InetAddress.getByName("127.0.0.1") , 8080 );//делаем пакет
                dsock.send(dpack_send);//шлем на сервер

                clearBytes(arr);
                dpack_recv = new DatagramPacket( arr , arr.length );
                dsock.receive(dpack_recv);
                System.out.println(new String(arr));

            }

        }catch(IOException e){
            System.out.println("Error : " + e );
        }

    }

    public static void clearBytes(byte[] arr){ //этой функцией я arr чищу
        for( int i = 0 ; i < arr.length ; i ++ )
            arr[i] = '\0' ;
    }

}
