import java.net.*;
import java.io.IOException;
import java.util.Scanner;



public class Client{

    public static void main(String args[]){

        try{

            DatagramSocket dsock = new DatagramSocket();
            byte[] arr = new byte[1000]; //это будет сообщение держать

            DatagramPacket dpack_send ,dpack_recv ;

            Scanner inp = new Scanner(System.in); //Создание объекта in класса Scanner.
            String myName = new String();
            System.out.print("Enter name : ");
            myName = (inp.nextLine() + " : "); // здесь задается имя юзера
            while(true){ //если верно, то выполняю след:

                clearBytes(arr);           // Очисттить переменную arr с сообщением
                arr = (myName + inp.nextLine()).getBytes(); // имя+ ввод сообщения с консоли 
                dpack_send =  new DatagramPacket( arr , arr.length ,  InetAddress.getByName("127.0.0.1") , 8080 ); //создаем пакет
                dsock.send(dpack_send);//отправляем на сервер

                clearBytes(arr);  // Очисттить переменную arr с сообщением
                dpack_recv = new DatagramPacket( arr , arr.length ); //Создает ячейку для содержания Датаграмных пакетов (сообщений)
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
