package com.pgbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FeedbackServlet")
public class FeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountHolderName = request.getParameter("accountHolderName");
        String accountNumber = request.getParameter("accountNumber");
        String suggestions = request.getParameter("suggestions");

        Connection conn = null;
        PreparedStatement pstmt = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/peoplegobanks", "root", "admin");

            // Insert feedback data into the database
            String insertFeedbackQuery = "INSERT INTO feedback (accountHolderName, accountNumber, suggestions) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(insertFeedbackQuery);
            pstmt.setString(1, accountHolderName);
            pstmt.setString(2, accountNumber);
            pstmt.setString(3, suggestions);
            pstmt.executeUpdate();

            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h1 { color: #1d3557; }");
            out.println("p { color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h1>Feedback Submitted Successfully!</h1>");
            out.println("<p>Thank you for your feedback, " + accountHolderName + ".</p>");
            out.println("</div>");
            out.println("</body></html>");
            
        

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h1 { color: #1d3557; }");
            out.println("p { color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h1>Error Submitting Feedback</h1>");
            out.println("<p>Please try again later.</p>");
            out.println("</div>");
            out.println("</body></html>");
            
        
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
