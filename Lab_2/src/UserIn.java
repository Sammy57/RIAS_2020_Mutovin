import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Scanner;

public class UserIn extends Thread{

    ServerSocket serverSocket = null;
    public UserIn(){ //Поскольку для тестирования работы EchoClient нужен сервер, этот класс сымитирует его работу
        try {        //Позже (lab3) этот класс будет заменен на EchoServer
            serverSocket = new ServerSocket(1500);
            System.out.println("Simulation start");
            start();
        }catch (Exception e){e.printStackTrace();}
    }
    public void run(){
        try {
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected from: " + clientSocket.getInetAddress().getHostAddress()); //пользователь подключается к серверу

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                Mail mail = new Mail("Connect to EchoServer established from : " + clientSocket.getInetAddress().getHostAddress(), Calendar.getInstance().getTime());
                out.writeObject(mail); //Сообщить пользователю об успешном подключении

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                mail = (Mail) in.readObject();   //здесь читается команда пользователя
                String[] choise = mail.getMail().split(" "); //split(" ") поможет выделить первое слово,
                switch (choise[0]){                          //в нем команда, что позволит выбрать соответсвующтий отклик
                    case ("help"):      //это выведет пользователю список возможных комманд
                        mail = new Mail("send <text> \n quit \n log \n help", Calendar.getInstance().getTime());
                        out.writeObject(mail);
                        break;
                    case ("send"):      //это эхом отразит клиенту все что идет после send
                        String str = new String();
                        for (int i=1;i<choise.length;i++){
                            str = str +choise[i] + " ";
                        }
                        mail = new Mail(str, Calendar.getInstance().getTime());
                        out.writeObject(mail);
                        break;

                    case ("logLevel"):  //после того как я полез в настройки IDEA и исправил ошибку не позволявшую использовать switch
                        break;          //у меня целиком перестал работать компилятор (invalid source release)
                                        //как исправлю, добавлю логгирование и нормальный connect-disconnect
                        
                    case ("quit"): //рвет связь с клиентом и отключает его
                        mail = new Mail("Application closed!", Calendar.getInstance().getTime());
                        out.writeObject(mail);
                        out.close();
                        break;
                    default: //на случай опечаток и нераспознанного ввода
                        mail = new Mail("No such command", Calendar.getInstance().getTime());
                        out.writeObject(mail);
                        out.close();
                        break;
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }
    public static void main(String[] args) {
        try {
            new UserIn();
            }catch (Exception e){e.printStackTrace();}
        }
    }
