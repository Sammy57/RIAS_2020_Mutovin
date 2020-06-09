import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Scanner;

public class EchoClient {

    public static void main(String[] args) {
        try {
            Scanner inp = new Scanner(System.in);
            String connCheck = new String();
            String message = new String();
            while (!connCheck.equals("connect localhost 1500")){  //этот цикл проверяет правильно ли пользователь ввел хоста и пароль
                System.out.print("EchoClient>");                  //вообще тут должны были вводится их переменные,
                connCheck = inp.nextLine();                       //которые затем вводятся в clientSocket для проверки связи, но я не успел
                if (connCheck.equals("connect localhost 1500")){
                    System.out.print("EchoClient> ");
                    System.out.println("Connection to EchoServer established");
                }
                else {
                    System.out.print("EchoClient> ");
                    System.out.println("Connect failed due to: wrong address or port");
                }
            }
            Socket clientSocket = new Socket("localhost",1500); //должен менятся динамически через connect-disconnect но я его жестко прописал чтобы успеть
            while (true){
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                Mail mail = (Mail) in.readObject();
                System.out.print("EchoClient> ");
                System.out.println(mail.getMail()); //здесь клиент выводит ответ от сервера

                message = null; //почистить перед вводом
                System.out.print("EchoClient> ");
                message = inp.nextLine();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                mail = new Mail(message, Calendar.getInstance().getTime());
                out.writeObject(mail);  //здесь отсылается следующая команда
            }
        }catch (Exception e){e.printStackTrace();}
    }


}
