/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.unicarioca.gmailinteraction;

import com.mysql.jdbc.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guilherme
 */
public final class EmailDB {

    private static final String DATABASE_NAME = "gmail";
    private static final String TABLE_NAME = "inbox";
    private static final String COLUMN_FROM = "sender";
    private static final String COLUMN_SUBJECT = "subject";
    private static EmailDB instance = null;
    private Connection conn = null;
    private PreparedStatement pStmt = null;
    private String sql;

    private EmailDB() {
        try {
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE_NAME, "root", "");
        } catch (SQLException ex) {
            Logger.getLogger(EmailDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static EmailDB getInstance() {
        if (instance == null) {
            instance = new EmailDB();
        }

        return instance;
    }

    public void setup() {
        Debug.log("Setupando banco de dados...");

        try {
            DatabaseMetaData dbm = conn.getMetaData();
            // check if "employee" table is there
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME, null);
            if (tables.next()) {
                Debug.log("Tabela " + TABLE_NAME + " encontrada.");
            } else {
                Debug.log("Criando tabela " + TABLE_NAME);
                
                sql = "CREATE TABLE IF NOT EXISTS inbox (id int(10) NOT NULL, sender varchar(100) NOT NULL, subject varchar(100) NOT NULL);";
                pStmt = conn.prepareStatement(sql);
                pStmt.execute();

                sql = "ALTER TABLE inbox ADD PRIMARY KEY (id), ADD UNIQUE KEY id (id);";
                pStmt = conn.prepareStatement(sql);
                pStmt.execute();

                sql = "ALTER TABLE inbox MODIFY id int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;";
                pStmt = conn.prepareStatement(sql);
                pStmt.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<MailData> select() {
        ArrayList<MailData> mailDataList = new ArrayList();
        MailData mailData;
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + ";";

        try {
            ResultSet rs = select(conn, sqlQuery);

            while (rs.next()) {
                mailData = new MailData();
                mailData.setFrom(rs.getString(COLUMN_FROM));
                mailData.setSubject(rs.getString(COLUMN_SUBJECT));

                mailDataList.add(mailData);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mailDataList;
    }

    public void insert(ArrayList<MailData> mailDataList) {
        String sqlQuery = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_FROM + ", " + COLUMN_SUBJECT + ") VALUES (?, ?);";
        ArrayList<String> params = new ArrayList();

        for (int i = 0; i < mailDataList.size(); i++) {
            params.clear();
            params.add(mailDataList.get(i).getFrom());
            params.add(mailDataList.get(i).getSubject());

            try {
                insert(conn, sqlQuery, params);
            } catch (SQLException ex) {
                Logger.getLogger(EmailDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void clear() {
        sql = "TRUNCATE TABLE " + TABLE_NAME;
        try {
            delete(conn, sql, null);
        } catch (SQLException ex) {
            Logger.getLogger(EmailDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        pStmt = conn.prepareStatement(sql);

        Debug.log(sql);

        return pStmt.executeQuery();
    }

    private int executeUpdate(Connection conn, String sql, ArrayList params) throws SQLException {
        pStmt = conn.prepareStatement(sql);

        if (params != null) {
            for (int i = 1; i <= params.size(); i++) {
                pStmt.setString(i, (String) params.get(i - 1));
            }
        }

        Debug.log(sql);

        return pStmt.executeUpdate();
    }

    private void insert(Connection conn, String sql, ArrayList params) throws SQLException {
        executeUpdate(conn, sql, params);
    }

    private void delete(Connection conn, String sql, ArrayList params) throws SQLException {
        executeUpdate(conn, sql, params);
    }

    private void update(Connection conn, String sql, ArrayList params) throws SQLException {
        executeUpdate(conn, sql, params);
    }

    private ResultSet select(Connection conn, String sql) throws SQLException {
        return executeQuery(conn, sql);
    }
}
