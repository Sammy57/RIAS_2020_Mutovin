import java.io.Serializable;
import java.util.Date;

public class Mail implements Serializable{

    private Date date;
    private String mail;

    public Mail(String mail, Date date){
        this.date = date;
        this.mail = mail;
    }

    public Date getDate(){
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getMail(){
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
}
