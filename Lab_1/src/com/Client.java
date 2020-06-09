import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class Client { //главный класс Клиента
    
    private Socket socket;
    private BufferedReader in; // чтения из потока
    private BufferedWriter out; // записывает текст в поток вывода символов
    private BufferedReader inputUser; // поток чтения с консоли
    private String adres; // ip адрес клиента
    private int port; // порт соединения
    private String name; // имя клиента
    private Date time; //время
    private String datatime;
    private SimpleDateFormat dt1;
    
    /* для создания необходимо принять адрес и номер порта */
    
    public Client(String adres, int port) {
        this.adres = adres;
        this.port = port;
        try { 			//определяет блок кода, в котором может произойти исключение;
            this.socket = new Socket(adres, port);
        } catch (IOException e) { //определяет блок кода, в котором происходит обработка исключения;// пишем обработку исключения при закрытии потока чтения
            System.err.println("Ошибка сокета"); //вывод ошибки
        }
        try { // при закрытии потока тоже возможно исключение, например, если он не был открыт, поэтому помещаем код в блок try
            // потоки чтения из сокета / записи в сокет, и чтения с консоли
            inputUser = new BufferedReader(new InputStreamReader(System.in)); //System.in входящий поток, для получения данных с клавиатуры
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.pressName(); // перед началом необходимо спросит имя
            new ChtenieSms().start(); // нить читающая сообщения из сокета в бесконечном цикле
            new ZapisSms().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле
        } catch (IOException e) {
            // Сокет должен быть закрыт при любой ошибке, кроме ошибки конструктора сокета
            Client.this.downService();
        }
        // В противном случае сокет будет закрыт в методе run.
    }
    
    /* просьба ввести имя, и приветствие на сервере */
    
    private void pressName() {
        System.out.print("Введите ваш ник: "); // поток для отправки данных на консоль
        try {
            name = inputUser.readLine();
            out.write("Приветсвую " + name + "\n");
            out.flush();  //передать данные из буфера во Writer. Флаш используется, чтобы принудительно записать в целевой поток данные, которые могут кэшироваться в текущем потоке
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
    private class ChtenieSms extends Thread {
        @Override
        public void run() {
            
            String str;
            try {
                while (true) {
                    str = in.readLine(); // ждем сообщения с сервера
                    if (str.equals("stop")) {
                        Client.this.downService(); // откл обслуживания
                        break; // выходим из цикла если пришло "stop"
                    }
                    System.out.println(str); // пишем сообщение с сервера на консоль
                }
            } catch (IOException e) {
                Client.this.downService(); // откл обслуживания
            }
        }
    }
    
    // нить отправляющая сообщения приходящие с консоли на сервер
    public class ZapisSms extends Thread {
        
        @Override
        public void run() {
            while (true) {
                String clientSms;
                try {
                    time = new Date(); // текущая дата
                    dt1 = new SimpleDateFormat("чч:мм:сс"); // берем только время до секунд
                    datatime = dt1.format(time); // время
                    clientSms = inputUser.readLine(); // счесть сообщения с консоли
                    if (clientSms.equals("stop")) {  //прерывание цикла при написании строки стоп
                        out.write("stop" + "\n");
                        Client.this.downService(); // откл обслуживания
                        break; // выходим из цикла если пришло "stop"
                    } else {
                        out.write("(" + datatime + ") " + name + ": " + clientSms + "\n"); // отправляем на сервер время,ник и сообщение
                    }
                    out.flush(); // чистим и передаем данные из буфера во Writer
                } catch (IOException e) {
                    Client.this.downService(); // в случае исключения тоже останавливает обслуживание
                    
                }
                
            }
        }
    }
}

public class Client {
    
    public static String ipAdres = "localhost";
    public static int port = 8080;
    public static void main(String[] args) {
        new Client(ipAdres, port);
    }
}
