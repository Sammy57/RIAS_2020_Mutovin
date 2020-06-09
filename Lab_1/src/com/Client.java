import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**создание клиента со всеми необходимыми утилитами, вход в программу Client*/

class ClientSomthing {
    
    private Socket socket;
    private BufferedReader in; // чтения из потока
    private BufferedWriter out; // записывает текст в поток вывода символов
    private BufferedReader inputUser; // поток чтения с консоли
    private String addr; // ip адрес клиента
    private int port; // порт соединения
    private String nickname; // имя клиента
    private Date time; //время
    private String dtime;
    private SimpleDateFormat dt1;
    
    /* для создания необходимо принять адрес и номер порта @param addr @param port */
    
    public ClientSomthing(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try { 			//определяет блок кода, в котором может произойти исключение;
            this.socket = new Socket(addr, port);
        } catch (IOException e) { //определяет блок кода, в котором происходит обработка исключения;
            System.err.println("Socket failed");
        }
        try { // при закрытии потока тоже возможно исключение, например, если он не был открыт, поэтому “оборачиваем” код в блок try
            // потоки чтения из сокета / записи в сокет, и чтения с консоли
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.pressNickname(); // перед началом необходимо спросит имя
            new ReadMsg().start(); // нить читающая сообщения из сокета в бесконечном цикле
            new WriteMsg().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле
        } catch (IOException e) {
            // Сокет должен быть закрыт при любой
            // ошибке, кроме ошибки конструктора сокета:
            ClientSomthing.this.downService();
        }
        // В противном случае сокет будет закрыт
        // в методе run() нити.
    }
    
    /* просьба ввести имя, и приветсвие на сервере */
    
    private void pressNickname() {
        System.out.print("Press your nick: ");
        try {
            nickname = inputUser.readLine();
            out.write("Hello " + nickname + "\n");
            out.flush();  //передать данные из буфера во Writer
        } catch (IOException ignored) {
        }
        
    }
    
    /* закрытие сокета */

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close(); //закрыть поток
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }
    
    // нить чтения сообщений с сервера
    private class ReadMsg extends Thread {
        @Override
        public void run() {
            
            String str;
            try {
                while (true) {
                    str = in.readLine(); // ждем сообщения с сервера
                    if (str.equals("stop")) {
                        ClientSomthing.this.downService(); // откл обслуживания
                        break; // выходим из цикла если пришло "stop"
                    }
                    System.out.println(str); // пишем сообщение с сервера на консоль
                }
            } catch (IOException e) {
                ClientSomthing.this.downService(); // откл обслуживания
            }
        }
    }
    
    // нить отправляющая сообщения приходящие с консоли на сервер
    public class WriteMsg extends Thread {
        
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    time = new Date(); // текущая дата
                    dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
                    dtime = dt1.format(time); // время
                    userWord = inputUser.readLine(); // счесть сообщения с консоли
                    if (userWord.equals("stop")) {  //прерывание цикла при написании строки стоп
                        out.write("stop" + "\n");
                        ClientSomthing.this.downService(); // откл обслуживания
                        break; // выходим из цикла если пришло "stop"
                    } else {
                        out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n"); // отправляем на сервер
                    }
                    out.flush(); // чистим и передаем данные из буфера во Writer
                } catch (IOException e) {
                    ClientSomthing.this.downService(); // в случае исключения тоже останавливает обслуживание
                    
                }
                
            }
        }
    }
}

public class Client {
    
    public static String ipAddr = "localhost";
    public static int port = 8080;
    
    /*создание клиент-соединения с узананными адресом и номером порта @param args */
    
    public static void main(String[] args) {
        new ClientSomthing(ipAddr, port);
    }
}