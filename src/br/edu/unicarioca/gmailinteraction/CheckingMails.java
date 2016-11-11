package br.edu.unicarioca.gmailinteraction;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;

public class CheckingMails {

    private static final String MAIL_FOLDER_NAME = "INBOX";
    private static final int MAIL_LIST_LIMIT = 10;

    private String host = "pop.gmail.com";
    private String mailStoreType = "pop3";
    private String username;
    private String password;
    private ArrayList<MailData> mailDataList = null;

    public CheckingMails() {
        this.username = "apsandersondb@gmail.com";
        this.password = "apsanderson";
        this.mailDataList = check();
    }

    public CheckingMails(String username, String password) {
        this.username = username;
        this.password = password;
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
        try {
            Properties properties = new Properties();

            Debug.log("Setando propriedades...");

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getInstance(properties, new GmailAuthenticator(username, password));

            Store store = emailSession.getStore("pop3s");

            Debug.log("Conectando...");

            store.connect(host, username, password);

            Debug.log("Abrindo caixa: " + MAIL_FOLDER_NAME);

            Folder emailFolder = store.getFolder(MAIL_FOLDER_NAME);
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            Debug.log("Mensagens recebidas = " + messages.length);

            ArrayList<MailData> mDataList = new ArrayList();
            MailData mailData;

            for (Message message : messages) {
                mailData = new MailData();

                Address address = message.getFrom()[0];

                mailData.setFrom(address.toString());
                mailData.setSubject(message.getSubject());

                if (message.getContent() instanceof MimeMultipart) {
                    Debug.log("Content eh um Mime.");
                    mailData.setContent(getTextFromMimeMultipart((MimeMultipart) message.getContent()));
                } else {
                    Debug.log("Content nao eh um Mime.");
                    mailData.setContent(message.getContent().toString());
                }

                mDataList.add(mailData);

                // apenas pra teste, meio lentinho esse troço...
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

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
