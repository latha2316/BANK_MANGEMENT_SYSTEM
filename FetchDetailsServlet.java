package com.pgbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
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
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/FetchDetailsServlet")
public class FetchDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FetchDetailsServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            logger.log(Level.INFO, "Servlet reached");

            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.log(Level.INFO, "JDBC driver loaded");

            // Establish the connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/peoplegobanks", "root", "admin");
            logger.log(Level.INFO, "Database connection established");

            // Fetch account details
            String accountQuery = "SELECT * FROM accounts WHERE accountNumber = ?";
            pstmt = conn.prepareStatement(accountQuery);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();
            logger.log(Level.INFO, "Account query executed");

            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
            out.println("table, th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }");
            out.println("th { background-color: #a8dadc; color: #1d3557; }");
            out.println("td { background-color: #f1faee; color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");

            if (rs.next()) {
                // Assuming the accounts table has columns: accountNumber, balance, accountHolderName, accountType, etc.
                String accountHolderName = rs.getString("accountHolderName");
                String accountType = rs.getString("accountType");
                double balance = rs.getDouble("balance");

                out.println("<h2>Account Details</h2>");
                out.println("<table>");
                out.println("<tr><th>Field</th><th>Value</th></tr>");
                out.println("<tr><td>Account Number</td><td>" + accountNumber + "</td></tr>");
                out.println("<tr><td>Account Holder Name</td><td>" + accountHolderName + "</td></tr>");
                out.println("<tr><td>Account Type</td><td>" + accountType + "</td></tr>");
                out.println("<tr><td>Balance</td><td>" + balance + "</td></tr>");

                // Check if the creationDate column exists
                try {
                    Date creationDate = rs.getDate("creationDate");
                    out.println("<tr><td>Creation Date</td><td>" + creationDate + "</td></tr>");
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Column 'creationDate' not found");
                }

                out.println("</table>");

                // Fetch transaction details
                String transactionQuery = "SELECT * FROM transactions WHERE accountNumber = ?";
                pstmt = conn.prepareStatement(transactionQuery);
                pstmt.setString(1, accountNumber);
                rs = pstmt.executeQuery();
                logger.log(Level.INFO, "Transaction query executed");

                out.println("<h2>Transaction History</h2>");
                out.println("<table>");
                out.println("<tr><th>Transaction ID</th><th>Type</th><th>Amount</th><th>Date</th></tr>");
                while (rs.next()) {
                    int transactionID = rs.getInt("transactionID");
                    String transactionType = rs.getString("transactionType");
                    double amount = rs.getDouble("amount");
                    Timestamp transactionDate = rs.getTimestamp("transactionDate");
                    out.println("<tr><td>" + transactionID + "</td><td>" + transactionType + "</td><td>" + amount + "</td><td>" + transactionDate + "</td></tr>");
                }
                out.println("</table>");
                
             
            } else {
                out.println("<p>No account found with the given account number.</p>");
            }

            out.println("</div>");
            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("</div>");
            out.println("</body></html>");
            
         
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
