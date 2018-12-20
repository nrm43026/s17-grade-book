package com.example.matth.gradebook.util;

import android.content.res.AssetManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.matth.gradebook.data.Assignment;
import com.example.matth.gradebook.data.Course;
import com.example.matth.gradebook.database.GradeBookDB;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Handles reading and writing XML
 * Created by Robert on 7/18/2017.
 */
public class XMLHandler {

    //CONSTANTS USED IN XML PARSING/CREATION
    public static final String COURSE = "course";
    public static final String COURSE_NAME = "courseName";
    public static final String ASSIGNMENT = "assignment";
    public static final String ASSIGNMENT_NAME = "name";
    public static final String ASSIGNMENT_DUE_DATE = "dueDate";
    public static final String ASSIGNMENT_POINTS_EARNED = "pointsEarned";
    public static final String ASSIGNMENT_POINTS_POSSIBLE = "pointsPossible";
    public static final String COURSE_FULL_NAME = "courseFullName";
    public static final String COURSES = "courses";

    /**
     * Loads the contents of an XML
     *
     * @return list of courses loaded and assignments in those courses
     */
    public static List<Course> loadFromXML(InputStream inputStream) {
        List<Course> list = new ArrayList();

        try {
            //Load contents
            Document doc = parseXML(inputStream);
            doc.getDocumentElement().normalize();

            //-----Parse contents----------
            //Get course nodes
            NodeList courseNodes = doc.getElementsByTagName(COURSE);
            for (int courseIndex = 0; courseIndex < courseNodes.getLength(); courseIndex++) {
                Node courseNode = courseNodes.item(courseIndex);

                String name = null;
                String nameFull = null;
                ArrayList<Assignment> assignments = new ArrayList();
                if (courseNode.hasChildNodes()) {
                    //Loop child nodes looking for data needed
                    NodeList cList = courseNode.getChildNodes();
                    for (int i = 0; i < cList.getLength(); i++) {

                        //Check node by name
                        Node node = cList.item(i);
                        if (node != null) {
                            String nodeName = node.getNodeName();

                            if (nodeName.equalsIgnoreCase(COURSE_NAME)) {
                                name = node.getTextContent();
                            } else if (nodeName.equalsIgnoreCase(COURSE_FULL_NAME)) {
                                nameFull = node.getTextContent();
                            }
                            //Handle assignment node
                            else if (nodeName.equalsIgnoreCase(ASSIGNMENT)) {

                                Assignment assignment = new Assignment();

                                //Loop assignment child nodes for data needed
                                NodeList aList = node.getChildNodes();
                                for (int j = 0; j < aList.getLength(); j++) {
                                    Node aNode = aList.item(j);

                                    if (aNode != null) {
                                        if (aNode.getNodeName().equalsIgnoreCase(ASSIGNMENT_NAME)) {
                                            assignment.setName(aNode.getTextContent());
                                        } else if (aNode.getNodeName().equalsIgnoreCase(ASSIGNMENT_DUE_DATE)) {
                                            //TODO parse date
                                            try {
                                                //DEMO DAY MOD parsing date, this is ugly.
                                                String dateString = aNode.getTextContent();
                                                String date[] = dateString.split(",");
                                                String date2[] = date[1].split(" ");
                                                int month = 0;
                                                if (date2[1].equalsIgnoreCase("Jan")) {
                                                    month = 1;
                                                }
                                                if (date2[1].equalsIgnoreCase("Feb")) {
                                                    month = 2;
                                                }
                                                if (date2[1].equalsIgnoreCase("Mar")) {
                                                    month = 3;
                                                }
                                                if (date2[1].equalsIgnoreCase("Apr")) {
                                                    month = 4;
                                                }
                                                if (date2[1].equalsIgnoreCase("May")) {
                                                    month = 5;
                                                }
                                                if (date2[1].equalsIgnoreCase("Jun")) {
                                                    month = 6;
                                                }
                                                if (date2[1].equalsIgnoreCase("Jul")) {
                                                    month = 7;
                                                }
                                                if (date2[1].equalsIgnoreCase("Aug")) {
                                                    month = 8;
                                                }
                                                if (date2[1].equalsIgnoreCase("Sep")) {
                                                    month = 9;
                                                }
                                                if (date2[1].equalsIgnoreCase("Oct")) {
                                                    month = 10;
                                                }
                                                if (date2[1].equalsIgnoreCase("Nov")) {
                                                    month = 11;
                                                }
                                                if (date2[1].equalsIgnoreCase("Dec")) {
                                                    month = 12;
                                                }
                                                String yearString[] = date[2].split(" ");
                                                int year = Integer.parseInt(yearString[1]);
                                                int day = Integer.parseInt(date2[2]);
                                                assignment.setDueDate(new Date(year - 1900, month, day));
                                            } catch (Exception e) {
                                                Log.e("XMLrw", "Unexpected error: " + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        } else if (aNode.getNodeName().equalsIgnoreCase(ASSIGNMENT_POINTS_EARNED)) {
                                            int points = 0;
                                            try {
                                                points = Integer.parseInt(aNode.getTextContent());
                                                //DEMO DAY MOD if score is parsed, assignment is graded -matt
                                                assignment.setGraded(true);
                                            } catch (NumberFormatException e) {
                                                //DEMO DAY MOD if no score, not graded -matt
                                                assignment.setGraded(false);
                                            }
                                            assignment.setPointsEarned(points);
                                        } else if (aNode.getNodeName().equalsIgnoreCase(ASSIGNMENT_POINTS_POSSIBLE)) {
                                            int points = 0;
                                            try {
                                                points = Integer.parseInt(aNode.getTextContent());
                                            } catch (NumberFormatException e) {

                                            }
                                            assignment.setPointsPossible(points);
                                        }
                                    }
                                }
                                if (assignment.getName() != null && !assignment.getName().isEmpty()) {
                                    //DEMO DAY MOD let the database assign the ID when it is inserted -matt
                                    //assignment.setAssignmentId(System.nanoTime());
                                    assignments.add(assignment);
                                } else {
                                    Log.e("XMLrw", "Import XML, failed to create assignment for '" + name + "' + as name is not valid. Name: " + assignment.getName());
                                    //TODO output error
                                }
                            }
                        }
                    }

                    if (name != null && nameFull != null && !name.isEmpty() && !nameFull.isEmpty()) {
                        Course course = new Course();
                        //DEMO DAY MOD let the database assign the ID when it is inserted -matt
                        //course.setCourseId(System.nanoTime());
                        //DEMO DAY MOD set course id so not null -matt
                        course.setCourseId(-1);
                        course.setName(name);
                        course.setFullName(nameFull);

                        course.setAssignments(assignments);
                        course.calcData();

                        list.add(course);
                    } else {
                        Log.e("XMLrw", "Import XML, failed to create class as name is not valid. Name: " + name + "  FullName: " + nameFull);
                        //TODO output error
                    }
                }
            }

        } catch (Exception e) {
            Log.e("XMLrw", e.getMessage()); //TODO see if there is a better way to log errors
            e.printStackTrace();
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * Loads the document from the source
     *
     * @param is
     * @return
     */
    public static Document parseXML(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Called to write the course data as an XML to the output stream
     *
     * @param stream - output
     * @return true if option didn't fail
     */
    public static boolean writeCourseData(GradeBookDB database, OutputStream stream) {

        String data = "test data";

        boolean good = false;
        try {
            Document doc = buildXML(database);

            TransformerFactory transformerfactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    transformerfactory.newTransformer();


            StreamResult result = new StreamResult(stream);
            transformer.transform(new DOMSource(doc), result);

            good = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Close stream
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return good;
    }

    public static Document buildXML(GradeBookDB database) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = parser.newDocument();

        Element root = doc.createElement(COURSES);
        doc.appendChild(root);

        for (Course course : database.getCourses()) {

            Element courseElement = doc.createElement(COURSE);

            //Name
            Element name = doc.createElement(COURSE_NAME);
            name.setTextContent(course.getName());
            courseElement.appendChild(name);

            //Full Name
            Element fullName = doc.createElement(COURSE_FULL_NAME);
            fullName.setTextContent(course.getFullName());
            courseElement.appendChild(fullName);

            //TODO export all data

            if (course.getAssignments() != null && !course.getAssignments().isEmpty()) {
                for (Assignment assignment : course.getAssignments()) {
                    Element assignmentElement = doc.createElement(ASSIGNMENT);

                    //Name
                    Element assignmentName = doc.createElement(ASSIGNMENT_NAME);
                    assignmentName.setTextContent(assignment.getName());
                    assignmentElement.appendChild(assignmentName);

                    //Due date
                    Element dueDate = doc.createElement(ASSIGNMENT_DUE_DATE);
                    //Build date            format: Sun, May 28, 2017
                    DateFormat df = new DateFormat();
                    CharSequence dataString = df.format("EEE, MMM dd, yyyy", assignment.getDueDate());
                    //Set to XML
                    dueDate.setTextContent(dataString.toString());
                    assignmentElement.appendChild(dueDate);

                    //Points earned
                    Element pointsEarned = doc.createElement(ASSIGNMENT_POINTS_EARNED);
                    pointsEarned.setTextContent("" + assignment.getPointsEarned());
                    assignmentElement.appendChild(pointsEarned);

                    //Points possible
                    Element pointsPossible = doc.createElement(ASSIGNMENT_POINTS_POSSIBLE);
                    pointsPossible.setTextContent("" + assignment.getPointsPossible());
                    assignmentElement.appendChild(pointsPossible);

                    courseElement.appendChild(assignmentElement);
                }
            }
            root.appendChild(courseElement);
        }

        return doc;
    }
}
