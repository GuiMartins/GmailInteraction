package br.edu.unicarioca.gmailinteraction;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class CheckingMails {

    private static final String MAIL_FOLDER_NAME = "INBOX";
    private static final int MAIL_LIST_LIMIT = 10;

    private String host = "pop.gmail.com";// change accordingly
    private String mailStoreType = "pop3";
    private String username = "apsandersonbd@gmail.com";// change accordingly
    private String password = "apsanderson";// change accordingly
    private ArrayList<MailData> mailDataList = null;

    public CheckingMails() {
        this.mailDataList = check();
    }

    public ArrayList<MailData> getMailData() {
        return mailDataList;
    }

    public void setMailData(ArrayList<MailData> mailData) {
        this.mailDataList = mailData;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMailStoreType() {
        return mailStoreType;
    }

    public void setMailStoreType(String mailStoreType) {
        this.mailStoreType = mailStoreType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private ArrayList<MailData> check() {
        Debug.log("Requesitando lista de emails...");

        try {
            //create properties field
            Properties properties = new Properties();

            Debug.log("Setando propriedades...");

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getInstance(properties, new GmailAuthenticator(username, password));

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            Debug.log("Conectando...");

            store.connect(host, username, password);

            Debug.log("Abrindo caixa: " + MAIL_FOLDER_NAME);

            //create the folder object and open it
            Folder emailFolder = store.getFolder(MAIL_FOLDER_NAME);
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            Debug.log("Mensagens recebidas = " + messages.length);

            ArrayList<MailData> mDataList = new ArrayList();
            MailData mailData;

            for (Message message : messages) {
                mailData = new MailData();

                Address address = message.getFrom()[0];
                
                mailData.setFrom(address.toString());
                mailData.setSubject(message.getSubject());

                mDataList.add(mailData);

                if (mDataList.size() >= MAIL_LIST_LIMIT) {
                    break;
                }
            }

            Debug.log("Numero de email do vetor = " + mDataList.size());

            //close the store and folder objects
            emailFolder.close(false);
            store.close();

            return mDataList;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

//   public static void main(String[] args)
//   {
//    
//
//      check(host, mailStoreType, username, password);
//   }
