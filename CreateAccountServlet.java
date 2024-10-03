package com.pgbs;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/CreateAccountServlet")
@MultipartConfig(maxFileSize = 16177215) // 16MB
public class CreateAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String accountHolderName = request.getParameter("accountHolderName");
        String accountType = request.getParameter("accountType");
        double balance = Double.parseDouble(request.getParameter("balance"));
        double minBalance = Double.parseDouble(request.getParameter("minBalance"));
        String gender = request.getParameter("gender");
        Part profileImagePart = request.getPart("profileImage");
        String pan = request.getParameter("pan");
        String aadhaar = request.getParameter("aadhaar");

        if (pan == null || pan.isEmpty()) {
            pan = "Not Provided";
        }
        if (aadhaar == null || aadhaar.isEmpty()) {
            aadhaar = "Not Provided";
        }

        InputStream profileImageInputStream = null;
        if (profileImagePart != null) {
            profileImageInputStream = profileImagePart.getInputStream();
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/peoplegobanks", "root", "admin");

            String query = "INSERT INTO accounts (accountNumber, username, password, accountHolderName, accountType, balance, minBalance, gender, profileImage, pan, aadhaar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, accountHolderName);
            pstmt.setString(5, accountType);
            pstmt.setDouble(6, balance);
            pstmt.setDouble(7, minBalance);
            pstmt.setString(8, gender);
            if (profileImageInputStream != null) {
                pstmt.setBlob(9, profileImageInputStream);
            } else {
                pstmt.setNull(9, java.sql.Types.BLOB);
            }
            pstmt.setString(10, pan);
            pstmt.setString(11, aadhaar);

            // Execute the insert statement
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("<html><body>");
                out.println("<h1>Account created successfully!</h1>");
                out.println("</body></html>");
            } else {
                out.println("<html><body>");
                out.println("<h1>Error: Account creation failed.</h1>");
                out.println("</body></html>");
            }

            if (!pan.equals("Not Provided") || !aadhaar.equals("Not Provided")) {
                String validationQuery = "INSERT INTO user_validation (username, pan, aadhaar) VALUES (?, ?, ?)";
                try (PreparedStatement validationPstmt = conn.prepareStatement(validationQuery)) {
                    validationPstmt.setString(1, username);
                    validationPstmt.setString(2, pan);
                    validationPstmt.setString(3, aadhaar);
                    validationPstmt.executeUpdate();
                }
            }

            // Display success message and account details in a table
            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: auto; overflow: hidden; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
            out.println("table, th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }");
            out.println("th { background-color: #a8dadc; color: #1d3557; }");
            out.println("td { background-color: #f1faee; color: #1d3557; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Account Created Successfully</h2>");
            out.println("<table>");
            out.println("<tr><th>Field</th><th>Value</th></tr>");
            out.println("<tr><td>Account Number</td><td>" + accountNumber + "</td></tr>");
            out.println("<tr><td>Username</td><td>" + username + "</td></tr>");
            out.println("<tr><td>Account Holder Name</td><td>" + accountHolderName + "</td></tr>");
            out.println("<tr><td>Account Type</td><td>" + accountType + "</td></tr>");
            out.println("<tr><td>Balance</td><td>" + balance + "</td></tr>");
            out.println("<tr><td>Minimum Balance</td><td>" + minBalance + "</td></tr>");
            out.println("<tr><td>Gender</td><td>" + gender + "</td></tr>");
            out.println("<tr><td>PAN</td><td>" + pan + "</td></tr>");
            out.println("<tr><td>Aadhaar</td><td>" + aadhaar + "</td></tr>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body></html>");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("<html><body>");
            out.println("<h1>Error loading JDBC driver: " + e.getMessage() + "</h1>");
            out.println("</body></html>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<html><body>");
            out.println("<h1>SQL Error: " + e.getMessage() + "</h1>");
            out.println("</body></html>");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}