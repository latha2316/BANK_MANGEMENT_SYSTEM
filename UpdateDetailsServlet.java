package com.pgbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateDetailsServlet")
public class UpdateDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String pan = request.getParameter("pan");
        String aadhaar = request.getParameter("aadhaar");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the validation database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/peoplegobanks", "root", "admin");

            // Check if PAN and Aadhaar details are already present
            String query = "SELECT * FROM user_validation WHERE username = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h2 { color: #1d3557; }");
            out.println("p { color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");

            if (rs.next()) {
                String existingPan = rs.getString("pan");
                String existingAadhaar = rs.getString("aadhaar");

                // Update PAN if missing
                if (existingPan == null || existingPan.isEmpty() || existingPan.equals("Not Provided")) {
                    existingPan = pan;
                }

                // Update Aadhaar if missing
                if (existingAadhaar == null || existingAadhaar.isEmpty() || existingAadhaar.equals("Not Provided")) {
                    existingAadhaar = aadhaar;
                }

                // Update the database with the new details
                String updateQuery = "UPDATE user_validation SET pan = ?, aadhaar = ? WHERE username = ?";
                pstmt = conn.prepareStatement(updateQuery);
                pstmt.setString(1, existingPan);
                pstmt.setString(2, existingAadhaar);
                pstmt.setString(3, username);
                int result = pstmt.executeUpdate();

                // Display result in browser
                if (result > 0) {
                    out.println("<h2>Details updated successfully!</h2>");
                    out.println("<p>Username: " + username + "</p>");
                    out.println("<p>PAN: " + existingPan + "</p>");
                    out.println("<p>Aadhaar: " + existingAadhaar + "</p>");
                } else {
                    out.println("<h2>Failed to update details.</h2>");
                }
            } else {
                out.println("<h2>Account not found.</h2>");
            }

            out.println("</div>");
            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h2 { color: #1d3557; }");
            out.println("p { color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
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
