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

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        Connection conn = null;
        PreparedStatement pstmt = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/peoplegobanks", "root", "admin");

            // Insert contact message data into the database
            String insertContactQuery = "INSERT INTO contact (name, email, subject, message) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertContactQuery);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, subject);
            pstmt.setString(4, message);
            pstmt.executeUpdate();

            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h1 { color: #1d3557; }");
            out.println("p { color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h1>Message Sent Successfully!</h1>");
            out.println("<p>Thank you for contacting us, " + name + ".</p>");
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
            out.println("<h1>Error Sending Message</h1>");
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
