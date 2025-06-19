import org.apache.fop.apps.*;
import org.apache.velocity.*;
import org.apache.velocity.app.*;
import java.sql.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/his_hu_step2";
        String user = "postgres";
        String password = "Southkorea121$";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Verbindung zur PostgreSQL-DB erfolgreich!");

            // SQL-Abfrage
            String sql = "SELECT s.student_id, s.name, s.email, e.grade " +
                       "FROM students s JOIN exams e ON s.student_id = e.student_id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                // Daten sammeln für Velocity
                List<Map<String, Object>> studentList = new ArrayList<>();
                StringWriter xmlWriter = new StringWriter();
                xmlWriter.write("<?xml version=\"1.0\"?>\n<students>\n");

                while (rs.next()) {
                    // Konsolenausgabe
                    System.out.println(
                        "ID: " + rs.getString("student_id") + ", " +
                        "Name: " + rs.getString("name") + ", " + 
                        "Email: " + rs.getString("email") + ", " + 
                        "Grade: " + rs.getFloat("grade")
                    );

                    // XML schreiben
                    xmlWriter.write(String.format(
                        "  <student id=\"%s\">\n    <name>%s</name>\n    <email>%s</email>\n    <grade>%s</grade>\n  </student>\n",
                        rs.getString("student_id"), rs.getString("name"), 
                        rs.getString("email"), rs.getFloat("grade")
                    ));

                    // Daten für Velocity vorbereiten
                    Map<String, Object> student = new HashMap<>();
                    student.put("id", rs.getString("student_id"));
                    student.put("name", rs.getString("name"));
                    student.put("email", rs.getString("email"));
                    student.put("grade", rs.getFloat("grade"));
                    studentList.add(student);
                }

                // XML speichern
                xmlWriter.write("</students>");
                String xmlOutput = xmlWriter.toString();
                Files.write(Paths.get("students.xml"), xmlOutput.getBytes());

                // PDF generieren
                generatePdf(xmlOutput);

                // HTML mit Velocity generieren
                generateHtml(studentList);
            }
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generatePdf(String xmlOutput) throws Exception {
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        
        try (OutputStream out = new FileOutputStream("students.pdf")) {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(
                new StreamSource(new File("students.xsl")));
            
            Source src = new StreamSource(new StringReader(xmlOutput));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
        }
    }

    private static void generateHtml(List<Map<String, Object>> studentList) throws Exception {
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        VelocityContext context = new VelocityContext();
        context.put("students", studentList);

        try (StringWriter writer = new StringWriter()) {
            Template template = ve.getTemplate("src/main/resources/template.vm");
            template.merge(context, writer);
            Files.write(Paths.get("output.html"), writer.toString().getBytes());
        }
    }
}