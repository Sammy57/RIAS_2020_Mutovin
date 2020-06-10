
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.log4j.Logger;


public class Server {

  private int port;
  private List<User> clients;
  private ServerSocket server;

  static Logger loger = Logger.getLogger(Server.class);
  public static void main(String[] args) throws IOException {
    new Server(1234).run();//запуск сервера поток
  }

  public Server(int port) {
    this.port = port;
    this.clients = new ArrayList<User>();
  }
//метод run запускает поток
  public void run() throws IOException {
    server = new ServerSocket(port) {
      protected void finalize() throws IOException {
        this.close();
      }
    };
    loger.info("Порт 1234 добавлен. Сервер запущен");
    while (true) {
      // принимает нового клиента
      Socket client = server.accept();

      loger.info("Клиент подключен");
      // создание нового пользователя
      User newUser = new User(client, "user");

      // добавить сообщение newUser в список
      this.clients.add(newUser);

      // создать новый поток для обработки входящих сообщений нового пользователя
      new Thread(new UserOchered(this, newUser)).start(); /*отправляется в очеред сообщение, и он в итоге обрабатывает сообщение вызывая метод run и передавая ему сообщение.*/
      loger.info("Создан новый поток для обработки входящих сообщений нового клиента");
    }
  }

  // удалить пользователя из списка
  public void removeUser(User user){
    this.clients.remove(user);
  }

  // отправить входящие сообщения всем пользователям
  public void broadcastSms(String msg) {
    for (User client : this.clients) {
      client.getOutStream().println(
        msg);
    }
  }


}

class UserOchered implements Runnable {

  private Server server;
  private User user;

  public UserOchered(Server server, User user) {
    this.server = server;
    this.user = user;

  }
  static Logger loger = Logger.getLogger(Server.class);
  public void run() {
    String sms;
    // когда появляется новое сообщение, транслируется всем
    Scanner vs = new Scanner(this.user.getInputStream());

    while (vs.hasNextLine()) {
      sms = vs.nextLine();
        server.broadcastSms(sms);
        loger.info("Получено sms для клиента: "+ sms);
    }
    //завершение потока
    server.removeUser(user);
    vs.close();
    loger.info("Сканер закрыт и удален клиент");
  }
}

class User {
  private static int nbUser = 0;
  private int userId;
  private PrintStream streamOut;
  private InputStream streamIn;
  private String nick;
  private Socket client;


  public User(Socket client, String name) throws IOException {
    this.streamOut = new PrintStream(client.getOutputStream());
    this.streamIn = client.getInputStream();
    this.client = client;
    this.nick = name;
    this.userId = nbUser;
     nbUser += 1;
  }

  public PrintStream getOutStream(){
    return this.streamOut;
  }

  public InputStream getInputStream(){
    return this.streamIn;
  }

  public String getNick(){
    return this.nick;
  }

  public String toString(){
    return  this.getNick();
  }
}


