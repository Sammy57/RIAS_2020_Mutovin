//https://www.sites.google.com/site/resheto131/loggers-1/log4j/logger //источник/подсказка
/* https://askdev.ru/q/kakovo-ispolzovanie-metoda-printstacktrace-v-java-30204/ */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.loger4j.*;

public class Client {
    public static int PORT = 8080;
    Logger loger = Logger.getLogger(); //подключаем логи
	public  static Socket socket = null;
    public static void main(String[] args)throws IOException, InterruptedException { //Метод блокирования
        new Client().runi();
    }

    public void runi() {
        loger.info("Порт сервера: " + PORT);
        try {
            ServerSocket server = new ServerSocket(PORT); //порт на котором запустится сервер
            System.out.println("Сервер запущен.");

            while (true) {
                Socket clientSocket = server.accept();  //прием запроса на подключения от клиента
                Thread thread = new Thread(new ClntRdr(clientSocket));
                loger.info("клиент принят " + clientSocket.toString());
                loger.info("ID клиента" + thread.getId());
                thread.start(); //запускаем поток
            }
        } catch (Exception e) {
            e.printStackTrace();
            loger.error(e);
        }
    }

    class ClntRdr implements Runnable {

        BufferedReader breader;// поток чтения с консоли
        Socket userSoket;

        ClntRdr(Socket clientSocket) {
            try {
                userSoket = clientSocket;
                breader = new BufferedReader(new InputStreamReader(userSoket.getInputStream())); //создаем поток чтения из сокета клиента
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String sms;
            try {
                while (!userSoket.isClosed()) { //слушаем пока клиент не напишит
                    sms = breader.readLine();
                    String[] comand =  sms.split(" ");
                    switch (comand[0]){
                        case "disconnect":
                            loger.debug("клиент отключен" + userSoket.toString());
                            multi("disconnect");
                            breader.close();
                            userSoket.close();
                            break;
                        case "send":
                            loger.info("клиент написал "+sms.substring(5));
                            multi(sms.substring(5));
							loger.flush();
                            break;
                        case "logerLevel":
                            loger.info("set Level" + comand[1]); /*Метод setLevel () класса Logger, используемый для установки уровня журнала, чтобы описать, какие уровни сообщений будут регистрироваться этим средством регистрации*/
                            switch (comand[1]){
								case "all":
                                    loger.setLevel(Level.All);
                                    break;
								case "debug":
                                    loger.setLevel(Level.Debug);
                                    break;
								case "info":
                                    loger.setLevel(Level.Info);
                                    break;	
								case "warn":
                                    loger.setLevel(Level.Warn);
                                    break;	
								case "error":
                                    loger.setLevel(Level.Error);
                                    break;
								case "fatal":
                                    loger.setLevel(Level.Fatal);
                                    break;	
                                case "off":
                                    loger.setLevel(Level.Off);
                                    break;
                                case "trace":
                                    loger.setLevel(Level.Trace);
                                    break;
                                default:
									help();
									break;
                            }
                            break;
						case "help":
							help();
							break;
						case "quit":
							if(sms.length()!=4){
								help();
								loger.setLevel("help" + "\n" );
								loger.flush();
							}
							else{
								loger.setLevel("disconnect" + "\n"); // отправляеncz на сервер
								out.flush();
								Client.CloseService();
								System.exit(0);
							}	
							break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
				e.printStackTrace(); //помогает программисту понять, где //возникла //фактическая проблема
            }
        }

        public void multi(String sms) {
            try {
                PrintWriter pw = new PrintWriter(userSoket.getOutputStream());
                pw.println(sms); //отправляем клиентy
                pw.flush(); //чистим
            }
            catch (Exception e)
            {
				System.out.println("Ошибка: " + e.getMessage());
				e.printStackTrace();
            }
        }
    }
}