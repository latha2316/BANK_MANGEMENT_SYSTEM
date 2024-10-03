package com.pgbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String transactionType = request.getParameter("transactionType");
        String fromAccount = request.getParameter("fromAccount");
        String toAccount = request.getParameter("toAccount");
        String accountNumber = request.getParameter("accountNumber");
        double amount = Double.parseDouble(request.getParameter("amount"));

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/peoplegobanks", "root", "admin");

            // Start transaction
            conn.setAutoCommit(false);

            if ("deposit".equals(transactionType)) {
                // Deposit transaction
                String updateBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(updateBalanceQuery);
                pstmt.setDouble(1, amount);
                pstmt.setString(2, accountNumber);
                pstmt.executeUpdate();

                String insertTransactionQuery = "INSERT INTO transactions (accountNumber, transactionType, amount, transactionDate) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertTransactionQuery);
                pstmt.setString(1, accountNumber);
                pstmt.setString(2, "deposit");
                pstmt.setDouble(3, amount);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                // Get the updated balance
                String getBalanceQuery = "SELECT balance FROM accounts WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(getBalanceQuery);
                pstmt.setString(1, accountNumber);
                rs = pstmt.executeQuery();
                double totalBalance = 0;
                if (rs.next()) {
                    totalBalance = rs.getDouble("balance");
                }

                out.println("<html><head><style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
                out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
                out.println("h1 { color: #1d3557; }");
                out.println("p { color: #1d3557; }");
                out.println("</style></head><body>");
                out.println("<div class='container'>");
                out.println("<h1>Deposit Successful!</h1>");
                out.println("<p>Amount Deposited: " + amount + "</p>");
                out.println("<p>Account Number: " + accountNumber + "</p>");
                out.println("<p>Total Balance: " + totalBalance + "</p>");
                out.println("</div>");
                out.println("</body></html>");

            } else if ("withdrawal".equals(transactionType)) {
                // Withdrawal transaction
                String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(updateBalanceQuery);
                pstmt.setDouble(1, amount);
                pstmt.setString(2, accountNumber);
                pstmt.executeUpdate();

                String insertTransactionQuery = "INSERT INTO transactions (accountNumber, transactionType, amount, transactionDate) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertTransactionQuery);
                pstmt.setString(1, accountNumber);
                pstmt.setString(2, "withdrawal");
                pstmt.setDouble(3, amount);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                // Get the updated balance
                String getBalanceQuery = "SELECT balance FROM accounts WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(getBalanceQuery);
                pstmt.setString(1, accountNumber);
                rs = pstmt.executeQuery();
                double totalBalance = 0;
                if (rs.next()) {
                    totalBalance = rs.getDouble("balance");
                }

                out.println("<html><head><style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
                out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
                out.println("h1 { color: #1d3557; }");
                out.println("p { color: #1d3557; }");
                out.println("</style></head><body>");
                out.println("<div class='container'>");
                out.println("<h1>Withdrawal Successful!</h1>");
                out.println("<p>Amount Withdrawn: " + amount + "</p>");
                out.println("<p>Account Number: " + accountNumber + "</p>");
                out.println("<p>Total Balance: " + totalBalance + "</p>");
                out.println("</div>");
                out.println("</body></html>");

            } else if ("transfer".equals(transactionType)) {
                // Transfer transaction
                // Withdraw from the sender's account
                String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(withdrawQuery);
                pstmt.setDouble(1, amount);
                pstmt.setString(2, fromAccount);
                pstmt.executeUpdate();

                // Deposit into the receiver's account
                String depositQuery = "UPDATE accounts SET balance = balance + ? WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(depositQuery);
                pstmt.setDouble(1, amount);
                pstmt.setString(2, toAccount);
                pstmt.executeUpdate();

                // Insert transaction record for sender
                String insertSenderTransactionQuery = "INSERT INTO transactions (accountNumber, transactionType, amount, transactionDate) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertSenderTransactionQuery);
                pstmt.setString(1, fromAccount);
                pstmt.setString(2, "transfer-out");
                pstmt.setDouble(3, amount);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                // Insert transaction record for receiver
                String insertReceiverTransactionQuery = "INSERT INTO transactions (accountNumber, transactionType, amount, transactionDate) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertReceiverTransactionQuery);
                pstmt.setString(1, toAccount);
                pstmt.setString(2, "transfer-in");
                pstmt.setDouble(3, amount);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                out.println("<html><head><style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
                out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
                out.println("h1 { color: #1d3557; }");
                out.println("p { color: #1d3557; }");
                out.println("</style></head><body>");
                out.println("<div class='container'>");
                out.println("<h1>Transfer Successful!</h1>");
                out.println("<p>Amount Transferred: " + amount + "</p>");
                out.println("<p>From Account: " + fromAccount + "</p>");
                out.println("<p>To Account: " + toAccount + "</p>");
                out.println("</div>");
                out.println("</body></html>");
            }

            // Commit transaction
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h1 { color: #1d3557; }");
            out.println("p { color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h1>Error occurred: " + e.getMessage() + "</h1>");
            out.println("</div>");
            out.println("</body></html>");
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
