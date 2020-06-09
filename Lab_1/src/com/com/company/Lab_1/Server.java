import java.io.*;
import java.net.*;
import java.util.LinkedList;

class Server extends Thread { //главный класс сервака с расширенным потоком
    
    private Socket socket; // сокет, через который сервер общается с клиентом
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // записывает текст в поток сокета
    
    /*для общения с клиентом нужен сокет (адресные данные)  */
    
    public Server(Socket socket) throws IOException {
        this.socket = socket;
        // если потоки ввода/вывода приведут к генерированию исключения, оно перейдет дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));// чтения из потока
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// записывает текст в поток вывода символов
        start(); // вызываем run\запускаем
    }
    @Override
    public void run() {
        String sms;
        try {
            // первое сообщение отправленное сюда - это никнейм
            sms = in.readLine();
            try { //определяет блок кода, в котором может произойти исключение
                out.write(sms + "\n");
                out.flush(); // flush используется, чтобы принудительно записать в целевой поток данные, которые могут кэшироваться в текущем потоке
                // и если такие есть, то очищает
            } catch (IOException ignored) {}//определяет блок кода, в котором происходит обработка исключения
            try {
                while (true) {
                    sms = in.readLine();
                    if(sms.equals("stop")) { //прерывание цикла при написании строки стоп
                        this.downService(); // прерывание обслуживания
                        break; // пустая строка = выходим из цикла
                    }
                    System.out.println("Эхо: " + sms);
                    for (Server cl : Server.serverList) {
                        cl.send(sms); // отправка смс от одного клиента ко всем
                    }
                }
            } catch (NullPointerException ignored) {}  
        } catch (IOException e) {
            this.downService(); //откл
        }
    }
    
    /*отправка одного смс клиенту по указанному потоку mesg */
    private void send(String mesg) {
        try {
            out.write(mesg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    } 
    /* закрытие сервера,прерывание себя как нити и удаление из списка нитей */
    private void downService() {
            try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (Server vr : Server.serverList) {
                    if(vr.equals(this)) cl.interrupt();//обработчик прерывания
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}

public class Server {

    public static final int PORT = 8080;
    public static LinkedList<Server> serverList = new LinkedList<>(); // список всех нитей - экземпляров сервера, слушающих каждый своего клиента
    
    /* параментр args //throws IOException */
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Сервер запущен"); //вывод текста в консоль
        try {
            while (true) {
                // Блокируется до возникновения нового соединения
                Socket socket = server.accept();
                try {
                    serverList.add(new Server(socket)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет
                    socket.close();
                }
            }
        } finally {
            server.close(); // в противном случае, нить закроет его
        }
    }
}