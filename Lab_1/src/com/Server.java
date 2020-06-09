import java.io.*;
import java.net.*;
import java.util.LinkedList;

/* проект реализует консольный многопользовательский чат. Вход в программу запуска сервера - в классе Server. */

class ServerSomthing extends Thread {
    
    private Socket socket; // сокет, через который сервер общается с клиентом, кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // записывает текст в поток сокета
    
    /*для общения с клиентом нужен сокет (адресные данные) //param socket //throws IOException */
    
    public ServerSomthing(Socket socket) throws IOException {
        this.socket = socket;
        // если потоки ввода/вывода приведут к генерированию исключения, оно перейдет дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));// чтения из потока
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// записывает текст в поток вывода символов
        Server.story.printStory(out); // поток вывода передаётся для передачи истории последних 10 сообщений новому подключению
        start(); // вызываем run()
    }
    @Override
    public void run() {
        String word;
        try {
            // первое сообщение отправленное сюда - это никнейм
            word = in.readLine();
            try { //определяет блок кода, в котором может произойти исключение;
                out.write(word + "\n");
                out.flush(); // flush() нужен для выталкивания оставшихся данных
                // если такие есть, и очистки потока для дальнейших нужд
            } catch (IOException ignored) {}//определяет блок кода, в котором происходит обработка исключения
            try {
                while (true) {
                    word = in.readLine();
                    if(word.equals("stop")) { //прерывание цикла при написании строки стоп
                        this.downService(); // прерывание обслуживания
                        break; // если пришла пустая строка - выходим из цикла
                    }
                    System.out.println("Echoing: " + word);
                    Server.story.addStoryEl(word);
                    for (ServerSomthing vr : Server.serverList) {
                        vr.send(word); // отправка принятого смс с привязанного клиента всем остальным 
                    }
                }
            } catch (NullPointerException ignored) {}

            
        } catch (IOException e) {
            this.downService();
        }
    }
    
    /*отсылка одного сообщения клиенту по указанному потоку //+param msg */
    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
        
    }
    
    /* закрытие сервера прерывание себя как нити и удаление из списка нитей */
    private void downService() {
            try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerSomthing vr : Server.serverList) {
                    if(vr.equals(this)) vr.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}

/* класс хранящий в ссылочном приватном списке информацию о последних 10  сообщениях */

class Story {
    
    private LinkedList<String> story = new LinkedList<>();
    
    /* эдд нью элмнт в списк парам e1*/
    
    public void addStoryEl(String el) {
        // если сообщений больше 10, удаляем первое и добавляем новое
     
        if (story.size() >= 10) {
            story.removeFirst();
            story.add(el);
        } else {
            story.add(el);    // иначе просто добавить
        }
    }
    
    /* отсылаем последовательно каждое сообщение из списка в поток вывода новому подключению, @param writer */
    
    public void printStory(BufferedWriter writer) {
        if(story.size() > 0) {
            try {
                writer.write("History messages" + "\n");
                for (String vr : story) {
                    writer.write(vr + "\n");
                }
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException ignored) {}
            
        }
        
    }
}

public class Server {

    public static final int PORT = 8080;
    public static LinkedList<ServerSomthing> serverList = new LinkedList<>(); // список всех нитей - экземпляров
    // сервера, слушающих каждый своего клиента
    public static Story story; // история переписки
    
    /* param args //throws IOException */
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        story = new Story();
        System.out.println("Server Started"); //вывод текста
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerSomthing(socket)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}